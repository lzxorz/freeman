package com.freeman.common.utils.excel.converter;

/**
 *
 * @author: 刘志新
 * @email: lzxorz@163.com
 */
public interface IConverter {

     /**
     * 判断一个类是否为基本数据类型。
     *
     * @param clazz 要判断的类。
     * @return true 表示为基本数据类型。
     */
     /*default boolean isBaseDataType(Class clazz) throws Exception {
        return (clazz.equals(String.class) || clazz.equals(Integer.class) || clazz.equals(Byte.class) || clazz.equals(Long.class) || clazz.equals(Double.class) || clazz.equals(Float.class) || clazz.equals(Character.class) || clazz.equals(Short.class) || clazz.equals(BigDecimal.class) || clazz.equals(BigInteger.class) || clazz.equals(Boolean.class) || clazz.equals(Date.class) || clazz.isPrimitive());
     }*/

    /**
     * 获取对象值（导入）
     */
    Object imp(String val);

    /**
     * 获取对象值（导出）
     */
    String exp(Object val);

}
