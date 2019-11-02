/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.freeman.common.utils.excel;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.freeman.common.reflect.ReflectUtils;
import com.freeman.common.utils.StrUtil;
import com.freeman.common.utils.excel.annotation.ExcelField;
import com.freeman.common.utils.excel.annotation.ExcelField.Align;
import com.freeman.common.utils.excel.annotation.ExcelField.Type;
import com.freeman.common.utils.excel.annotation.ExcelFields;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 导出Excel文件（导出“XLSX”格式，支持大数据量导出   @see org.apache.poi.ss.SpreadsheetVersion）
 * @author ThinkGem
 * @version 2013-04-21
 */
public class ExcelExport {
	
	private static Logger log = LoggerFactory.getLogger(ExcelExport.class);
			
	/**
	 * 工作薄对象
	 */
	private Workbook wb;
	
	/**
	 * 工作表对象
	 */
	private Sheet sheet;
	
	/**
	 * 样式列表
	 */
	private Map<String, CellStyle> styles;
	
	/**
	 * 当前行号
	 */
	private int rownum;
	
	/**
	 * 注解列表（Object[]{ ExcelField, Field/Method }）
	 */
	List<Object[]> annotationList;
	
	/**
	 * 构造函数
	 * @param title 表格标题，传“空值”，表示无标题
	 * @param cls 实体对象，通过annotation.ExportField获取标题
	 */
	public ExcelExport(String title, Class<?> cls){
		this(title, cls, ExcelField.Type.EXPORT);
	}
	
	/**
	 * 构造函数
	 * @param title 表格标题，传“空值”，表示无标题
	 * @param cls 实体对象，通过annotation.ExportField获取标题
	 * @param type 导出类型（1:导出数据；2：导出模板）
	 * @param groups 导入分组
	 */
	public ExcelExport(String title, Class<?> cls, Type type, String... groups){
		this(null, null, title, cls, type, groups);
	}
	
	/**
	 * 构造函数
	 * @param sheetName 指定Sheet名称
	 * @param title 表格标题，传“空值”，表示无标题
	 * @param cls 实体对象，通过annotation.ExportField获取标题
	 * @param type 导出类型（1:导出数据；2：导出模板）
	 * @param groups 导入分组
	 */
	public ExcelExport(String sheetName, String title, Class<?> cls, Type type, String... groups){
		this(null, sheetName, title, cls, type, groups);
	}
	
	/**
	 * 构造函数
	 * @param wb 指定现有工作簿对象
	 * @param sheetName 指定Sheet名称
	 * @param title 表格标题，传“空值”，表示无标题
	 * @param cls 实体对象，通过annotation.ExportField获取标题
	 * @param type 导出类型（1:导出数据；2：导出模板）
	 * @param groups 导入分组
	 */
	public ExcelExport(Workbook wb, String sheetName, String title, Class<?> cls, Type type, String... groups){
		if (wb != null){
			this.wb = wb;
		}else{
			this.wb = createWorkbook();
		}
		this.createSheet(sheetName, title, cls, type, groups);
	}
	
	/**
	 * 构造函数
	 * @param title 表格标题，传“空值”，表示无标题
	 * @param headerList 表头数组
	 */
	public ExcelExport(String title, List<String> headerList, List<Integer> headerWidthList) {
		this(null, null, title, headerList, headerWidthList);
	}
	
	/**
	 * 构造函数
	 * @param sheetName 指定Sheet名称
	 * @param title 表格标题，传“空值”，表示无标题
	 * @param headerList 表头数组
	 */
	public ExcelExport(String sheetName, String title, List<String> headerList, List<Integer> headerWidthList) {
		this(null, sheetName, title, headerList, headerWidthList);
	}
	
	/**
	 * 构造函数
	 * @param wb 指定现有工作簿对象
	 * @param sheetName，指定Sheet名称
	 * @param title 表格标题，传“空值”，表示无标题
	 * @param headerList 表头列表
	 */
	public ExcelExport(Workbook wb, String sheetName, String title, List<String> headerList, List<Integer> headerWidthList) {
		if (wb != null){
			this.wb = wb;
		}else{
			this.wb = createWorkbook();
		}
		this.createSheet(sheetName, title, headerList, headerWidthList);
	}
	
