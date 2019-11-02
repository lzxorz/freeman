package com.freeman.common.auth.shiro.token;

import java.util.StringJoiner;

public class FmUsernamePasswordToken extends BaseToken {

    private static final long serialVersionUID = 6544894623532242959L;

    private String username;
    private char[] password;


    public FmUsernamePasswordToken() { super(); }

    public FmUsernamePasswordToken(String username, char[] password) {
        this.username = username;
        this.password = (char[])(password != null ? password : null);
    }
    public FmUsernamePasswordToken(String username, char[] password, LoginPlatform loginPlatform) {
        super(loginPlatform);
        this.username = username;
        this.password = (char[])(password != null ? password : null);
    }

    public FmUsernamePasswordToken(String username, String password) {
        this.username = username;
        this.password = (char[])(password != null ? password.toCharArray() : null);
    }

    public FmUsernamePasswordToken(String username, String password, LoginPlatform loginPlatform) {
        super(loginPlatform);
        this.username = username;
        this.password = (char[])(password != null ? password.toCharArray() : null);
    }


    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public char[] getPassword() { return password; }

    public void setPassword(char[] password) { this.password = password; }

    @Override public Object getPrincipal() { return username; }

    @Override public Object getCredentials() { return password; }

    @Override
    public void clear() {
        super.clear();
        this.username = null;
        if (this.password != null) {
            for(int i = 0; i < this.password.length; ++i) {
                this.password[i] = 0;
            }
            this.password = null;
        }

    }

    @Override @SuppressWarnings("Duplicates")
    public String toString() {
        StringJoiner sj = new StringJoiner(", ",this.getClass().getSimpleName()+" : {","}");
        //if (null!=getUserId()) sj.add("userId : " + super.getUserId());
        if (null!= getLoginPlatform()) sj.add("loginPlatform : " + super.getLoginPlatform());
        sj.add("username : " + username);
        sj.add("password : " + String.valueOf(password));
        return sj.toString();
    }

}
