package com.stsl.employeeservice.service;


import com.stsl.employeeservice.custom.CustomUserEntity;
import com.stsl.employeeservice.entity.EmployeeEntity;
import com.stsl.employeeservice.entity.QueryEntity;
import com.stsl.employeeservice.entity.enums.Status;
import com.stsl.employeeservice.repository.EmployeeRepository;
import com.stsl.employeeservice.repository.QueryRepository;
import com.stsl.employeeservice.response.BaseResponse;

import com.stsl.employeeservice.response.NewNotificationRequest;
import com.stsl.employeeservice.response.NewQueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@Slf4j
public class QueryService {

    private final QueryRepository queryRepository;
    private final ProducerTemplate producerTemplate;
    private final EmployeeRepository employeeRepository;

    private final ResourceLoader resourceLoader;

    public QueryService(QueryRepository queryRepository, ProducerTemplate producerTemplate, EmployeeRepository employeeRepository, ResourceLoader resourceLoader) {
        this.queryRepository = queryRepository;
        this.producerTemplate = producerTemplate;
        this.employeeRepository = employeeRepository;
        this.resourceLoader = resourceLoader;
    }


    public BaseResponse createQuery(NewQueryRequest request) {
        BaseResponse response = new BaseResponse();
        Optional<EmployeeEntity> employeeEntity = employeeRepository.findByAuthUserId(request.getAuthUserId());
        if (employeeEntity.isPresent()) {
            QueryEntity queryEntity = QueryEntity.builder()
                    .authUserId(request.getAuthUserId())
                    .authUserName(request.getAuthUserName())
                    .employeeId(employeeEntity.get().getEmployeeId())
                    .status(Status.PENDING)
                    .reason(request.getReason())
                    .description(request.getDescription())
                    .dateCreated(LocalDate.now())
                    .timeCreated(LocalTime.now())
                    .dateUpdated(LocalDate.now())
                    .timeUpdated(LocalTime.now())
                    .build();
            queryRepository.save(queryEntity);
            response.setDescription("Employee Query created.");
            response.setStatusCode(HttpStatus.OK.value());
            response.setData(queryEntity.getEmployeeId());

            NewNotificationRequest newNotificationRequest = NewNotificationRequest.builder()
                    .authUserId(queryEntity.getAuthUserId())
                    .authUserName(queryEntity.getAuthUserName())
                    .authUserEmail(request.getAuthUserEmail())
                    .subject(request.getReason())
                    .body(request.getDescription())
                    .withInternal("Yes")
                    .withEmail("Yes").build();
            producerTemplate.sendBody("direct:sendNewNotification", newNotificationRequest);
        } else {
            response.setDescription("Employee record not found.");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
        }
        return response;
    }

    public BaseResponse getEmployeeQueries() {
        BaseResponse response = new BaseResponse();
        List<QueryEntity> queryEntityList = queryRepository.findAll();
        if (!queryEntityList.isEmpty()) {
            response.setDescription("Records found.");
            response.setStatusCode(HttpStatus.OK.value());
            response.setData(queryEntityList);
        } else {
            response.setDescription("No record found.");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
        }
        return response;
    }


    public BaseResponse updateEmployeeQuery(Long id, String queryResponse, String email) {
        BaseResponse response = new BaseResponse();
        Optional<QueryEntity> queryEntity = queryRepository.findById(id);
        if (queryEntity.isPresent()) {
            queryEntity.get().setResponse(queryResponse);
            queryEntity.get().setStatus(Status.RESPONDED);
            queryEntity.get().setDateUpdated(LocalDate.now());
            queryEntity.get().setTimeUpdated(LocalTime.now());
            queryRepository.save(queryEntity.get());
            response.setDescription("Query responded successfully.");
            response.setStatusCode(HttpStatus.OK.value());
            response.setData(queryEntity.get());

            String body = "Hi " + queryEntity.get().getAuthUserName() + ", \n\nYour " + queryEntity.get().getResponse() + " has been sent and you will be notified once acted upon. \n\nThank you. \n\nCheers \nSTSL - Team.";
            NewNotificationRequest newNotificationRequest = NewNotificationRequest.builder()
                    .authUserId(queryEntity.get().getAuthUserId())
                    .authUserName(queryEntity.get().getAuthUserName())
                    .authUserEmail(email)
                    .subject("Employee Query Response - " + queryEntity.get().getReason())
                    .body(body)
                    .withInternal("Yes")
                    .withEmail("Yes").build();
            producerTemplate.sendBody("direct:sendQueryResponse", newNotificationRequest);
        } else {
            response.setDescription("Employee record not found.");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
        }
        return response;
    }


    public BaseResponse getUserEmployeeQuery(String authUserId) {
        BaseResponse response = new BaseResponse();
        List<QueryEntity> queryEntityList = queryRepository.findByAuthUserId(authUserId);
        if (!queryEntityList.isEmpty()) {
            response.setDescription("Employee query records found.");
            response.setStatusCode(HttpStatus.OK.value());
            response.setData(queryEntityList);
        } else {
            response.setDescription("Employee record not found.");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
        }
        return response;
    }

    public BaseResponse manageEmployeeQuery(Long id, Status status) {
        BaseResponse response = new BaseResponse();
        Optional<QueryEntity> queryEntity = queryRepository.findById(id);
        if (queryEntity.isPresent()) {
            queryEntity.get().setStatus(status);
            queryEntity.get().setDateUpdated(LocalDate.now());
            queryEntity.get().setTimeUpdated(LocalTime.now());
            response.setDescription("Employee query record updated.");
            response.setStatusCode(HttpStatus.OK.value());
            response.setData(queryEntity.get());
        } else {
            response.setDescription("Employee query record not found.");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
        }
        return response;
    }

}