	/**
	 * 创建一个工作簿
	 */
	public Workbook createWorkbook(){
		return new SXSSFWorkbook(500);
	}

	/**
	 * 获取当前工作薄
	 * @author ThinkGem
	 */
	public Workbook getWorkbook() {
		return wb;
	}
	
	/**
	 * 创建工作表
	 * @param sheetName，指定Sheet名称
	 * @param title 表格标题，传“空值”，表示无标题
	 * @param cls 实体对象，通过annotation.ExportField获取标题
	 * @param type 导出类型（1:导出数据；2：导出模板）
	 * @param groups 导入分组
	 */
	public void createSheet(String sheetName, String title, Class<?> cls, Type type, String... groups){
		this.annotationList = CollectionUtil.newArrayList();
		// Get annotation field
		Field[] fs = cls.getDeclaredFields();
		for (Field f : fs){
			ExcelFields efs = f.getAnnotation(ExcelFields.class);
			if (efs != null && efs.value() != null){
				for (ExcelField ef : efs.value()){
					addAnnotation(annotationList, ef, f, type, groups);
				}
			}
			ExcelField ef = f.getAnnotation(ExcelField.class);
			addAnnotation(annotationList, ef, f, type, groups);
		}
		// Get annotation method
		Method[] ms = cls.getDeclaredMethods();
		for (Method m : ms){
			ExcelFields efs = m.getAnnotation(ExcelFields.class);
			if (efs != null && efs.value() != null){
				for (ExcelField ef : efs.value()){
					addAnnotation(annotationList, ef, m, type, groups);
				}
			}
			ExcelField ef = m.getAnnotation(ExcelField.class);
			addAnnotation(annotationList, ef, m, type, groups);
		}
		// Field sorting
		Collections.sort(annotationList, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				return new Integer(((ExcelField)o1[0]).sort()).compareTo(
						new Integer(((ExcelField)o2[0]).sort()));
			};
		});
		// Initialize
		List<String> headerList = CollectionUtil.newArrayList();
		List<Integer> headerWidthList = CollectionUtil.newArrayList();
		for (Object[] os : annotationList){
			ExcelField ef = (ExcelField)os[0];
			String headerTitle = ef.title();
			// 如果是导出，则去掉注释
			if (type == Type.EXPORT){
				String[] ss = StrUtil.split(headerTitle, "**");
				if (ss.length == 2){
					headerTitle = ss[0];
				}
			}
			headerList.add(headerTitle);
			headerWidthList.add(ef.width());
		}
		// 创建工作表
		this.createSheet(sheetName, title, headerList, headerWidthList);
	}
	
	/**
	 * 添加到 annotationList
	 */
	private void addAnnotation(List<Object[]> annotationList, ExcelField ef, Object fOrM, Type type, String... groups){
//		if (ef != null && (ef.type()==0 || ef.type()==type)){
		if (ef != null && (ef.type() == Type.ALL || ef.type() == type)){
			if (groups != null && groups.length > 0){
				boolean inGroup = false;
				for (String g : groups){
					if (inGroup){
						break;
					}
					for (String efg : ef.groups()){
						if (StrUtil.equals(g, efg)){
							inGroup = true;
							annotationList.add(new Object[]{ef, fOrM});
							break;
						}
					}
				}
			}else{
				annotationList.add(new Object[]{ef, fOrM});
			}
		}
	}
	
	/**
	 * 创建工作表
	 * @param sheetName 指定Sheet名称
	 * @param title 表格标题，传“空值”，表示无标题
	 * @param headerList 表头字段设置
	 * @param headerWidthList 表头字段宽度设置
	 */
	public void createSheet(String sheetName, String title, List<String> headerList, List<Integer> headerWidthList) {
		this.sheet = wb.createSheet(StrUtil.blankToDefault(sheetName, StrUtil.blankToDefault(title, "Sheet1")));
		this.styles = createStyles(wb);
		this.rownum = 0;
		// Create title
		if (StrUtil.isNotBlank(title)){
			Row titleRow = sheet.createRow(rownum++);
			titleRow.setHeightInPoints(30);
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellStyle(styles.get("title"));
			titleCell.setCellValue(title);
			sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(),
					titleRow.getRowNum(), titleRow.getRowNum(), headerList.size()-1));
		}
		// Create header
		if (headerList == null){
			throw new ExcelException("headerList not null!");
		}
		Row headerRow = sheet.createRow(rownum++);
		headerRow.setHeightInPoints(16);
		for (int i = 0; i < headerList.size(); i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellStyle(styles.get("header"));
			String[] ss = StrUtil.split(headerList.get(i), "**");
			if (ss.length==2){
				cell.setCellValue(ss[0]);
				Comment comment = this.sheet.createDrawingPatriarch().createCellComment(
						new XSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 5, 6));
				comment.setRow(cell.getRowIndex());
				comment.setColumn(cell.getColumnIndex());
				comment.setString(new XSSFRichTextString(ss[1]));
				cell.setCellComment(comment);
			}else{
				cell.setCellValue(headerList.get(i));
			}
