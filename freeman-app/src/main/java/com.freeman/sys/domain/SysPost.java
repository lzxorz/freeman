package com.freeman.sys.domain;

import com.freeman.common.utils.excel.annotation.ExcelField;
import com.freeman.spring.data.annotation.TableAlias;
import com.freeman.spring.data.domain.AuditableEntity;
import com.freeman.spring.data.utils.idgenerate.IdGen;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 岗位表 sys_post
 * 
 * @author 刘志新
 */
@Data @Builder(toBuilder = true)
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity @TableAlias("sp")
public class SysPost extends AuditableEntity<SysPost,Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = IdGen.TYPE)
    @ExcelField(title = "岗位序号")
    private Long id;

    /** 岗位编码 */
    @ExcelField(title = "岗位编码")
    private String code;

    /** 岗位名称 */
    @ExcelField(title = "岗位名称")
    private String name;

    /** 排序 */
    @ExcelField(title = "岗位排序")
    private Integer sortNo;

    /** 状态（0正常 1停用） */
    @ExcelField(title = "状态") // , converter =
    private String status;


}
