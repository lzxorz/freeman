package com.freeman.common.advice;


import cn.hutool.core.date.DateUtil;
import com.freeman.common.result.AssertUtil;
import com.freeman.common.result.FMException;
import com.freeman.common.result.i18n.UnifiedMessageSource;
import com.freeman.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.beans.PropertyEditorSupport;
import java.util.Date;


/**
 * 统一异常处理
 * @author 刘志新
 */
@Slf4j
@Component
@RestControllerAdvice
@ConditionalOnWebApplication
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnMissingBean(RestCtrlAdvice.class)
public class RestCtrlAdvice {
    /** 生产环境 */
    private final static String ENV_PROD = "prod";
    /** 当前环境 */
    @Value("${spring.profiles.active}")
    private String profile;

    @Autowired
    private UnifiedMessageSource unifiedMessageSource;

    /** 获取国际化消息 */
    public String getMessage(FMException e) {
        String code = "response." + e.getCode();
        String message = unifiedMessageSource.getMessage(code, e.getArgs());

        if (message == null || message.isEmpty()) {
            return e.getMessage();
        }

        return message;
    }


    /** Controller上一层相关异常 */
    @ExceptionHandler({
            NoHandlerFoundException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            HttpMediaTypeNotAcceptableException.class,
            MissingPathVariableException.class,
            MissingServletRequestParameterException.class,
            TypeMismatchException.class,
            HttpMessageNotReadableException.class,
            HttpMessageNotWritableException.class,
            // BindException.class,
            // MethodArgumentNotValidException.class
            ServletRequestBindingException.class,
            ConversionNotSupportedException.class,
            MissingServletRequestPartException.class,
            AsyncRequestTimeoutException.class
    })
    public R handleServletException(Exception e) {
        log.error(e.getMessage(), e);
        int code = 400;
        try {
            AssertUtil exceptionEnum = AssertUtil.valueOf(e.getClass().getSimpleName());
            code = exceptionEnum.getCode();
        } catch (IllegalArgumentException e1) {
            log.error("class [{}] not defined in enum {}", e.getClass().getName(), AssertUtil.class.getName());
        }

        return new R(code, e.getMessage());
    }

    /** 参数绑定异常 */
    @ExceptionHandler(value = BindException.class)
    public R handleBindException(BindException e) {
        log.error("参数绑定校验异常", e);
        return wrapperBindingResult(e.getBindingResult());
    }

    /** 参数校验(Valid)异常，将校验失败的所有异常组合成一条错误信息 */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e) {
        log.error("参数绑定校验异常", e);
        return wrapperBindingResult(e.getBindingResult());
    }

    /** 包装绑定异常结果 */
    private R wrapperBindingResult(BindingResult bindingResult) {
        StringBuilder msg = new StringBuilder();

        for (ObjectError error : bindingResult.getAllErrors()) {
            msg.append(", ");
            if (error instanceof FieldError) {
                msg.append(((FieldError) error).getField()).append(": ");
            }
            msg.append(error.getDefaultMessage() == null ? "" : error.getDefaultMessage());

        }

        return new R(AssertUtil.PARAM_INVALID.getCode(), msg.substring(2));
    }

    /** 自定义异常 */
    @ExceptionHandler(value = FMException.class)
    public R handleBaseException(FMException e) {
        log.error(e.getMessage(), e);
        return new R(e.getCode(), getMessage(e));
    }

    /** 未知异常 */
    @ExceptionHandler(value = Exception.class)
    public R handleException(Exception e) {
        log.error("未知异常==>{}\n{}",e.getMessage(), e);

        return new R(R.E.UNKNOWN_ERROR.getCode(), e.getMessage());
    }

    //////////////////////统一处理请求参数校验的异常处理//////////////////////
    /**
     * BindException
     * 统一处理请求参数校验的异常处理(实体对象传参)
     * 处理Get请求中 使用@Valid 验证路径中请求实体校验失败后抛出的异常,比如: 参数类型不匹配
     * 例子:
     *      @GetMapping("refund")
     *      public R refund(@Valid 实体类 abc) throws Exception {
     *          //...
     *      }
     *
     *      @Data
     *      public class 实体类 implements Serializable {
     *          @NotBlank(message = "订单号不能为空")
     *          private String orderId;
     *          @Min(value = 0, getMessage = "已消费金额金额不能为负数")
     *          private int orderAmt;
     *      }
     */

    /**
     * MethodArgumentNotValidException
     * 统一处理请求参数校验的异常处理
     * 请求的 JSON请求体内的参数校验
     * Controoler方法 @RequestBody上validate失败后抛出的异常
     *
     */

    /**
     * ConstraintViolationException
     * 统一处理请求参数校验的异常处理(普通传参)
     * 请求的 JSON请求体内的参数校验
     * 请求的 URL  参数检验
     * Controoler方法 @RequestParam上validate失败后抛出的异常
     */
    //////////////////////统一处理请求参数校验的异常处理//////////////////////


    /* // implements ResponseBodyAdvice<Object> 不好使
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }
    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        ServletServerHttpRequest serverRequest = (ServletServerHttpRequest)serverHttpRequest;
        ServletServerHttpResponse serverResponse = (ServletServerHttpResponse)serverHttpResponse;
        if(serverRequest == null || serverResponse == null
                || serverRequest.getServletRequest() == null || serverResponse.getServletResponse() == null) {
            return o;
        }

        // 对于未添加跨域消息头的响应进行处理
        HttpServletRequest request = serverRequest.getServletRequest();
        HttpServletResponse response = serverResponse.getServletResponse();
        String originHeader = "Access-Control-Allow-Origin";
        if(!response.containsHeader(originHeader)) {
            String origin = request.getHeader("Origin");
            String referer = request.getHeader("Referer");
            if(origin == null && referer != null) {
                origin = referer.substring(0, referer.indexOf("/", 7));
            }
            response.setHeader(originHeader, origin);
        }

        String allowHeaders = "Access-Control-Allow-Headers";
        if(!response.containsHeader(allowHeaders))
            response.setHeader(allowHeaders, request.getHeader(allowHeaders));

        String exposeHeaders = "Access-Control-Expose-Headers";
        if(!response.containsHeader(exposeHeaders))
            response.setHeader(exposeHeaders, "Authorization,isNewToken,X-Requested-With,Origin,Content-Type,Accept");

        String allowMethods = "Access-Control-Allow-Methods";
        if(!response.containsHeader(allowMethods))
            response.setHeader(allowMethods, "GET,POST,PUT,DELETE,OPTIONS");

        String credentials = "Access-Control-Allow-Credentials";
        if(!response.containsHeader(credentials))
            response.setHeader(credentials, "true");

        return o;
    }*/

    /** 将前台传递过来的日期格式的字符串，自动转化为Date类型 */
    @InitBinder
    public void initBinder(WebDataBinder binder){
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport(){
            @Override
            public void setAsText(String text){
                setValue(DateUtil.parse(text));
            }
        });
    }

}

