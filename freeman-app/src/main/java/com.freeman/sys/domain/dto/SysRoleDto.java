package com.freeman.sys.domain.dto;


import com.freeman.common.base.domain.AuditableDto;
import lombok.Data;

/**
 *
 * @author: liuzhixin
 * @email: lzxorz@163.com
 * @date: 19-8-10
 * @version: 1.0
 */
@Data
public class SysRoleDto extends AuditableDto {

    private static final long serialVersionUID = -4406568425874426924L;


    private Long id;

    /** 上一级ID */
    private Long parentId;

    /** 角色名称 */
    private String name;

    /** 角色编码 */
    private String code;

    /** 角色描述 */
    private String remark;

    /** 排序 */
    private Integer sortNo;

    private String status;

    /** 角色的权限IDS */
    private String permissionIds;

    /** 数据范围 */
    private String dataScope;

    /** 可以查看数据的部门IDS */
    private String dataDeptIds;


}