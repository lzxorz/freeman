package com.freeman.common.auth.shiro.token;

import org.apache.shiro.authc.AuthenticationToken;



/**
 *
 */
public abstract class BaseToken implements AuthenticationToken {

    private static final long serialVersionUID = 8342336522663196521L;


    private LoginPlatform loginPlatform; //登录平台标识

    public BaseToken() {}
    public BaseToken(LoginPlatform loginPlatform) { this.loginPlatform = loginPlatform;}

    public LoginPlatform getLoginPlatform() { return loginPlatform; }

    public BaseToken setLoginPlatform(LoginPlatform loginPlatform) {
        this.loginPlatform = loginPlatform; return this;
    }


    public void clear() {
        this.loginPlatform = null;
    }
}
