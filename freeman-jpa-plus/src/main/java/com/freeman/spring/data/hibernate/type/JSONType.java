package com.freeman.spring.data.hibernate.type;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.hibernate.HibernateException;
//import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.type.descriptor.java.DataHelper;
import org.hibernate.usertype.DynamicParameterizedType;
import org.hibernate.usertype.UserType;
import org.jboss.logging.Logger;
import org.springframework.util.ReflectionUtils;

import javax.persistence.Column;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.Objects;
import java.util.Properties;

public class JSONType implements UserType, DynamicParameterizedType, Serializable {
    private static final long serialVersionUID = 352044032843534075L;
    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, com.freeman.spring.data.hibernate.type.JSONType.class.getName());
    public static final String TYPE = "JSONType";
    public static final String CLASS_NAME = "class";
    private int sqlType = Types.VARCHAR;
    private Type type = Object.class;

    @Override
    public int[] sqlTypes() {
        return new int[]{sqlType};
    }

    @Override
    public Class returnedClass() {
        if (type instanceof ParameterizedType) {
            return (Class) ((ParameterizedType) type).getRawType();
        } else {
            return (Class) type;
        }
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    /**
     * 从JDBC ResultSet读取数据,将其转换为自定义类型后返回
     * (此方法要求对可能出现null值进行处理)
     * names中包含了当前自定义类型的映射字段名称
     * @param rs
     * @param names
     * @param owner
     * @return
     * @throws HibernateException
     * @throws SQLException
     */
    @Override
    //public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor sharedSessionContractImplementor, Object owner) throws HibernateException, SQLException {
        String value = extractString(rs.getObject(names[0]));
        if (rs.wasNull() || StrUtil.isEmpty(value)) {
            if (LOG.isTraceEnabled()) {
                LOG.tracev("Returning null as column {0}", names[0]);
            }
            return null;
        } else if (type == Object.class) {
            return JSON.parse(value);
        } else {
            return JSON.parseObject(value, type);
        }
    }

    /**
     * 本方法将在Hibernate进行数据保存时被调用
     * 我们可以通过PreparedStateme将自定义数据写入到对应的数据库表字段
     * @param st
     * @param value
     * @param index
     * @throws HibernateException
     * @throws SQLException
     */
    @Override
    //public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor sharedSessionContractImplementor) throws HibernateException, SQLException {
        if (value == null) {
            if (LOG.isTraceEnabled()) {
                LOG.tracev("Binding null to parameter: {0}", index);
            }
            st.setNull(index, sqlType);
        } else {
            String json;
            if (type == Object.class) {
                json = JSON.toJSONString(value, SerializerFeature.WriteClassName);
            } else {
                json = JSON.toJSONString(value);
            }
            if (sqlType == Types.CLOB) {
                StringReader sr = new StringReader(json);
                st.setCharacterStream(index, sr, json.length());
            } else {
                st.setObject(index, json, sqlType);
            }
        }
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
     * @param value
     * @return
     * @throws HibernateException
     */
    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
      /*if (value instanceof JSONObject) {
            return ((JSONObject) value).clone();
        } else if (value instanceof Cloneable) {
            return ObjectUtils.clone(value);
        } else if (value instanceof Serializable) {
            return SerializationHelper.clone((Serializable) value);
        } else {
            return value;
        }*/
    }

    /**
     * 本类型实例是否可变
     * @return
     */
    @Override
    public boolean isMutable() {
        return true;
    }

    /* 序列化 */
    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
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

    @SuppressWarnings("unchecked")
    public void setParameterValues(Properties parameters) {
        try {
            Class eClass = ReflectHelper.classForName(parameters.getProperty(DynamicParameterizedType.ENTITY));
            Field field = ReflectionUtils.findField(eClass, parameters.getProperty(DynamicParameterizedType.PROPERTY));
            Type fieldType = field.getGenericType();
            if (fieldType instanceof Class || fieldType instanceof ParameterizedType) {
                type = fieldType;
            }
            parseSqlType(field.getAnnotations());
            return;
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        //final ParameterType reader = (ParameterType) parameters.get(
        final DynamicParameterizedType.ParameterType reader = (DynamicParameterizedType.ParameterType) parameters.get(
                DynamicParameterizedType.PARAMETER_TYPE);
        if (reader != null) {
            type = reader.getReturnedClass();
            parseSqlType(reader.getAnnotationsMethod());
        } else {
            try {
                type = ReflectHelper.classForName((String) parameters.get(CLASS_NAME));
            } catch (ClassNotFoundException exception) {
                throw new HibernateException("class not found", exception);
            }
        }
    }

    private void parseSqlType(Annotation[] anns) {
        for (Annotation an : anns) {
            if (an instanceof Column) {
                int length = ((Column) an).length();
                if (length > 4000) {
                    sqlType = Types.CLOB;
                }
                break;
            }
        }
    }

    private static String extractString(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        }
        if (value instanceof Reader) {
            return DataHelper.extractString((Reader) value);
        }
        if (value instanceof Clob) {
            return DataHelper.extractString((Clob) value);
        }
        return null;
    }
}
