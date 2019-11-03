package com.freeman.common.auth.shiro.token;

import java.util.StringJoiner;



public class JwtToken extends BaseToken {
    private static final long serialVersionUID = -3815846854883782716L;

    private String token;

    public JwtToken() { super(); }
    public JwtToken(String token) { this.token = token; }
    public JwtToken(String token, LoginPlatform loginPlatform) {
        super(loginPlatform);
        this.token = token;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    @Override public Object getPrincipal() { return token; }

    @Override public Object getCredentials() { return token; }

    @Override
    public void clear() {
        super.clear();
        this.token = null;
    }

    @Override @SuppressWarnings("Duplicates")
    public String toString() {
        StringJoiner sj = new StringJoiner(", ",this.getClass().getSimpleName()+" : {","}");
        //if (null!=getUserId()) sj.add("userId : " + super.getUserId());
        if (null!= getLoginPlatform()) sj.add("loginPlatform : " + super.getLoginPlatform());
        sj.add("token : " + token);
        return sj.toString();
    }

}
