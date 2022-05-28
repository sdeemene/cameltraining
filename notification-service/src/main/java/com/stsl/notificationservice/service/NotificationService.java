package com.stsl.notificationservice.service;

import com.stsl.notificationservice.entity.NotificationEntity;
import com.stsl.notificationservice.repository.NotificationRepository;
import com.stsl.notificationservice.response.BaseResponse;
import com.stsl.notificationservice.response.NewNotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepo;

    private final EmailService emailService;

    public NotificationService(NotificationRepository notificationRepo, EmailService emailService) {
        this.notificationRepo = notificationRepo;
        this.emailService = emailService;
    }

    public NewNotificationRequest createNewNotification(NewNotificationRequest request) {
        if (request.getWithInternal().equals("Yes")) {
            NotificationEntity notificationEntity = NotificationEntity.builder()
                    .authUserId(request.getAuthUserId())
                    .authUserEmail(request.getAuthUserEmail())
                    .authUserName(request.getAuthUserName())
                    .body(request.getBody())
                    .dateCreated(LocalDate.now())
                    .timeCreated(LocalTime.now())
                    .subject(request.getSubject()).build();
            NotificationEntity savedNotificationEntity = notificationRepo.save(notificationEntity);
            log.info("saved notification {}", savedNotificationEntity);
        }
        if (request.getWithEmail().equals("Yes")) {
            emailService.sendEmail(request.getAuthUserEmail(), "", request.getSubject(), request.getBody());
        }
        return request;
    }


    public BaseResponse deleteNotification(long id) {
        BaseResponse response = new BaseResponse();
        Optional<NotificationEntity> notificationEntity = notificationRepo.findById(id);
        if (notificationEntity.isPresent()) {
            notificationRepo.deleteById(id);
            response.setStatusCode(HttpStatus.OK.value());
            response.setDescription("Notification deleted.");
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setDescription("Notification not found");
        }
        return response;
    }

    public BaseResponse updateNotification(Long notificationId) {
        BaseResponse response = new BaseResponse();
        Optional<NotificationEntity> notificationEntity = notificationRepo.findById(notificationId);
        if (notificationEntity.isPresent()) {
            notificationEntity.get().setRead(true);
            notificationRepo.save(notificationEntity.get());
            response.setData(notificationEntity.get());
            response.setStatusCode(HttpStatus.OK.value());
            response.setDescription("Notification updated");
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setDescription("Notification not found");
        }
        return response;

    }


    public BaseResponse getUnreadNotificationsByUserId(String authUserId) {
        BaseResponse response = new BaseResponse();
        List<NotificationEntity> notificationEntityList = notificationRepo.findByAuthUserIdAndRead(authUserId, false);
        if (!notificationEntityList.isEmpty()) {
            response.setData(notificationEntityList.size());
            response.setStatusCode(HttpStatus.OK.value());
            response.setDescription("Notifications Found");
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setDescription("Notifications not found");
        }
        return response;
    }

    public BaseResponse getAllNotifications() {
        BaseResponse response = new BaseResponse();
        List<Object> allNotifications = new ArrayList<>();
        List<NotificationEntity> notificationEntityList = notificationRepo.findAll();
        if (!notificationEntityList.isEmpty()) {
            notificationEntityList.sort(Comparator.comparing(NotificationEntity::getDateCreated).thenComparing(NotificationEntity::getTimeCreated).reversed());
            handleNotification(allNotifications, notificationEntityList);
            response.setData(allNotifications);
            response.setStatusCode(HttpStatus.OK.value());
            response.setDescription("Notifications Found");
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setDescription("Notifications not found");
        }
        return response;
    }

    public BaseResponse getNotificationsByUserId(String authUserId) {
        BaseResponse response = new BaseResponse();
        List<Object> allNotifications = new ArrayList<>();
        List<NotificationEntity> notificationEntityList = notificationRepo.findByAuthUserId(authUserId);
        if (!notificationEntityList.isEmpty()) {
            notificationEntityList.sort(Comparator.comparing(NotificationEntity::getDateCreated).reversed().thenComparing(NotificationEntity::getTimeCreated));
            handleNotification(allNotifications, notificationEntityList);
            response.setData(allNotifications);
            response.setStatusCode(HttpStatus.OK.value());
            response.setDescription("Notifications Found");
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setDescription("Notifications not found");
        }
        return response;
    }

    private void handleNotification(List<Object> allNotifications, List<NotificationEntity> notificationEntityList) {
        List<LocalDate> localDates = notificationEntityList.stream().map(NotificationEntity::getDateCreated)
                .distinct()
                .collect(Collectors.toList());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        for (LocalDate localDate : localDates) {
            Map<String, Object> notificationList = new HashMap<>();
            List<NotificationEntity> notificationEntities = notificationEntityList.stream().filter(notificationEntity -> notificationEntity.getDateCreated().equals(localDate)).collect(Collectors.toList());

            if (!notificationEntities.isEmpty()) {
                notificationList.put("date", localDate.format(formatter));
                notificationList.put("notification", notificationEntities);
                allNotifications.add(notificationList);
            }
        }
    }

}
