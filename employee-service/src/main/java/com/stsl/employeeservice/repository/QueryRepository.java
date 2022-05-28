package com.stsl.employeeservice.repository;

import com.stsl.employeeservice.entity.QueryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QueryRepository extends JpaRepository<QueryEntity, Long> {


    List<QueryEntity> findByAuthUserId(String authUserId);

}
