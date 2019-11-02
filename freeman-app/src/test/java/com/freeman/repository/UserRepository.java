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


    @Override
    default Optional<User> findOne(Specification<User> specification) {
        return Optional.empty();
    }

    @Override
    default List<User> findAll(Specification<User> specification) {
        return null;
    }

    @Override
    default Page<User> findAll(Specification<User> specification, Pageable pageable) {
        return null;
    }

    @Override
    default List<User> findAll(Specification<User> specification, Sort sort) {
        return null;
    }

    @Override
    default long count(Specification<User> specification) {
        return 0;
    }

    User findByUsername(String username);
    List<User> findAllByAgeIn(List<String> ages, Sort sort);
    Page<User> findAllByUsernameLike(String username, Pageable pageable);
    //Page<User> findAll(Specification<User> specification, Pageable pageable);

}
