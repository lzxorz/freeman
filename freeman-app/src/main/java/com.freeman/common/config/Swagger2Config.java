package com.freeman.common.config;


import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.*;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author scott
 */
@Slf4j
//@EnableSwagger2
//@Configuration
public class Swagger2Config implements WebMvcConfigurer {

	/** springmvc静态资源处理, springmvc处理静态资源映射 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
		registry.addResourceHandler("/swagger/**").addResourceLocations("classpath:/swagger/");
	}


	/**
	 * Swagger2 丝袜哥的配置文件
	 * https://blog.csdn.net/u012702547/article/details/88775298
	 * https://www.toutiao.com/i6708971971431891470/
	 */
	@Bean
	public Docket docket() {
		List<ResponseMessage> responseMessages = new ArrayList();
		responseMessages.add(new ResponseMessageBuilder().code(HttpServletResponse.SC_OK).message("操作成功").build());
		responseMessages.add(new ResponseMessageBuilder().code(HttpServletResponse.SC_NOT_FOUND).message("资源不存在").build());
		responseMessages.add(new ResponseMessageBuilder().code(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).message("服务器异常").build());

		return new Docket(DocumentationType.SWAGGER_2)
				.globalResponseMessage(RequestMethod.GET, responseMessages)
				.globalResponseMessage(RequestMethod.POST, responseMessages)
				.globalResponseMessage(RequestMethod.DELETE, responseMessages)
				.useDefaultResponseMessages(false)
				.pathMapping("/")
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.freeman"))
				.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)) // 加了ApiOperation注解的类，才生成接口文档
				.paths(PathSelectors.any())
				.build().apiInfo(apiInfo())
				.globalOperationParameters(headerToken());
	}

	/** api文档的详细信息 */
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("XXX系统数据接口文档")
				.description("SpringBoot整合Swagger2的restful风格接口")
				.version("9.0")
				//.contact(new Contact("名字", "www.csdn.net", "abc@163.com"))
				//.license("The Apache License")
				//.licenseUrl("http://www.baidu.com")
				.build();
	}

	/** JWT token */
	private List<Parameter> headerToken() {
		ParameterBuilder tokenPar = new ParameterBuilder();
		List<Parameter> pars = new ArrayList<>();
		tokenPar.name("Authorization").description("token").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
		pars.add(tokenPar.build());
		return pars;
	}

}
