package com.freeman.repository;

import com.freeman.domain.Phone;
import com.freeman.spring.data.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneRepository extends BaseRepository<Phone, Long> {

}
