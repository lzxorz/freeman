package com.freeman.sys.repository;


import com.freeman.spring.data.repository.BaseRepository;
import com.freeman.sys.domain.SysPost;
import org.springframework.stereotype.Repository;

@Repository
public interface SysPostRepository extends BaseRepository<SysPost,Long> {

}

