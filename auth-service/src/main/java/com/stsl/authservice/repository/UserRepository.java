package com.stsl.authservice.repository;

import com.stsl.authservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String userName);

    Optional<UserEntity> findByUid(String authUserId);
}
