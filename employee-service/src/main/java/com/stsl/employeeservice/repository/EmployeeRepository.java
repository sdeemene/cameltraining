package com.stsl.employeeservice.repository;

import com.stsl.employeeservice.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

    Optional<EmployeeEntity> findByAuthUserId(String authUserId);

    Optional<EmployeeEntity> findByEmployeeId(String employeeId);
}
