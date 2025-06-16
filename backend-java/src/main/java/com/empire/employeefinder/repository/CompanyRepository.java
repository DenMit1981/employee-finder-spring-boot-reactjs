package com.empire.employeefinder.repository;

import com.empire.employeefinder.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByCompanyName(String companyName);

    Optional<Company> findByRegNumber(String regNumber);
}
