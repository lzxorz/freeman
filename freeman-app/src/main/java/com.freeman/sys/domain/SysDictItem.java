package com.freeman.sys.domain;

import com.freeman.spring.data.annotation.TableAlias;
import com.freeman.spring.data.domain.AuditableEntity;
import com.freeman.spring.data.utils.idgenerate.IdGen;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity @TableAlias("sdi")
public class SysDictItem extends AuditableEntity<SysDictItem,Long> {
    private static final long serialVersionUID = -2417192307462937743L;

    @Id
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = IdGen.TYPE)
    private Long id;

    @NotBlank(message = "{required}")
    private String type;

    @NotBlank(message = "{required}")
    private String label;

    @NotBlank(message = "{required}")
    private String value;

    private Integer sortNo;

    private String remark;

    private String status;

}
