package com.freeman.domain;



import com.freeman.spring.data.domain.BaseEntity;
import com.freeman.spring.data.domain.idgenerate.IdGen;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * 没啥用
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "test_id_card")
public class IdCard extends BaseEntity<IdCard,Long> {

    @Id
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = IdGen.TYPE)
    private Long id;

    private String number;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;


}