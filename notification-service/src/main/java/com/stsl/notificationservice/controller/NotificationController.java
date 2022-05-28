package com.stsl.notificationservice.controller;


import com.stsl.notificationservice.custom.CustomUserEntity;
import com.stsl.notificationservice.response.BaseResponse;
import com.stsl.notificationservice.service.NotificationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


@RestController
@RequestMapping("notification")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }


    @GetMapping("ping")
    public String ping(@ApiIgnore @AuthenticationPrincipal CustomUserEntity customUserEntity) {
        return "Hello " + customUserEntity.getName();
    }

    @PutMapping(value = "mark_as_read/{id}")
    public BaseResponse updateNotification(@PathVariable long id) {
        return notificationService.updateNotification(id);
    }

    @GetMapping()
    public BaseResponse getAllNotifications() {
        return notificationService.getAllNotifications();
    }



    @DeleteMapping(value = "{id}")
    public BaseResponse deleteNotification(@PathVariable long id) {
        return notificationService.deleteNotification(id);
    }


    @GetMapping(value = "user")
    public BaseResponse getNotificationsByUserId(@ApiIgnore @AuthenticationPrincipal CustomUserEntity customUserEntity) {
        return notificationService.getNotificationsByUserId(customUserEntity.getUid());
    }

    @GetMapping(value = "user/unread")
    public BaseResponse getUnreadNotificationsByUserId(@ApiIgnore @AuthenticationPrincipal CustomUserEntity customUserEntity) {
        return notificationService.getUnreadNotificationsByUserId(customUserEntity.getUid());
    }

}
