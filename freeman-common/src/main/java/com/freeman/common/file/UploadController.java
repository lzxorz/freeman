package com.freeman.common.file;

import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/upload")
//@Api(tags={"文件上传接口"})
public class UploadController {

	Logger logger = LoggerFactory.getLogger(getClass());

	/** 单文件上传 */
	@PostMapping("single")
	//@ApiOperation(value="单个文件上传", notes="上传单个文件，并返回路径")
	public String singleUpload(@ApiParam(value = "所需上传文件",required = true) MultipartFile file,
							   @ApiParam(name="dirName", value = "上传文件的目录(接口对象名)",required = true) String dirName){

		String filePath = FileUtil.uploadFile(file,dirName);
		return  filePath;
	}


	/** 批量文件上传 */
	@PostMapping("batch")
	//@ApiOperation(value="批量文件上传", notes="上传批量文件，并返回路径列表")
	public List<String> batchUpload(@ApiParam(name = "file",allowMultiple=true,value="文件数据",required=true) HttpServletRequest request,
									@ApiParam(value = "所需上传文件(该出仅供swagger测试使用，具体开发需要上传files属性)",required = false) //该出仅供swagger测试使用，具体开发需要上传files属性
											MultipartFile file1,
									@ApiParam(value = "所需上传文件(该出仅供swagger测试使用，具体开发需要上传files属性)",required = false) //该出仅供swagger测试使用，具体开发需要上传files属性
											MultipartFile file2,
									@ApiParam(name="dirName", value = "上传文件的目录(接口对象名)",required = true) String dirName){
		List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("files");

		List<String> filePaths = new ArrayList<String>();
		String filePath;
		for (MultipartFile multipartFile : files) {
			filePath = FileUtil.uploadFile(multipartFile,dirName);
			filePaths.add(filePath);
		}
		return filePaths;
	}


}
