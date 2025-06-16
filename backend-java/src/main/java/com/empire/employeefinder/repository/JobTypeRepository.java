package com.empire.employeefinder.repository;

import com.empire.employeefinder.model.JobType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobTypeRepository extends JpaRepository<JobType, Long> {

    List<JobType> findAllByOrderByNameAsc();
}
