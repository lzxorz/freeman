package com.freeman.common.xss;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.BooleanUtil;
import com.freeman.common.utils.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

/**
 * 防止XSS攻击的过滤器
 * 通过Jsoup过滤请求参数内的特定字符
 * @author 刘志新
 */
public class XssFilter implements Filter {
    private static Logger logger = LoggerFactory.getLogger(XssFilter.class);

    /** 排除链接 */
    public List<String> excludes = new ArrayList<>();
    /** xss过滤开关 */
    public boolean enabled = false;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (logger.isDebugEnabled()) logger.debug("xss filter init~~~~~~~~~~~~");

        String excludesStr = filterConfig.getInitParameter("excludes");
        if (StrUtil.isNotBlank(excludesStr)) {
            String[] url = excludesStr.split(StrUtil.COMMA);
            for (int i = 0; url.length>0 && i<url.length; i++) {
                excludes.add(url[i]);
            }
        }

        String enabledStr = filterConfig.getInitParameter("enabled");
        enabled = StrUtil.isNotBlank(enabledStr) ? BooleanUtil.toBoolean(enabledStr) : false;

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (logger.isDebugEnabled()) logger.debug("xss filter is open~~~~~~~~~~~~");

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        if (!enabled || isExcludeURL(req, res)) {
            chain.doFilter(request, response);
            return;
        }
        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request);
        chain.doFilter(xssRequest, response);
    }

    private boolean isExcludeURL(HttpServletRequest request, HttpServletResponse response) {

        if (ObjectUtils.isEmpty(excludes)) return false;

        String url = request.getServletPath();
        for (String pattern : excludes) {
            Matcher m = Pattern.compile("^" + pattern).matcher(url);
            if (m.find()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void destroy() {}
}