package com.freeman.common.file;

import com.sun.jndi.toolkit.url.UrlUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@RequestMapping("/download")
@Api(tags={"文件下载接口"})
public class DownloadController {

	
	Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 获取系统当前的文件存储前缀˝
	 */
	@Value("${tools.file.prefix}")
	private String prefix;

	@Value("${tools.file.local.path}")
	private String localpath;

	/**
	 * 文件下载
	 * @param filePath
	 * @param fileName
	 * @param response
	 */
	@GetMapping("")
	@ApiOperation(value="文件下载", notes="下载单个文件")
	public void download(@RequestParam @ApiParam(name = "filePath",value = "下载文件的相对路径", required = true) String filePath
						  ,@RequestParam @ApiParam(name = "fileName",value = "文件名", required = true) String fileName
						  ,HttpServletResponse response)  {
		if (!fileName.contains(".")){
			fileName = fileName + filePath.substring(filePath.lastIndexOf("."));
		}


		try(InputStream inputStream = new FileInputStream(new File(localpath+filePath));
			OutputStream outputStream = response.getOutputStream()) {

			//设置内容类型为下载类型
			response.setContentType("application/x-download");
			//内容体积
			response.addHeader("Content-Length", "" + inputStream.available());
			//设置请求头 和 文件下载名称
			response.setHeader("Content-Disposition", "attachment;filename="+ UrlUtil.encode(fileName,"UTF-8"));
            byte[] buf = new byte[4096];
            for(int length = 0; (length=inputStream.read(buf)) != -1; ) {
                outputStream.write(buf, 0, length);
            }
			outputStream.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
 
	
}
