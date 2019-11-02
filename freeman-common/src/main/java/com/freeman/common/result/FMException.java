package com.freeman.common.result;

import lombok.Getter;

/**
 * 自定义异常类
 *
 */
@Getter
public class FMException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 返回状态码 */
    protected int code;
    /** 异常消息参数 */
    protected Object[] args;


    public FMException(Assert anAssert) {
        super(anAssert.getMessage());
        this.code = anAssert.getCode();
    }

    public FMException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public FMException(Assert anAssert, Object[] args, String message) {
        super(message);
        this.code = anAssert.getCode();
        this.args = args;
    }

    public FMException(Assert anAssert, Object[] args, String message, Throwable cause) {
        super(message, cause);
        this.code = anAssert.getCode();
        this.args = args;
    }

}
