package com.freeman.common.constants;

import java.util.regex.Pattern;

/**
 * 正则常量
 */
public interface RegexpConstant {

    /**
     * 正则表达式:验证用户名(不包含中文和特殊字符)如果用户名使用手机号码或邮箱 则结合手机号验证和邮箱验证
     */
    String USERNAME = "^[a-zA-Z]\\w{5,17}$";
    /**
     * 正则表达式:验证密码(不包含特殊字符)
     */
    String PASSWORD = "^[a-zA-Z0-9]{6,16}$";
    /**
     * 正则表达式:验证邮箱
     */
    String EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    /**
     * 正则表达式:验证汉字(1-9个汉字)  {1,9} 自定义区间
     */
    String CHINESE = "^[\u4e00-\u9fa5]{1,9}$";
    /**
     * 正则表达式:验证身份证
     */
    String ID_CARD = "(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])";
    /**
     * 正则表达式:验证URL
     */
    String URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
    /**
     * 正则表达式:验证IP地址
     */
    String IP_ADDR = "(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})";
    /**
     * 说明：移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
     * 联通：130、131、132、152、155、156、185、186
     * 电信：133、153、180、189
     * 虚拟运营商  170
     * 总结起来就是第一位必定为1，第二位必定为3或4或5或7或8，其他位置的可以为0-9
     * 验证号码 手机号 固话均可
     * 正则表达式:验证手机号
     */
    String MOBILE = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$";

    /**
     * 校验用户名
     *
     * @param username
     * @return 校验通过返回true，否则返回false
     */
    static boolean isUserName(String username) {
        return Pattern.matches(USERNAME, username);
    }

    /**
     * 校验密码
     *
     * @param password
     * @return 校验通过返回true，否则返回false
     */
    static boolean isPassword(String password) {
        return Pattern.matches(PASSWORD, password);
    }

    /**
     * 校验手机号
     *
     * @param phone
     * @return
     */
    static boolean checkPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return Boolean.FALSE;
        }

        return Pattern.compile(MOBILE).matcher(phone).matches();
    }

}
