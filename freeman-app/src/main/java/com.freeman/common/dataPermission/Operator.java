package com.freeman.common.dataPermission;


import java.util.StringJoiner;

/**
 * Query 规则 常量
 */
@Deprecated
public enum Operator {

    EQ("等于"), //,"eq"),
    NE("不等于"), //,"ne"),           // a.username != 'jack'
    LT("小于"), //,"lt"),             // a.code < 6
    LTE("小于等于"), //,"le"),        // a.type <= 6
    GT("大于"), //,"gt"),            // a.salary > 8888
    GTE("大于等于"), //,"ge"),       // type >= 6
    //NOT("不是"), //,"gt"),          // not a.salary > 8888
    ISNULL("是null"), //,"is_null"),              // a.realname is null
    ISNOTNULL("不是null"), //,"not_null"), // a.phone is not null
    EMPTY("是空值"), //,"empty"),                      // a.realname = ‘’
    NOTEMPTY("不是空值"), //,"not_empty"), // a.realname ！= ‘’
    CTS("包含"), //,"like"),    // a.nickname like '%与骑%'
    NOTCTS("不包含"), //,"not_like"),                  // a.username not like '%jack%'
    START("以此开始"), //,"left_like"),          // a.nickname like '%兵'
    NOTSTART("不以此开始"), //,"not_left_like"),       // a.nickname not like '%兵'
    END("以此结束"), //,"right_like"),        // a.nickname like '安%'
    NOTEND("不以此结束"), //,"not_right_like"), // a.nickname not like '安%'
    IN("在其中"), //,"in"),     // a.userId in (110,278,566)
    NOTIN("不在其中"), //,"not_in"),     // a.userId not in (110,278,566)
    BETWEEN("在范围"), //,"between"),    // a.age between 1 and 30
    NOTBETWEEN("不在范围"); //,"not_between");    // a.age not between 1 and 30


    private String label;
    //private String value;
    //private String operate;

    Operator(String label){
        this.label = label;
    }


    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(",","{","}");
           sj.add(this.label+" : "+this.name());
        return sj.toString();
    }


    public static String toJsonStr() {
        StringJoiner sj = new StringJoiner(",","[","]");
        for(Operator en :values()){
            sj.add("{"+en.label+":"+en.name()+"}");
        }
        return sj.toString();
    }
}
