package com.freeman.domain;



import com.freeman.spring.data.domain.BaseEntity;
import com.freeman.spring.data.utils.idgenerate.IdGen;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 没啥用
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"user"})
@Entity(name = "test_address")
public class Address extends BaseEntity<Address,Long> {

    @Id
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = IdGen.TYPE)
    private Long id;

    @Column(name = "label", nullable = false, columnDefinition = "varchar(16) comment '地址标签（家、公司）'")
    private String label;

    @Column(name = "country", nullable = false, columnDefinition = "varchar(16) comment '国家'")
    private String country;

    @Column(name = "province", nullable = false, columnDefinition = "varchar(32) comment '省份'")
    private String province;

    @Column(name = "city", nullable = false, columnDefinition = "varchar(32) comment '城市'")
    private String city;

    @Column(name = "district", nullable = false, columnDefinition = "varchar(32) comment '区县'")
    private String district;

    @Column(name = "street", nullable = false, columnDefinition = "varchar(255) comment '街道'")
    private String street;

    @Column(name = "detail", nullable = false, columnDefinition = "varchar(255) comment '详细地址'")
    private String detail;

    // address表为“一对多”中的多，所以使用@ManyToOne注解，并且配合@JoinColumn注解使用。
    // 如果单独使用@ManyToOne，那么会生成一张中间表来维护两张表关系，如果不想使用中间表使用@JoinColumn来生成外键维护两张表关系。
    // name="user_id"，表示生成的外键名称，并且字段类型以User表的主键为准。
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    //Date createDate, Long createdBy, Date lastModifiedDate, Long lastModifiedBy,
    public Address(String label, String country, String province, String city, String district, String street, String detail, User user) {
        //super(createDate, createdBy, lastModifiedDate, lastModifiedBy);
        label = label;
        country = country;
        province = province;
        city = city;
        district = district;
        street = street;
        detail = detail;
        user = user;
    }

}