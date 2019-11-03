package com.freeman.repository;

import com.freeman.domain.IdCard;
import com.freeman.spring.data.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdCardRepository extends BaseRepository<IdCard, Long> {

}
