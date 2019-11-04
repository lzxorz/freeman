package com.freeman.common.dataPermission;

import com.freeman.common.auth.shiro.utils.ShiroUtil;
import com.freeman.common.constants.Constants;
import com.freeman.common.utils.StrUtil;
import com.freeman.spring.data.annotation.TableAlias;
import com.freeman.spring.data.domain.BaseEntity;
import com.freeman.sys.domain.SysOrg;
import com.freeman.sys.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

@Slf4j
public class DataPermUtil {
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
                    segments.clear();
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
            entity.putParam(Constants.DATA_SCOPE.FIELD,sqlStr.toString());
        }
        return entity;
    }

}
