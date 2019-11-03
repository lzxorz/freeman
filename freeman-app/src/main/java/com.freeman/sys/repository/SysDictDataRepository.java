package com.freeman.sys.repository;


import com.freeman.sys.domain.SysDictItem;
import com.freeman.spring.data.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysDictDataRepository extends BaseRepository<SysDictItem,Long> {
}