//			sheet.autoSizeColumn(i);
		}
		boolean isDefWidth = (headerWidthList != null && headerWidthList.size() == headerList.size());
		for (int i = 0; i < headerList.size(); i++) {
			int colWidth = -1;
			if (isDefWidth){
				colWidth = headerWidthList.get(i);
			}
			if (colWidth == -1){
				colWidth = sheet.getColumnWidth(i)*2;
				colWidth = colWidth < 3000 ? 3000 : colWidth;
			}
			if (colWidth == 0){
				sheet.setColumnHidden(i, true);
			}else{
				sheet.setColumnWidth(i, colWidth);  
			}
		}
		log.debug("Create sheet {} success.", sheetName);
	}
	
	/**
	 * 创建表格样式
	 * @param wb 工作薄对象
	 * @return 样式列表
	 */
	private Map<String, CellStyle> createStyles(Workbook wb) {
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		
		CellStyle style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER); // 水平居中
		style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
		Font titleFont = wb.createFont();
		titleFont.setFontName("Arial");
		titleFont.setFontHeightInPoints((short) 16);
		titleFont.setBold(true);
		style.setFont(titleFont);
		styles.put("title", style);

		style = wb.createCellStyle();
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		Font dataFont = wb.createFont();
		dataFont.setFontName("Arial");
		dataFont.setFontHeightInPoints((short) 10);
		style.setFont(dataFont);
		styles.put("data", style);
		
		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("data"));
		style.setAlignment(HorizontalAlignment.LEFT);
		styles.put("data1", style);

		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("data"));
		style.setAlignment(HorizontalAlignment.CENTER);
		styles.put("data2", style);

		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("data"));
		style.setAlignment(HorizontalAlignment.RIGHT);
		styles.put("data3", style);
		
		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("data"));
