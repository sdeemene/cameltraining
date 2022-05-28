package com.stsl.notificationservice.repository;

import com.stsl.notificationservice.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;


@Transactional
@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByAuthUserId(String authUserId);

    List<NotificationEntity> findByAuthUserIdAndRead(String authUserId, Boolean read);




}
