package com.freeman.utils;

import com.jfinal.template.Env;
import com.jfinal.template.TemplateException;
import com.jfinal.template.expr.ast.Expr;
import com.jfinal.template.expr.ast.ExprList;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Location;
import com.jfinal.template.stat.ParseException;
import com.jfinal.template.stat.Scope;
import com.jfinal.template.stat.ast.Output;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.StringJoiner;

public class FmOutput extends Output {

    private Expr expr;

    public FmOutput(ExprList exprList, Location location) {
        super(exprList, location);
        this.expr = exprList.getActualExpr();
    }

    public void exec(Env env, Scope scope, Writer writer) {
        try {
            Object value = this.expr.eval(scope);
            if (value instanceof String) {
                String str = "'"+value+"'";
                writer.write(str, 0, str.length());
            } else if (value instanceof Number) {
                Class<?> c = value.getClass();
                if (c == Integer.class) {
                    writer.write((Integer)value);
                } else if (c == Long.class) {
                    writer.write((Long)value);
                } else if (c == Double.class) {
                    writer.write((Double)value);
                } else if (c == Float.class) {
                    writer.write((Float)value);
                } else if (c == Short.class) {
                    writer.write((Short)value);
                } else {
                    writer.write(value.toString());
                }
            } else if (value instanceof Boolean) {
                writer.write((Boolean)value);
            } else if (value.getClass().isArray()) {
                writer.write(joinArray(value));
            }  else if (value instanceof Collection) {
                writer.write(joinCollection(value));
            } /*else if (value instanceof Map) { }*/
            else if (value != null) {
                writer.write(value.toString());
            }

        } catch (ParseException | TemplateException var6) {
            throw var6;
        } catch (Exception var7) {
            throw new TemplateException(var7.getMessage(), this.location, var7);
        }
    }

    public String joinArray(Object value) {
        Object[] valueArr = (Object[]) value;
        String res = "";
        if(valueArr.length>0){
            Class<?> type = value.getClass().getComponentType();
            if(type == String.class){
                StringJoiner sj = new StringJoiner("','","('","')");
                for (Object v : valueArr) {
                    sj.add(v.toString());
                }
                res = sj.toString();
            }else {
                String str = value.toString();
                res = "("+ str.substring(1,str.length()-1) +")";
            }
        }
        return res;
    }
    public String joinCollection(Object value) {
        Iterator iterator = ((Collection) value).iterator();
        if (iterator.hasNext()){
            StringJoiner sj = new StringJoiner(",","(",")");
            while (iterator.hasNext()) {
                Object v = iterator.next();
                if (v instanceof String) {
                    sj.add("'" + v.toString() + "'");
                }else {
                    sj.add(v.toString());
                }
            }
            return sj.toString();
        }
        return "";
    }

    public Boolean isEmpty(Object v) {
        if (v == null) {
            return true;
        } else if (v instanceof Collection) {
            return ((Collection)v).isEmpty();
        } else if (v instanceof Map) {
            return ((Map)v).isEmpty();
        } else if (v.getClass().isArray()) {
            return Array.getLength(v) == 0;
        } else if (v instanceof Iterator) {
            return !((Iterator)v).hasNext();
        } else if (v instanceof Iterable) {
            return !((Iterable)v).iterator().hasNext();
        } else {
            throw new IllegalArgumentException("isEmpty(...) 方法只能接受 Collection、Map、数组、Iterator、Iterable 类型参数");
        }
    }

    public Boolean notEmpty(Object v) {
        return !this.isEmpty(v);
    }
}