//		style.setWrapText(true);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		Font headerFont = wb.createFont();
		headerFont.setFontName("Arial");
		headerFont.setFontHeightInPoints((short) 10);
		headerFont.setBold(true);
		headerFont.setColor(IndexedColors.WHITE.getIndex());
		style.setFont(headerFont);
		styles.put("header", style);
		
		return styles;
	}

	/**
	 * 添加一行
	 * @return 行对象
	 */
	public Row addRow(){
		return sheet.createRow(rownum++);
	}

	/**
	 * 添加一个单元格
	 * @param row 添加的行
	 * @param column 添加列号
	 * @param val 添加值
	 * @return 单元格对象
	 */
	public Cell addCell(Row row, int column, Object val){
		return this.addCell(row, column, val, Align.AUTO, Class.class, null);
	}
	
	/**
	 * 添加一个单元格
	 * @param row 添加的行
	 * @param column 添加列号
	 * @param val 添加值
	 * @param align 对齐方式（1：靠左；2：居中；3：靠右）
	 * @param converter 数据转换器
	 * @param dataFormat 数值格式（例如：0.00，yyyy-MM-dd）
	 * @return 单元格对象
	 */
	public Cell addCell(Row row, int column, Object val, Align align, Class<?> converter, String dataFormat){
		Cell cell = row.createCell(column);
		String defaultDataFormat = "@";
		try {
			if(val == null){
				cell.setCellValue("");
			}else if(converter != Class.class){
				cell.setCellValue((String)converter.getMethod("setValue", Object.class).invoke(null, val));
				try{
					defaultDataFormat = (String)converter.getMethod("getDataFormat").invoke(null);
				} catch (Exception ex) {
					defaultDataFormat = "@";
				}
			}else{
				if(val instanceof String) {
					cell.setCellValue((String) val);
				}else if(val instanceof Integer) {
					cell.setCellValue((Integer) val);
					defaultDataFormat = "0";
				}else if(val instanceof Long) {
					cell.setCellValue((Long) val);
					defaultDataFormat = "0";
				}else if(val instanceof Double) {
					cell.setCellValue((Double) val);
					defaultDataFormat = "0.00";
				}else if(val instanceof Float) {
					cell.setCellValue((Float) val);
					defaultDataFormat = "0.00";
				}else if(val instanceof Date) {
					cell.setCellValue((Date) val);
					defaultDataFormat = "yyyy-MM-dd HH:mm";
				}else {
					cell.setCellValue((String)Class.forName(this.getClass().getName().replaceAll(this.getClass().getSimpleName(), 
						"converter."+val.getClass().getSimpleName()+"Converter")).getMethod("exp", Object.class).invoke(null, val));
				}
			}
//			if (val != null){
				CellStyle style = styles.get("data_column_"+column);
				if (style == null){
					style = wb.createCellStyle();
					style.cloneStyleFrom(styles.get("data"+(align.value()>=1&&align.value()<=3?align.value():"")));
					if (dataFormat != null){
						defaultDataFormat = dataFormat;
					}
			        style.setDataFormat(wb.createDataFormat().getFormat(defaultDataFormat));
					styles.put("data_column_" + column, style);
				}
				cell.setCellStyle(style);
//			}
		} catch (Exception ex) {
			log.info("Set cell value ["+row.getRowNum()+","+column+"] error: " + ex.toString());
			cell.setCellValue(ObjectUtil.toString(val));
		}
		return cell;
	}

	/**
	 * 添加数据（通过annotation.ExportField添加数据）
	 * @return list 数据列表
	 */
	public <E> ExcelExport setDataList(List<E> list){
		for (E e : list){
			int colunm = 0;
			Row row = this.addRow();
			StringBuilder sb = new StringBuilder();
			for (Object[] os : annotationList){
				ExcelField ef = (ExcelField)os[0];
				Object val = null;
				// Get entity value
				try{
					if (StrUtil.isNotBlank(ef.attrName())){
						val = ReflectUtils.invokeGetter(e, ef.attrName());
					}else{
						if (os[1] instanceof Field){
							val = ReflectUtils.invokeGetter(e, ((Field)os[1]).getName());
						}else if (os[1] instanceof Method){
							val = ReflectUtils.invokeMethod(e, ((Method)os[1]).getName(), new Class[] {}, new Object[] {});
						}
					}
					// If is dict, get dict label
					/*if (StrUtil.isNotBlank(ef.dictType())){
						Class<?> dictUtils = Class.forName("com.fyts.core.modules.sys.utils.DictUtils");
						if(StrUtil.contains(val.toString(), ',')) {
							val = dictUtils.getMethod("getDictLabels", String.class, String.class,
									String.class).invoke( null, ef.dictType(), val==null?"":val.toString(), "");
						}else {
							val = dictUtils.getMethod("getDictLabel", String.class, String.class,
									String.class).invoke(null, ef.dictType(), val==null?"":val.toString(), "");
						}
						//val = DictUtils.getDictLabel(val==null?"":val.toString(), ef.dictType(), "");
					}*/
				}catch(Exception ex) {
					// Failure to ignore
					log.info(ex.toString());
					val = "";
				}
				String dataFormat = ef.dataFormat();
				try {
					// 获取Json格式化注解的格式化参数
					JsonFormat jf = e.getClass().getMethod("get"+StrUtil.upperFirst(ef.attrName())).getAnnotation(JsonFormat.class);
					if (jf != null && jf.pattern() != null){
						dataFormat = jf.pattern();
					}
				} catch (Exception e1) {
					// 如果获取失败，则使用默认。
				}
				this.addCell(row, colunm++, val, ef.align(), ef.converter(), dataFormat);
				sb.append(val + ", ");
			}
			log.debug("Write success: ["+row.getRowNum()+"] "+sb.toString());
		}
		return this;
	}
	
	/**
	 * 输出数据流
	 * @param os 输出数据流
	 */
	public ExcelExport write(OutputStream os){
		try{
			wb.write(os);
		}catch(IOException ex){
			log.error(ex.getMessage(), ex);
		}
		return this;
	}
	
	/**
	 * 输出到客户端
	 * @param fileName 输出文件名
	 */
	public ExcelExport write(HttpServletResponse response, String fileName){
		response.reset();
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename="+ URLUtil.encode(fileName));
		try {
			write(response.getOutputStream());
		} catch (IOException ex) {
			log.error(ex.getMessage(), ex);
		}
		return this;
	}
	
	/**
	 * 输出到文件
	 * @param fileName 输出文件名
	 */
	public ExcelExport writeFile(String fileName) throws FileNotFoundException, IOException{
		FileOutputStream os = new FileOutputStream(fileName);
		this.write(os);
		return this;
	}
	
	/**
	 * 清理临时文件
	 */
	public ExcelExport dispose(){
		if (wb instanceof SXSSFWorkbook){
			((SXSSFWorkbook)wb).dispose();
		}
		return this;
	}



	////////////////////以下三个方法,是想封装HuTool POI, 嫌麻烦, 放弃 //////////////////////////////////
	/**
	 * 导出Bean数据
	 *
	 * @param list     输出到excel的列表, 元素是bean
	 * @param title    excel 标题
	 * @param fileName excel 文件名
	 */
	@Deprecated
	public static void beanExportExcel(HttpServletResponse response, List list, String title, String fileName) throws IOException {

	}

	/**
	 * 导出map数据
	 *
	 * @param list  输出到excel的列表, 元素是map
	 * @param title excel 标题
	 * @param fileName excel 文件名
	 */
	@Deprecated
	public static void mapExportExcel(HttpServletResponse response, List<Map<String, Object>> list, String title, String fileName) throws IOException {
		/*Map<String, Object> row1 = new LinkedHashMap<>();
		  row1.put("姓名", "张三");
		  row1.put("年龄", 23);
		  row1.put("成绩", 88.32);
		  row1.put("是否合格", true);
		  row1.put("考试日期", DateUtil.date());
		  ArrayList<Map<String, Object>> rows = CollUtil.newArrayList(row1);
		*/
		//设置输出头
		response.reset();
		response.setContentType("application/octet-stream; charset=utf-8");
		response.setHeader("Content-Disposition", "attachment; filename="+ URLUtil.encode(fileName)+ cn.hutool.core.date.DateUtil.format(new Date(), "yyyyMMddHHmmss" + ".xlsx"));

		// 通过工具类创建writer
		cn.hutool.poi.excel.ExcelWriter writer = ExcelUtil.getWriter(true);
		// 合并单元格后的标题行，使用默认标题样式
		writer.merge(list.size() - 1, title);
		// 一次性写出内容，使用默认样式，强制输出标题
		writer.write(list, true);
		//out为OutputStream，需要写出到的目标流
		writer.flush(response.getOutputStream());
		// 关闭writer，释放内存
		writer.close();
	}

	/**
	 * 导出list数据
	 *
	 * @param list  输出到excel的列表, 元素是List<String>
	 * @param title excel 标题
	 * @param fileName excel 文件名
	 */
	@Deprecated
	public static void listExportExcel(HttpServletResponse response, List<List<String>> list, String title, String fileName) throws IOException {
		/*  List<String> row1 = CollUtil.newArrayList("姓名", "年龄", "性别", "出生日期");
			List<String> row2 = CollUtil.newArrayList("张三", "21", "男", "2019-01-02");
			List<String> row3 = CollUtil.newArrayList("李四", "22", "男", "2019-01-02");
			List<String> row4 = CollUtil.newArrayList("王五", "23", "男", "2019-01-02");
			List<String> row5 = CollUtil.newArrayList("赵六", "24", "男", "2019-01-02");
		*/
		//设置输出头
		response.reset();
		response.setContentType("application/octet-stream; charset=utf-8");
		response.setHeader("Content-Disposition", "attachment; filename="+ URLUtil.encode(fileName)+ DateUtil.format(new Date(), "yyyyMMddHHmmss" + ".xlsx"));

		// 通过工具类创建writer
		ExcelWriter writer = ExcelUtil.getWriter(true);

		// 设置样式
		CellStyle headCellStyle = writer.getHeadCellStyle();
		headCellStyle.setFillForegroundColor((short) 12);
		headCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headCellStyle.setBorderTop(BorderStyle.DOTTED);
		headCellStyle.setBorderRight(BorderStyle.DOTTED);
		headCellStyle.setBorderBottom(BorderStyle.DOTTED);
		headCellStyle.setBorderLeft(BorderStyle.DOTTED);
		headCellStyle.setAlignment(HorizontalAlignment.LEFT);// 对齐
		headCellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
		headCellStyle.setFillBackgroundColor(HSSFColor.GREEN.index);
		// 应用标题字体到标题样式
		Font font = writer.createFont();
		font.setColor(HSSFColor.WHITE.index);
		headCellStyle.setFont(font);

		// 合并单元格后的标题行，使用默认标题样式
		writer.merge(list.size() - 1, title);
		// 一次性写出内容，使用默认样式，强制输出标题
		writer.write(list, true);
		//out为OutputStream，需要写出到的目标流
		writer.flush(response.getOutputStream());
		// 关闭writer，释放内存
		writer.close();
	}

