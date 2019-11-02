package com.freeman.common.dataPermission;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.freeman.common.constants.Constants;
import com.freeman.common.utils.DateUtil;
import com.freeman.common.utils.StrUtil;
import com.freeman.sys.domain.SysOrg;
import com.freeman.sys.domain.SysUser;
import com.freeman.spring.data.annotation.TableAlias;
import com.freeman.common.auth.shiro.utils.ShiroUtil;
import com.freeman.spring.data.domain.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.util.*;

@Slf4j
public class DataPermissionUtil {
    //0:全部数据权限,1:自定义数据权限,2:本公司及以下数据,3:本公司数据,4:本部门数据及以下数据,5:本部门数据 ,6:仅本人数据,7:无


    /**
     * 数据范围过滤
     * 　需要数据范围过滤的表,必须要有create_by(创建者)列,　根据这个列　连表　sys_user(可能用到的列company_id,company_ids,dept_id,dept_ids) 关联到数据行的所属机构
     *  然后,当前用户的根据角色选择需要拼接的片段, 当前用户对应信息做为参数
     *
     * @param entity sql模板查询　对应的实体类
     * @param entity　需要用到实体类上注解TableAlias
     *              例如: @TableAlias("su") ==> 表别名 su
     */
    public static <T extends BaseEntity> T dataScopeFilter(T entity){
        Class<? extends BaseEntity> entityClass = entity.getClass();
        if(!entityClass.isAnnotationPresent(TableAlias.class)){
            throw new RuntimeException("实体类["+entityClass.getSimpleName()+"]上需要注解@TableAlias");
        }
        String tableAlias = entityClass.getAnnotation(TableAlias.class).value(); // 表别名
        String tablePermissonColumn = "";
        if (entity instanceof SysOrg) {
            tablePermissonColumn = String.join(".", tableAlias, "id");
        }else if (entity instanceof SysUser){
            tablePermissonColumn = String.join(".", tableAlias, "id");
        }else {
            tablePermissonColumn = String.join(".", tableAlias, "create_by");
        }


        // 获取当前用户
        SysUser currentUser = ShiroUtil.getCurrentUser();
        // 当前用户是超级管理员,不用过滤
        if (currentUser.isSuperAdmin()) return entity;

        // 解析成sql字符串片段
        Set<String> segments = new HashSet<>();
        for (String scope : currentUser.getDataScope()) {
            switch (scope){
                case Constants.DATA_SCOPE.ALL: // 0:全部数据权限
                    return entity;
                case Constants.DATA_SCOPE.CUSTOM: // 1:自定义数据权限
                    if (entity instanceof SysOrg){
                        segments.add(StrUtil.format("FIND_IN_SET({},{}) > 0", tablePermissonColumn, currentUser.getDataDeptIds()));
                    }else {
                        segments.add(StrUtil.format("FIND_IN_SET(fnDeptIdByUid({}),{}) > 0", tablePermissonColumn, currentUser.getDataDeptIds()));
                    }
                    break;
                case Constants.DATA_SCOPE.COMPANY_AND_CHILD: // 2:本公司及以下数据
                    if (entity instanceof SysOrg) {
                        segments.add(StrUtil.format("FIND_IN_SET({},fnOrgAncestor({})) > 0", currentUser.getCompanyId(), tablePermissonColumn));
                    }else {
                        segments.add(StrUtil.format("FIND_IN_SET({},fnOrgAncestorByUid({})) > 0", currentUser.getCompanyId(), tablePermissonColumn));
                    }
                    break;
                case Constants.DATA_SCOPE.COMPANY: // 3:本公司数据
                    if (entity instanceof SysOrg) {
                        segments.add(StrUtil.format("fnCommpanyIdByOrgId({}) = {}", tablePermissonColumn, currentUser.getCompanyId()));
                    }else {
                        segments.add(StrUtil.format("fnCommpanyIdByUid({}) = {}", tablePermissonColumn, currentUser.getCompanyId()));
                    }
                    break;
                case Constants.DATA_SCOPE.DEPT_AND_CHILD: // 4:本部门数据及以下数据
                    if (entity instanceof SysOrg) {
                        segments.add(StrUtil.format("FIND_IN_SET({}, fnOrgAncestor({})) > 0", currentUser.getDeptId(), tablePermissonColumn));
                    }else {
                        segments.add(StrUtil.format("FIND_IN_SET({}, fnOrgAncestorByUid({})) > 0", currentUser.getDeptId(), tablePermissonColumn));
                    }
                    break;
                case Constants.DATA_SCOPE.DEPT: // 5:本部门数据
                    if (entity instanceof SysOrg) {
                        segments.add(StrUtil.format("{} = {}", tablePermissonColumn, currentUser.getDeptId()));
                    }else {
                        segments.add(StrUtil.format("fnDeptIdByUid({}) = {}", tablePermissonColumn, currentUser.getDeptId()));
                    }
                    break;
                case Constants.DATA_SCOPE.SELF: // 6:仅本人数据　这个条件不用联查
                    if (entity instanceof SysOrg) {
                    }else {
                        segments.add(StrUtil.format("{} = {}", tablePermissonColumn, currentUser.getId()));
                    }
                    break;
                case Constants.DATA_SCOPE.NO: // 7:无
                    segments.add("1=2");
                    break;
                default :
                    log.error("角色[{}]数据范围获取出错", currentUser.getRoleNames());
            }
        }
        if (!ObjectUtils.isEmpty(segments)){
            // 拼接角色的数据范围
            StringJoiner sqlStr = new StringJoiner("OR "," (",")");
            for (String segment : segments) {
                sqlStr.add(segment);
            }
            entity.getParams().put(Constants.DATA_SCOPE.FIELD,sqlStr.toString());
        }
        return entity;
    }


