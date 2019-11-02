package com.freeman.common.auth.shiro.token;

/**
 * 调用登录接口时,给对应shiro token 设置 登录平台标识, 用户账号可以 多平台 同时在线
 *
 * @author 刘志新
 * @email  lzxorz@163.com
 **/
public enum LoginPlatform {
    PC,MOBILE; // PC平台 和 移动平台 可以同时在线

}
