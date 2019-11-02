/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.freeman.spring.data.hibernate.type;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.beanutils.ConvertUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Properties;

/**
 * 将List转换为指定分隔符分隔的字符串存储 List的元素类型只支持常见的数据类型 可参考{@link org.apache.commons.beanutils.ConvertUtilsBean}
 *
 * @author Wu Tianqiang
 */
public class CollectionToStringUserType implements UserType, ParameterizedType, Serializable {

    /**
     * 默认,
     */
    private String separator;
    /**
     * 默认 java.lang.Long
     */
    private Class elementType;
    /**
     * 默认 ArrayList
     */
    private Class collectionType;


    @Override
    public void setParameterValues(Properties parameters) {
        String separator = (String) parameters.get("separator");
        if (!StrUtil.isEmpty(separator)) {
            this.separator = separator;
        } else {
            this.separator = ",";
        }

        String collectionType = (String) parameters.get("collectionType");
        if (!StrUtil.isEmpty(collectionType)) {
            try {
                this.collectionType = Class.forName(collectionType);
            } catch (ClassNotFoundException e) {
                throw new HibernateException(e);
            }
        } else {
            this.collectionType = java.util.ArrayList.class;
        }

        String elementType = (String) parameters.get("elementType");
        if (!StrUtil.isEmpty(elementType)) {
            try {
                this.elementType = Class.forName(elementType);
            } catch (ClassNotFoundException e) {
                throw new HibernateException(e);
            }
        } else {
            this.elementType = Long.TYPE;
        }
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.VARCHAR};
    }

    @Override
    public Class returnedClass() {
        return collectionType;
    }

    @Override
    public boolean equals(Object o, Object o1) throws HibernateException {
        return o == o1 || !(o == null) && o.equals(o1);

    }

    @Override
    public int hashCode(Object o) throws HibernateException {
        return o.hashCode();
    }


    /**
     * 从JDBC ResultSet读取数据,将其转换为自定义类型后返回
     * (此方法要求对克能出现null值进行处理)
     * names中包含了当前自定义类型的映射字段名称
     *
     * @param names
     * @param owner
     * @throws HibernateException
     * @throws SQLException
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object nullSafeGet(ResultSet resultSet, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        String valueStr = resultSet.getString(names[0]);
        if (StrUtil.isEmpty(valueStr)) {
            return newCollection();
        }
        String[] values = StrUtil.split(valueStr, separator);
        Collection result = newCollection();
        for (String value : values) {
            if (StrUtil.isNotEmpty(value)) {
                if (Persistable.class.isAssignableFrom(this.elementType)) {
                    Integer id = Integer.valueOf(value);
                    Object obj = session.immediateLoad(this.elementType.getName(), id);
                    result.add(ConvertUtils.convert(obj, elementType));
                } else {
                    result.add(ConvertUtils.convert(value, elementType));
                }
            }
        }
        return result;
    }




    private Collection newCollection() {
        try {
            return (Collection) collectionType.newInstance();
        } catch (Exception e) {
            throw new HibernateException(e);
        }
    }

    /**
     * 本方法将在Hibernate进行数据保存时被调用
     * 我们可以通过PreparedStateme将自定义数据写入到对应的数据库表字段
     */
    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index, SharedSessionContractImplementor sharedSessionContractImplementor) throws HibernateException, SQLException {
        String valueStr;
        if (value == null) {
            valueStr = "";
        } else {
            valueStr = join((Collection) value, separator);
        }
        preparedStatement.setString(index, valueStr);
    }

    private String join(Collection value, String separator) {
        StringBuilder sb = new StringBuilder();
        for (Object o : value) {
            if (o instanceof Persistable) {
                sb.append(((Persistable) o).getId());
            } else {
                sb.append(String.valueOf(o));
            }
            sb.append(separator);
        }
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 提供自定义类型的完全复制方法
     * 本方法将用构造返回对象
     * 当nullSafeGet方法调用之后，我们获得了自定义数据对象，在向用户返回自定义数据之前，
     * deepCopy方法将被调用，它将根据自定义数据对象构造一个完全拷贝，并将此拷贝返回给用户
     * 此时我们就得到了自定义数据对象的两个版本，第一个是从数据库读出的原始版本，其二是我们通过
     * deepCopy方法构造的复制版本，原始的版本将有Hibernate维护，复制版由用户使用。原始版本用作
     * 稍后的脏数据检查依据；Hibernate将在脏数据检查过程中将两个版本的数据进行对比（通过调用
     * equals方法），如果数据发生了变化（equals方法返回false），则执行对应的持久化操作
     *
     * @param o
     * @throws HibernateException
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object deepCopy(Object o) throws HibernateException {
        if (o == null) return null;
        Collection copyCollection = newCollection();
        copyCollection.addAll((Collection) o);
        return copyCollection;
    }

    /**
     * 本类型实例是否可变
     */
    @Override
    public boolean isMutable() {
        return true;
    }

    /* 序列化 */
    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return ((Serializable) value);
    }

    /* 反序列化 */
    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

}
