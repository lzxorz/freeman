package com.freeman;

import cn.hutool.core.util.StrUtil;
import com.freeman.utils.StringKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
public class regexTest {
    /** order by 驼峰转下划线 */
    private static String buildOrderBy(String query) {
        Pattern ORDERBY_PATTERN = Pattern.compile("order\\s+by.+?$", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher matcher = ORDERBY_PATTERN.matcher(query);

        StringBuffer selectSql = new StringBuffer();
        StringBuffer orderByClause = new StringBuffer();
        if (!matcher.find()) {
            return query;
        }else {
            String part = matcher.group(0);
            orderByClause.append(part);
            if ((!part.contains(")") || StringUtils.countOccurrencesOf(part, ")") == StringUtils.countOccurrencesOf(part, "("))) {
                matcher.appendReplacement(selectSql, "");
            }
            matcher.appendTail(selectSql);
        }

        Matcher m = Pattern.compile("\\b[a-z][a-z\\d]*[A-Z][a-z\\d]*\\b").matcher(orderByClause.toString());

        orderByClause.setLength(0);
        while (m.find()) {
            String part = m.group();
            m.appendReplacement(orderByClause, StringKit.camelToUnderline(part));
        }
        m.appendTail(orderByClause);

        String s = selectSql.append(orderByClause).toString();
        return s;
    }

    /** 转换 #include("xxx") */
    private static String converterInclude(String tplContent) {

        Matcher matcher = Pattern.compile("#include\\(\"\\w+\"\\)").matcher(tplContent);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String matchStr = matcher.group();
            int fromIndex = StrUtil.indexOf(matchStr, '"')+1;
            int lastIndexOf = StrUtil.indexOf(matchStr, '"', fromIndex);
            //engine.getTemplate(new EntityMethodKeySource(getTemplateKey(entityName, StrUtil.sub(matchStr, fromIndex, lastIndexOf)),content,true));
            matcher.appendReplacement(sb, "1, 2,");
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    public static void main(String[] args) {

        String sql = "SELECT sp.id, sp.parent_id, sp.name, sp.path, sp.component, sp.perms, sp.icon, sp.type, sp.sort_no, sp.hide, sp.description, sp.create_by, sp.create_time, sp.update_by, sp.update_time, sp.del_flag FROM sys_permission sp WHERE 1=1 AND sp.del_flag = '0' order by sp.sortNo asc, sp.createTime desc";
        // log.info(buildOrderBy(sql));

        // String tplContent = "SELECT #include(\"test123\") sr.id, sr.parent_id, sr.name, sr.code, sr.remark, sr.sort_no, sr.status, sr.data_scope, (SELECT GROUP_CONCAT(srd.dept_id) as data_dept_ids FROM sys_role_dept srd WHERE srd.role_id = sr.id) as data_dept_ids, sr.create_time,sr.create_by,sr.update_by,sr.update_time,sr.del_flag FROM sys_role sr  WHERE 1=1 AND sr.del_flag = '0' #if(name??)　 AND sr.name LIKE CONCAT('%',#(name),'%') #end #if(params?? && params.createTime??)  AND date_format(sr.create_time,'%Y-%m-%d') BETWEEN #(params.createTime[0]) AND #(params.createTime[1]) #end #if(params?? && params.dataScope??) #(params.dataScope) #end";
        String tplContent = "SELECT #include(\"test123\") sr.id, sr.parent_id, sr.name, sr.code, sr.remark, sr.sort_no, sr.status, sr.data_scope, (SELECT GROUP_CONCAT(srd.dept_id) as data_dept_ids FROM sys_role_dept srd WHERE srd.role_id = sr.id) as data_dept_ids, sr.create_time,sr.create_by,sr.update_by,sr.update_time,sr.del_flag FROM sys_role sr  WHERE 1=1 AND sr.del_flag = '0' #if(name??)　 AND sr.name LIKE CONCAT('%',#(name),'%') #end #if(params?? && params.createTime??)  AND date_format(sr.create_time,'%Y-%m-%d') BETWEEN #(params.createTime[0]) AND #(params.createTime[1]) #end #if(params?? && params.dataScope??) #(params.dataScope) #end";
        log.info(converterInclude(tplContent));

    }
}
