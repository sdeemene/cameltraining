package com.stsl.attendanceservice.controller;

import com.stsl.attendanceservice.custom.CustomUserEntity;
import com.stsl.attendanceservice.response.BaseResponse;
import com.stsl.attendanceservice.service.AttendanceService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("record")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("time-in")
    public BaseResponse createTimeIn(@ApiIgnore @AuthenticationPrincipal CustomUserEntity customUserEntity){
        return attendanceService.createTimeInAttendance(customUserEntity);
    }

    @PutMapping("time-out/{id}")
    public BaseResponse createTimeOut(@PathVariable Long id, @ApiIgnore @AuthenticationPrincipal CustomUserEntity customUserEntity){
        return attendanceService.createTimeOutAttendance(id, customUserEntity);
    }


    @GetMapping("user")
    public BaseResponse getUserAttendance(@ApiIgnore @AuthenticationPrincipal CustomUserEntity customUserEntity){
        return attendanceService.getUserAttendance(customUserEntity.getUid());
    }


    @GetMapping()
//    @PreAuthorize("hasRole('ROLE_Admin')")
    public BaseResponse getAttendances() {
        return attendanceService.getAttendances();
    }

}
