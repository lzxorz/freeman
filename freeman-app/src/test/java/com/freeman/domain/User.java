package com.freeman.domain;

import com.freeman.spring.data.annotation.TableAlias;
import com.freeman.spring.data.domain.AuditableEntity;
import com.freeman.spring.data.utils.idgenerate.IdGen;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;


/**
 * 没啥用
 */
@Data
//@Builder(toBuilder = true) //注释到全参数的构造器上,才可以builer().父类属性 //toBuilder = true ==> 可以new User().toBuilder().xxxxx().xxxx().build();
@EqualsAndHashCode(callSuper = true, exclude = {"address"})
@NoArgsConstructor
@ToString(callSuper = true)
@Entity(name = "test_user") @TableAlias("user")
public class User extends AuditableEntity<User, Long> {

    @Id
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = IdGen.TYPE)
    private Long id;

    @Column()
    private String realName;

    @Column(name = "username",nullable=false,columnDefinition="varchar(32) unique comment '用户名'")
    private String username;

    @Column(name="password") //,nullable=false,columnDefinition="varchar(64) comment '密码'"
    protected String password; //密码

    @Column(name = "nickname", length = 100)
    private String nickname;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "age", length = 3)
    private Integer age;

    // @Column(name = "phone", length = 50)
    // private String phone;

    @Column(name = "avatar", length = 2048)
    private String avatar;

    @Column(name="email",nullable=false,columnDefinition="varchar(32) default '' comment '邮箱'")
    protected String email; //邮箱号

    @Column(length=32,name="wxId",columnDefinition="varchar(32) unique comment '微信号'")
    private String wxId; //微信ID

    @OneToOne
    @JoinColumn(name = "id_card_id")
    private IdCard idCard;

    @OneToMany
    private Set<Phone> phones;

    // user是用户实体，一个用户对应多个地址，所以user是“一对多”中的“一”。在一的实体中，使用此注解标注。
    // mappedBy:标注该属性对应“多”的实体中的属性名。
    // cascade 表示级联操作。
    // fetch  加载方式，默认都是lazy加载。
    // @OneToMany(mappedBy="user",fetch = FetchType.EAGER) //,fetch= FetchType.LAZY
    // private Set<Address> addresses;

    @Builder //注意，需要重写(包括父类的)全参数的构造方法，否则使用budiler()时, 父数中的属性不能被赋值。
    public User(Long id, Date createDate, Long createdBy, Date lastModifiedDate, Long lastModifiedBy, String realName, String username, String password, String nickname, LocalDate birthday, Integer age,/* String phone,*/ String avatar, String email, String wxId, Set<Address> addresses) {
        super(createDate, createdBy, lastModifiedDate, lastModifiedBy);
        this.id = id;
        this.realName = realName;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.birthday = birthday;
        this.age = age;
        // this.phone = phone;
        this.avatar = avatar;
        this.email = email;
        this.wxId = wxId;
        // this.addresses = addresses;
    }

    // public User addAddress(Address addr){
    //     if(null == addresses) {
    //         addresses = new HashSet<>();
    //     }
    //     if(addr != null){
    //         addresses.add(addr);
    //     }
    //
    //     return this;
    // }


}
