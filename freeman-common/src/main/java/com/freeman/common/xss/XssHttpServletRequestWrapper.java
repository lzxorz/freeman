package com.freeman.common.xss;

import com.freeman.common.utils.JsoupUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


/** 
 * <code>{@link XssHttpServletRequestWrapper}</code>
 * @author
 */  
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
  
    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }
    
    @Override
    public String[] getParameterValues(String name) {
    	String[] values = super.getParameterValues(name);
    	if(values != null){
    		for (int i=0;i<values.length;i++) {
                // 防xss攻击和过滤前后空格
                values[i] = JsoupUtil.clean(values[i]);
    		}
    	}
    	return values;
    }
}  
