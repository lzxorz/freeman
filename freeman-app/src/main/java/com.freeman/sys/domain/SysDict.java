package com.freeman.sys.domain;

import com.freeman.spring.data.annotation.TableAlias;
import com.freeman.spring.data.domain.AuditableEntity;
import com.freeman.spring.data.utils.idgenerate.IdGen;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity @TableAlias("sd")
public class SysDict extends AuditableEntity<SysDict,Long> {

    private static final long serialVersionUID = 7780820231535870010L;

    @Id
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = IdGen.TYPE)
    private Long id;

    @NotBlank(message = "{required}")
    private String name;

    @NotBlank(message = "{required}")
    private String type;

    private Integer sortNo;

    private String remark;

    private String status;

    @OneToMany
    @JoinColumn(name="type")
    private transient List<SysDictItem> dictItems;

}
