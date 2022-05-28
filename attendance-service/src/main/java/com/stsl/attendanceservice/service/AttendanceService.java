package com.stsl.attendanceservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stsl.attendanceservice.custom.CustomUserEntity;
import com.stsl.attendanceservice.entity.AttendanceEntity;
import com.stsl.attendanceservice.repository.AttendanceRepository;
import com.stsl.attendanceservice.response.BaseResponse;
import com.stsl.attendanceservice.response.NewNotificationRequest;
import com.stsl.attendanceservice.response.NewQueryRequest;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final ProducerTemplate producerTemplate;

    public AttendanceService(AttendanceRepository attendanceRepository, ProducerTemplate producerTemplate) {
        this.attendanceRepository = attendanceRepository;
        this.producerTemplate = producerTemplate;
    }

    public BaseResponse createTimeInAttendance(CustomUserEntity customUserEntity) {
        BaseResponse response = new BaseResponse();
        AttendanceEntity attendanceEntity = AttendanceEntity.builder()
                .authUserId(customUserEntity.getUid())
                .timeCreated(LocalTime.now())
                .dateCreated(LocalDate.now())
                .timeIn(LocalTime.now())
                .dayOfWeek(new SimpleDateFormat("EEEEE").format(new Date()))
                .build();
        attendanceRepository.save(attendanceEntity);
        response.setData(attendanceEntity);
        response.setStatusCode(HttpStatus.OK.value());
        response.setDescription("Employee clock-in time created.");
        String formattedTimeIn = getFormattedTime(attendanceEntity.getTimeIn());
        String body = "Hi " + customUserEntity.getName() + ", \n\nYou are welcome to work today. We are happy to see you. Ensure you visit the cafe at 12noon for your lunch. You resumed today by " + formattedTimeIn + ".\n\nCheers \nSTSL - Team.";
        NewNotificationRequest newNotificationRequest = NewNotificationRequest.builder()
                .authUserId(customUserEntity.getUid())
                .authUserName(customUserEntity.getName())
                .authUserEmail(customUserEntity.getUsername())
                .subject("Employee Clock In Time")
                .body(body)
                .withInternal("Yes")
                .withEmail("Yes").build();
        producerTemplate.sendBody("direct:sendTimeInData", newNotificationRequest);

        return response;
    }


    private String getFormattedTime(LocalTime localTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a");
        return localTime.format(dateTimeFormatter);
    }


    public BaseResponse createTimeOutAttendance(Long Id, CustomUserEntity customUserEntity) {
        BaseResponse response = new BaseResponse();
        Optional<AttendanceEntity> attendanceEntity = attendanceRepository.findById(Id);
        if (attendanceEntity.isPresent()) {
            Duration hoursWorks = Duration.between(attendanceEntity.get().getTimeIn(), LocalTime.now());
            log.info("hoursWorked {}", hoursWorks);
            attendanceEntity.get().setTimeOut(LocalTime.now());
            attendanceEntity.get().setTotalHours(hoursWorks.toHours());
            attendanceEntity.get().setTotalDaysPresent(attendanceEntity.get().getTotalDaysPresent() + 1L);
            attendanceRepository.save(attendanceEntity.get());
            response.setData(attendanceEntity.get());
            response.setStatusCode(HttpStatus.OK.value());
            String formattedTimeIn = getFormattedTime(attendanceEntity.get().getTimeIn());
            String formattedTimeOut = getFormattedTime(attendanceEntity.get().getTimeOut());
            response.setDescription("Employee clock-out time created.");
            String body = "Hi " + customUserEntity.getName() + ", \n\nYou have closed for the day. Thank you for coming. Hope you had your lunch because we care so much about your welfare, here at STLS. \n\nYou resumed today at " + formattedTimeIn + " and you are closing today at " + formattedTimeOut + ".\n\nCheers \nSTSL - Team.";
            NewNotificationRequest newNotificationRequest = NewNotificationRequest.builder()
                    .authUserId(customUserEntity.getUid())
                    .authUserName(customUserEntity.getName())
                    .authUserEmail(customUserEntity.getUsername())
                    .subject("Employee Clock Out Time")
                    .body(body)
                    .withInternal("Yes")
                    .withEmail("Yes").build();
            String token = SecurityContextHolder.getContext().getAuthentication().getName();
            producerTemplate.sendBodyAndProperty("direct:sendTimeOutData", newNotificationRequest, "token", token);
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setDescription("Attendance not found.");
        }
        return response;
    }

    public BaseResponse getUserAttendance(String authUserId) {
        BaseResponse response = new BaseResponse();
        List<AttendanceEntity> attendanceEntityList = attendanceRepository.findByAuthUserId(authUserId);
        if (!attendanceEntityList.isEmpty()) {
            attendanceEntityList.sort(Comparator.comparing(AttendanceEntity::getTimeIn).thenComparing(AttendanceEntity::getDateCreated).reversed());
            response.setData(attendanceEntityList);
            response.setStatusCode(HttpStatus.OK.value());
            response.setDescription("Records found.");
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setDescription("No record found.");
        }
        return response;
    }

    public BaseResponse getAttendances() {
        BaseResponse response = new BaseResponse();
        List<AttendanceEntity> attendanceEntityList = attendanceRepository.findAll();
        if (!attendanceEntityList.isEmpty()) {
            attendanceEntityList.sort(Comparator.comparing(AttendanceEntity::getTimeIn).thenComparing(AttendanceEntity::getDateCreated).reversed());
            response.setData(attendanceEntityList);
            response.setStatusCode(HttpStatus.OK.value());
            response.setDescription("Records found.");
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setDescription("No record found.");
        }
        return response;
    }


    public BaseResponse getAttendanceById(Long id) {
        BaseResponse response = new BaseResponse();
        Optional<AttendanceEntity> attendanceEntityList = attendanceRepository.findById(id);
        if (attendanceEntityList.isPresent()) {
            response.setData(attendanceEntityList);
            response.setStatusCode(HttpStatus.OK.value());
            response.setDescription("Records found.");
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setDescription("No record found.");
        }
        return response;
    }


    @Async //to allow both the send and reply to on a single thread
    public NewNotificationRequest checkEmployeeClockOutTime(@ExchangeProperties Map<String, String> properties, @Body NewNotificationRequest newNotificationRequest) throws JsonProcessingException {
        Optional<AttendanceEntity> attendanceEntity = attendanceRepository.findByAuthUserIdAndDateCreated(newNotificationRequest.getAuthUserId(), LocalDate.now());
        String body = newNotificationRequest.getBody();
        if (attendanceEntity.isPresent() && attendanceEntity.get().getTotalHours() < 8) {
            String description = "Hi " + newNotificationRequest.getAuthUserName() + ", \n\nYou worked for " + attendanceEntity.get().getTotalHours() + " hour(s) and closed before the approved closing time, do well to respond to this query. You resumed at " + getFormattedTime(attendanceEntity.get().getTimeIn()) + " and you closed at " + getFormattedTime(attendanceEntity.get().getTimeOut()) + ".\n\nCheers \nSTSL - Team.";
            NewQueryRequest newQueryRequest = NewQueryRequest.builder()
                    .authUserId(attendanceEntity.get().getAuthUserId())
                    .authUserName(newNotificationRequest.getAuthUserName())
                    .authUserEmail(newNotificationRequest.getAuthUserEmail())
                    .reason("Early Closing Time")
                    .description(description)
                    .build();
//            producerTemplate.sendBodyAndProperty("direct:sendQueryData", newQueryRequest, "token", properties.get("token"));
//           Object response = producerTemplate.requestBodyAndHeader("direct:sendQueryData", ExchangePattern.InOut, newQueryRequest, "token", properties.get("token"), String.class);
            String response = producerTemplate.requestBodyAndHeader("direct:sendQueryData", newQueryRequest, HttpHeaders.AUTHORIZATION, "Bearer " + properties.get("token"), String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            BaseResponse baseResponse = objectMapper.readValue(response, BaseResponse.class);
            String employeeId = "";
            if (baseResponse.getStatusCode() == HttpStatus.OK.value()) {
                employeeId = (String) baseResponse.getData();
                body = "Hello TWIMC,  \nThe Employee - " + newNotificationRequest.getAuthUserName() + " with ID: " + employeeId + " worked for " + attendanceEntity.get().getTotalHours() + " hour(s) and closed for the day. \n\nResumption Time: " + getFormattedTime(attendanceEntity.get().getTimeIn()) + ".\nClosing Time: " + getFormattedTime(attendanceEntity.get().getTimeOut()) + ".\n\nCheers \nSTSL - Team.";
            }
        } else {
            body = "Hello TWIMC,  \n" + newNotificationRequest.getAuthUserName() + " worked for " + attendanceEntity.get().getTotalHours() + " hours and closed for the day. \n\nResumption Time : " + getFormattedTime(attendanceEntity.get().getTimeIn()) + " Closing Time: " + getFormattedTime(attendanceEntity.get().getTimeOut()) + ".\n\nCheers \nSTSL - Team.";
        }
        newNotificationRequest.setBody(body);
        return newNotificationRequest;
    }

}
