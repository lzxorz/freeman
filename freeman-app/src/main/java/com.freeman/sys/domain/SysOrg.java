package com.freeman.sys.domain;


import com.freeman.spring.data.annotation.TableAlias;
import com.freeman.spring.data.domain.AuditableEntity;
import com.freeman.spring.data.domain.idgenerate.IdGen;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;


@Data
@Builder(toBuilder = true)
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity @TableAlias("so")
@NamedStoredProcedureQuery(name = "findAllChildren", procedureName ="prOrgChildren",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.INOUT, name = "id", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "parentId", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "name", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.INOUT, name = "type", type = String.class)
        })
public class SysOrg extends AuditableEntity<SysOrg,Long> {

    private static final long serialVersionUID = -798033486264509053L;

    @Id
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = IdGen.TYPE)
    private Long id;

    /** 上级机构ID，顶级机构为0 */
    private Long parentId;

    /** 公司/部门编码 */
    private String code;

    /** 公司/部门名称 */
    @NotBlank(message = "{required}")
    @Size(max = 20, message = "{noMoreThan}")
    private String name;

    /** 区划编码 */
   /*private String regionCode;*/

    /** 机构类型[1:公司,2:部门] */
    private String type;

    /** 排序 */
    private Integer sortNo;

    /** 联系方式 */
    private String phone;

    /** 地址 */
    private String address;

    /** 描述 */
    private String description;

    /** 状态[0:锁定,1:有效] */
    private String status;

    private transient List<SysOrg> children;


}