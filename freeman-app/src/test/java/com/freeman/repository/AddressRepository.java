package com.freeman.repository;

import com.freeman.domain.Address;
import com.freeman.spring.data.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends BaseRepository<Address, Long> {

    //public Page<TestAddress> findAll(Specification<TestAddress> specification, Pageable pageable);
}
