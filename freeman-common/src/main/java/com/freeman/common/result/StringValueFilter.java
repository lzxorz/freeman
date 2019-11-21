package com.freeman.common.result;

import com.alibaba.fastjson.serializer.ValueFilter;
import com.freeman.common.utils.StrUtil;
import org.springframework.web.util.HtmlUtils;

@Deprecated
public class StringValueFilter implements ValueFilter {
    private static String[] ignoreField = {"istatus","type","delFlag"};
    /**
     * 属性名不在忽略属性数组中, 属性值是String类型, 返回给前端要解码
     *
     * @Param  object
     * @Param  name  属性名
     * @Param  value 属性值
     * @return
     */
    @Override
    public Object process(Object object, String name, Object value) {

        if (value instanceof String && !StrUtil.equalsAny(name,ignoreField)) {
            value = HtmlUtils.htmlUnescape((String)value); // HtmlUtils.htmlUnescape("尼古拉&middot;特斯拉");
        }
        return value;
    }
}
