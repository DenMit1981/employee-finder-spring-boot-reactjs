package com.empire.employeefinder.repository;

import com.empire.employeefinder.model.Employee;
import com.empire.employeefinder.model.Selection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SelectionRepository extends JpaRepository<Selection, Long>, JpaSpecificationExecutor<Employee> {

    Optional<Selection> findByCompanyIdAndSubmittedFalse(Long companyId);

    boolean existsByCompanyIdAndChosenCandidatesContains(Long companyId, Employee employee);

    boolean existsByChosenCandidates_IdAndSubmittedFalseAndCompany_IdNot(Long employeeId, Long companyId);
}
