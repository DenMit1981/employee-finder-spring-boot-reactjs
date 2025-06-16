package com.empire.employeefinder.repository;

import com.empire.employeefinder.model.JobPosition;
import com.empire.employeefinder.model.JobType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPositionRepository extends JpaRepository<JobPosition, Long> {

    List<JobPosition> findAllByOrderByNameAsc();

    List<JobPosition> findAllByJobTypeOrderByNameAsc(JobType jobType);
}
