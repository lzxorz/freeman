package com.freeman.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;

/**
 * 断言工具类
 * @author 刘志新
 * 使用方式示例: AssertUtil.PARAM_INVALID.isNull(变量, "参数校验失败，不能为空");
 */
@Getter
@AllArgsConstructor
public enum AssertUtil implements Assert {
    /**
     * <p>异常类与http status对照关系</p>
     *
     * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
     */
    MethodArgumentTypeMismatchException(HttpServletResponse.SC_BAD_REQUEST, "失败"), // 400
    MissingServletRequestPartException(HttpServletResponse.SC_BAD_REQUEST, ""), // 400
    MissingPathVariableException(HttpServletResponse.SC_BAD_REQUEST, ""), // 400
    BindException(HttpServletResponse.SC_BAD_REQUEST, ""), // 400
    MissingServletRequestParameterException(HttpServletResponse.SC_BAD_REQUEST, ""), // 400
    TypeMismatchException(HttpServletResponse.SC_BAD_REQUEST, ""), // 400
    ServletRequestBindingException(HttpServletResponse.SC_BAD_REQUEST, ""), // 400
    HttpMessageNotReadableException(HttpServletResponse.SC_BAD_REQUEST, ""), // 400
    NoHandlerFoundException(HttpServletResponse.SC_NOT_FOUND, "接口不存在"), // 404
    NoSuchRequestHandlingMethodException(HttpServletResponse.SC_NOT_FOUND, ""), // 404
    HttpRequestMethodNotSupportedException(HttpServletResponse.SC_METHOD_NOT_ALLOWED, ""), // 405
    HttpMediaTypeNotAcceptableException(HttpServletResponse.SC_NOT_ACCEPTABLE, ""), // 406
    HttpMediaTypeNotSupportedException(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, ""), // 415
    ConversionNotSupportedException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ""), // 500
    HttpMessageNotWritableException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ""), // 500
    AsyncRequestTimeoutException(HttpServletResponse.SC_SERVICE_UNAVAILABLE, ""), // 503

    SUCCESS(200,"成功"),

    UNAUTHORIZED(4001, "未认证"),
    LOCKSCREEN(4002, "用户锁屏"),
    AUTHORIZED_FAIL(4003, "没有访问权限"),
    PARAMTYPE_MISMATCH(4005, "参数类型不匹配"),
    PARAM_INVALID(4006, "参数校验失败"),
    LIMIT_ACCESS_ERROR(4007, "接口访问超出频率限制"),

    UNKNOWN_ERROR(500, "服务器繁忙,请稍后重试"),
    UPLOAD_FILE(5005, "上传文件出错");


    /** 返回状态码 */
    private int code;
    /** 返回消息 */
    private String message;

    @Override
    public BizException builderException(Object... args) {
        String msg = MessageFormat.format(this.getMessage(), args);
        return new BizException(this, args, msg);
    }

    @Override
    public BizException builderException(Throwable t, Object... args) {
        String msg = MessageFormat.format(this.getMessage(), args);
        return new BizException(this, args, msg, t);
    }
}
