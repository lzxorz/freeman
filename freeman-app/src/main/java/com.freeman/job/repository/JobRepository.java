package com.freeman.job.repository;




import com.freeman.job.domain.Job;
import com.freeman.spring.data.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface JobRepository extends BaseRepository<Job,Long> {

	@Modifying
	@Query(value = "UPDATE t_job SET status = ?1  WHERE job_id IN ?2", nativeQuery = true)
	int updateStatusByInIn(String status, List<Long> list);
}