//	/**
//	 * 导出测试
//	 */
//	public static void main(String[] args) throws Throwable {
//		
//		// 初始化表头
//		List<String> headerList = CollectionUtil.newArrayList();
//		for (int i = 1; i <= 10; i++) {
//			headerList.add("表头"+i);
//		}
//
//		// 初始化数据集
//		List<String> rowList = CollectionUtil.newArrayList();
//		for (int i = 1; i <= headerList.size(); i++) {
//			rowList.add("数据"+i);
//		}
//		List<List<String>> dataList = CollectionUtil.newArrayList();
//		for (int i = 1; i <=100; i++) {
//			dataList.add(rowList);
//		}
//		
//		// 创建一个Sheet表，并导入数据
//		ExcelExport ee = new ExcelExport("表格1", "表格标题1", headerList, null);
//		for (int i = 0; i < dataList.size(); i++) {
//			Row row = ee.addRow();
//			for (int j = 0; j < dataList.get(i).size(); j++) {
//				ee.addCell(row, j, dataList.get(i).get(j));
//			}
//		}
//		
//		// 再创建一个Sheet表，并导入数据
//		ee.createSheet("表格2", "表格标题2", headerList, null);
//		for (int i = 0; i < dataList.size(); i++) {
//			Row row = ee.addRow();
//			for (int j = 0; j < dataList.get(i).size(); j++) {
//				ee.addCell(row, j, dataList.get(i).get(j)+"2");
//			}
//		}
//		
//		// 输出到文件
//		ee.writeFile("target/export.xlsx");
//
//		// 清理销毁
//		ee.dispose();
//		
//		log.debug("Export success.");
//		
//	}

}
