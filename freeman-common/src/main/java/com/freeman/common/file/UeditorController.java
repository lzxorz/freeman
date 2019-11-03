package com.freeman.common.file;

import com.alibaba.fastjson.JSON;
import com.freeman.common.utils.StrUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ueditor")
public class UeditorController {

	@Value("${tools.file.prefix}")
	private String filePrefix;


	@SuppressWarnings("unchecked")
    @RequestMapping(value="",produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> upload( @RequestParam String action, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("application/json");
		if (StrUtil.equals(action,"config")){
			try (BufferedReader br = new BufferedReader(new InputStreamReader(new ClassPathResource("config/ueditor.json").getInputStream()))) {

				StringBuilder sb = new StringBuilder();
				for(String str="";(str=br.readLine()) != null; ) {
					sb.append(str);
				}

				HashMap map = JSON.parseObject(sb.toString(), HashMap.class);
				map.put("imageUrlPrefix", filePrefix);
				map.put("scrawlUrlPrefix", filePrefix);
				map.put("fileUrlPrefix", filePrefix);
				map.put("videoUrlPrefix", filePrefix);
				map.put("imageManagerUrlPrefix", filePrefix);
				map.put("fileManagerUrlPrefix", filePrefix);

				return map;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {

            MultipartFile file = ((MultipartHttpServletRequest) request).getFile("file");
            String fileName = file.getOriginalFilename();
            final String result = FileUtil.uploadFile(file, getDir(action));

            return new HashMap<String, Object>() {{
                put("state", "SUCCESS");
                put("type", file.getContentType());
                put("size", file.getSize());
                put("url", result);
                put("title", fileName);
                put("original", fileName);
            }};
        }
        return null;
	}


	private String getDir(String action) {
		String dir;
		switch (action){
			case "uploadimage":
				dir = "ueditor/images";
				break;
			case "uploadvideo":
				dir = "ueditor/videos";
				break;
			case "uploadfile":
				dir = "ueditor/files";
				break;
			default:
				dir = "ueditor";
		}
		return dir;
	}
}