    /////////////////////////////////////////以下内容为生成动态sql片段使用,考虑到实际意义不大,但是明显增加了代码复杂度,放弃///////////////////////////////////////////////

    /**
     * 根据 实体类属性的DataRole注解、查询接口的传参、缓存中的数据规则 , 生成 List<DateRuleDto>
     *
     * @param clazz
     * @param rulesParams 参数形式: {username:"co"age:[18,35],...}
     * @param rulesCaches 参数形式: [{filedName:"username",operate:"EQ"},{filedName:"realname",operate:"CTS"},...]
     * @return
     */
    private static List<DateRuleDto> genDateRuleDtos(Class<?> clazz, JSONObject rulesParams, JSONArray rulesCaches) {
        List<DateRuleDto> dateRuleDtos = new ArrayList();

        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if(!field.isAnnotationPresent(DataRule.class)){ continue; }

                String fieldName = field.getName(); // 1
                DataRule dataRule = field.getAnnotation(DataRule.class);
                if(!rulesParams.containsKey(fieldName)) { continue; }
                String javaType = field.getType().getSimpleName(); // 2
                Column column = field.getAnnotation(Column.class);
                String columnName = (null != column && !column.name().isEmpty()) ? column.name() : StrUtil.camelStr2UnderlineStr(fieldName); // 3

                Operator operator = null; // 4
                JSONObject jo =null;
                for (int i = 0; i < rulesCaches.size(); i++) {
                    jo = rulesCaches.getJSONObject(i);
                    if (jo.getString("filedName").equals(fieldName)){
                        operator = Operator.valueOf(jo.getString("operator"));
                        rulesCaches.remove(i);
                        break;
                    }
                }
                operator = (null!= operator) ? operator : dataRule.defualtOperate(); //缓存中没有,就用@DateRule的默认值

                Object value = rulesParams.get(fieldName); //5 // 需要sql过滤

                dateRuleDtos.add(new DateRuleDto().setFiledName(fieldName)
                        .setJavaType(javaType)
                        .setColumnName(dataRule.tableAlias() + "." + columnName)
                        .setOperator(operator)
                        .setValue(value));

            }
            clazz = clazz.getSuperclass();
        }

        return dateRuleDtos;
    }


    /**
     * 根据 处理过的表列名，操作符，参数值 生成 单个sql条件
     * @param sqlStr
     * @param dateRuleDto
     * @return
     */
    @SuppressWarnings("Duplicates")
    private static void joinSql(StringJoiner sqlStr, DateRuleDto dateRuleDto){
        String columnName = dateRuleDto.getColumnName();
        String javaType = dateRuleDto.getJavaType();
        Operator operator = dateRuleDto.getOperator();
        Object value = dateRuleDto.getOperator();
        if (null == value){
            return;
        }else if (value instanceof String) {
            if (javaType.equals("Date")){
                value = DateUtil.dateTimeFormatter.get().format(DateUtil.parse(value.toString()));
            }
            value = "\'"+value+"\'";
        }else if (value instanceof String[]){
            String[] arr = (String[]) value;
            for (int i = 0; i < arr.length; i++) {
                if (javaType.equals("Date")){
                    arr[i] = DateUtil.dateTimeFormatter.get().format(DateUtil.parse(arr[i]));
                    if ((operator.name().equals("BETWEEN") || operator.name().equals("NBETW")) && (1==i && arr[i].contains("00:00:00"))) {
                        arr[i] = arr[i].replace(" 00:00:00"," 23:59:59");
                    }
                }
                arr[i] = "\'"+arr[i]+"\'";
            }
        }
        switch (operator) {
            case EQ:
                sqlStr.add(columnName + " = " + value);
                break;
            case NE:
                sqlStr.add(columnName + " != " + value);
                break;
            case LT:
                sqlStr.add(columnName + " < " + value);
                break;
            case LTE:
                sqlStr.add(columnName + " <= " + value);
                break;
            case GT:
                sqlStr.add(columnName + " > " + value);
                break;
            case GTE:
                sqlStr.add(columnName + " >= " + value);
                break;
            /*case NOT:
                break;*/
            case CTS:
                sqlStr.add(columnName + " like " + "CONCAT('%',"+ value +",'%')");
                break;
            case NOTCTS:
                sqlStr.add(columnName + " not like " + "CONCAT('%',"+ value +",'%')");
                break;
            case START:
                sqlStr.add(columnName + " like " + value +",'%')");
                break;
            case NOTSTART:
                sqlStr.add(columnName + " not like " + value +",'%')");
                break;
            case END:
                sqlStr.add(columnName + " like " + "CONCAT('%',"+ value +")");
                break;
            case NOTEND:
                sqlStr.add(columnName + " not like " + "CONCAT('%',"+ value+")");
                break;
            case ISNULL:
                sqlStr.add(columnName + " is null");
                break;
            case ISNOTNULL:
                sqlStr.add(columnName + " is not null");
                break;
            case EMPTY:
                sqlStr.add(columnName + " = ''");
                break;
            case NOTEMPTY:
                sqlStr.add(columnName + " != ''");
                break;
            case IN:
                StringBuilder sb_in = new StringBuilder();
                Object[] arr_in = (Object[]) value;
                for (int i = 0; i < arr_in.length; i++) {
                    sb_in.append(arr_in[i]);
                    if (i<arr_in.length-1){ sb_in.append(","); }
                }
                if (javaType.equals("Date")){
                    sqlStr.add("DATE_FORMAT("+ columnName + ",'%Y-%m-%d %H:%i:%s')" + "IN (" + sb_in.toString() + ")");
                }else {
                    sqlStr.add(columnName + "IN (" + sb_in.toString() + ")");
                }
                break;
            case NOTIN:
                StringBuilder sb_nin = new StringBuilder();
                Object[] arr_nin = (Object[]) value;
                for (int i = 0; i < arr_nin.length; i++) {
                    sb_nin.append(arr_nin[i]);
                    if (i<arr_nin.length-1){ sb_nin.append(","); }
                }
                if (javaType.equals("Date")){
                    sqlStr.add("DATE_FORMAT("+ columnName + ",'%Y-%m-%d %H:%i:%s')" + " NOT IN (" + sb_nin.toString() + ")");
                }else {
                    sqlStr.add(columnName + "NOT IN (" + sb_nin.toString() + ")");
                }
                break;
            case BETWEEN:
                Object[] arr_betw = (Object[]) value;
                if (javaType.equals("Date")) {
                    sqlStr.add("DATE_FORMAT("+ columnName + ",'%Y-%m-%d %H:%i:%s')" + "BETWEEN " + arr_betw[0] + " AND " + arr_betw[1]);
                }else {
                    sqlStr.add(columnName + "BETWEEN " + arr_betw[0] + " AND " + arr_betw[1]);
                }
                break;
            case NOTBETWEEN:
                Object[] arr_nbetw = (Object[]) value;
                if (javaType.equals("Date")) {
                    sqlStr.add("DATE_FORMAT("+ columnName + ",'%Y-%m-%d %H:%i:%s')" + "NOT BETWEEN "+ arr_nbetw[0] + " AND " + arr_nbetw[1]);
                }else {
                    sqlStr.add(columnName + "NOT BETWEEN "+ arr_nbetw[0] + " AND " + arr_nbetw[1]);
                }
                break;
        }
    }

}
