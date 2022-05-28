package com.stsl.attendanceservice.repository;

import com.stsl.attendanceservice.entity.AttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Long> {

    List<AttendanceEntity> findByAuthUserId(String authUserId);

    Optional<AttendanceEntity> findByAuthUserIdAndDateCreated(String authUserId, LocalDate dateCreated);


}
