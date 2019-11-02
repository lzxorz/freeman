package com.freeman.common.exec;

import org.apache.commons.jexl3.*;

import java.util.Map;

/**
 * java将(字符串)表达式转换成可执行代码
 * 参考链接 https://www.cnblogs.com/Jmmm/p/10610535.html
 * @author 刘志新
 */
public class Eval {
    /**
     * java将(字符串转)表达式换成可执行代码
     *
     * @param jexlExp 表达式
     * @param map
     * @return
     */
    public static Object convertToCode(String jexlExp, Map<String, Object> map) {
        // 创建或检索引擎
        JexlEngine jexl = new JexlBuilder().create();
        // 创建一个表达式
        JexlExpression e = jexl.createExpression(jexlExp);
        // 创建上下文并添加数据
        JexlContext jc = new MapContext();
        for (String key : map.keySet()) {
            jc.set(key, map.get(key));
        }
        // 现在评估表达式，得到结果
        if (null == e.evaluate(jc)) {
            return "";
        }
        return e.evaluate(jc);
    }

    //public static void main(String[] args) throws Exception {
    //    try {
    //        Map<String, Object> map = new HashMap<String, Object>(16){{put("money", 6100);}};
    //        String expression = "money>=2000&&money<=14000";
    //        Object code = convertToCode(expression, map);
    //        System.out.println((Boolean) code);
    //    } catch (Exception e) {
    //        // TODO Auto-generated catch block
    //        e.printStackTrace();
    //    }
    //}

}
