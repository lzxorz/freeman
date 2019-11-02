package com.freeman.common.auth.shiro.token;

import java.util.StringJoiner;

public class PhoneSmsCodeToken extends BaseToken {

    private static final long serialVersionUID = 3079290637594876836L;

    private String phone;
    private String smsCode;

    public PhoneSmsCodeToken() { super(); }

    public PhoneSmsCodeToken(String phone, String smsCode) {
        this.phone = phone;
        this.smsCode = smsCode;
    }

    public PhoneSmsCodeToken(Long userId, String phone, String smsCode) {
        this.phone = phone;
        this.smsCode = smsCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    @Override public Object getPrincipal() { return phone; }

    @Override public Object getCredentials() { return this.smsCode; }

    @Override
    public void clear() {
        super.clear();
        this.phone = null;
        this.smsCode = null;
    }

    @Override @SuppressWarnings("Duplicates")
    public String toString() {
        StringJoiner sj = new StringJoiner(", ",this.getClass().getSimpleName()+" : {","}");
        //if (null!=getUserId()) sj.add("userId : " + super.getUserId());
        if (null!= getLoginPlatform()) sj.add("loginPlatform : " + super.getLoginPlatform());
        sj.add("phone : " + phone);
        sj.add("smsCode : " + smsCode);
        return sj.toString();
    }
}
