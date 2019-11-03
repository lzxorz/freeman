package com.freeman.common.utils;

import java.io.Console;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrUtil extends cn.hutool.core.util.StrUtil {

    /*public static String joinPids(String parentIds, String parentId){
        List<String> pids = splitTrim(parentIds, COMMA);
        pids.add(parentId);
        return join(",", pids);
    }*/

    /**
     * 将下划线连接方式命名的字符串转换为驼峰式
     * @param underlineStr 需要转换的字符串
     * @param firstLetter2LowerCase 是否首字母小写
     *
     * 如果转换前的字符串为空，则返回空字符串。</br>
     * 例如：
     *  underlineStr2CamelStr("HELLO_WORLD",true)  --> helloWorld
     *  underlineStr2CamelStr("HELLO_WORLD",false) --> HelloWorld
     *  underlineStr2CamelStr("hello_world",true)  --> helloWorld
     *  underlineStr2CamelStr("hello_world",false) --> HelloWorld
     *  underlineStr2CamelStr("HelloWorld",true)   --> helloWorld
     *  underlineStr2CamelStr("HelloWorld",false)  --> HelloWorld
     *  underlineStr2CamelStr("",true)         --> "" //空字符串, 第二个参数true/false，结果一样
     *  underlineStr2CamelStr(null,true)       --> "" //null,   第二个参数true/false，结果一样
     *  underlineStr2CamelStr("\t \n  ",true)  --> "" //空白符,  第二个参数true/false，结果一样
     * @return
     */
    public static String underlineStr2CamelStr(String underlineStr,boolean firstLetter2LowerCase) {
        if (cn.hutool.core.util.StrUtil.isBlank(underlineStr)) { // 空字符串/null/空白符-->“”
            return "";
        }
        if (!underlineStr.contains("_")) { // 没有下划线
            return firstLetter2LowerCase ? underlineStr : StrUtil.upperFirst(underlineStr); // 是否首字母转成小写
        }

        Matcher matcher = Pattern.compile("_([a-z])").matcher(underlineStr);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return firstLetter2LowerCase ? sb.toString() : StrUtil.upperFirst(sb.toString());
    }


    /**
     * 将驼峰命名方式的字符串转化成下划线连接的字符串
     * @param camelStr 需要转换的字符串
     * 例如：
     *  camelStr2UnderlineStr("SystemFileSeparator",true)  --> system_file_separator
     *  camelStr2UnderlineStr("SystemFileSeparator",false)  --> SYSTEM_FILE_SEPARATOR
     *  camelStr2UnderlineStr("",true)         --> "" //空字符串, 第二个参数true/false，结果一样
     *  camelStr2UnderlineStr(null,true)       --> "" //null,   第二个参数true/false，结果一样
     *  camelStr2UnderlineStr("\t \n  ",true)  --> "" //空白符,  第二个参数true/false，结果一样
     * @return
     */
    public static String camelStr2UnderlineStr(String camelStr){
        if (StrUtil.isBlank(camelStr)) { // 空字符串/null/空白符-->“”
            return "";
        }

        Matcher matcher = Pattern.compile("[A-Z]").matcher(camelStr);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group().toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.substring(1);
    }

    /*public static void main(String[] args) {
        String s = "SystemUserConfig";
        String s1 = camelStr2UnderlineStr(s);
        System.out.println(s1);
    }*/

}
