package com.freeman.repository;



import com.freeman.domain.User;
import com.freeman.spring.data.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
//@RepositoryDefinition(domainClass=User.class,idClass=Long.class) 注解和 extends JpaRepository<User,Long> 一样
public interface UserRepository extends BaseRepository<User, Long> {

    User findByUsername(String username);
    List<User> findAllByAgeIn(List<String> ages, Sort sort);
    Page<User> findAllByUsernameLike(String username, Pageable pageable);
    //Page<User> findAll(Specification<User> specification, Pageable pageable);

}
