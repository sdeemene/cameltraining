package com.stsl.employeeservice.service;

import com.stsl.employeeservice.custom.CustomUserEntity;
import com.stsl.employeeservice.entity.EmployeeEntity;
import com.stsl.employeeservice.repository.EmployeeRepository;
import com.stsl.employeeservice.response.BaseResponse;
import com.stsl.employeeservice.response.NewEmployeeRequest;
import com.stsl.employeeservice.response.NewNotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final ProducerTemplate producerTemplate;


    public EmployeeService(EmployeeRepository employeeRepository, ProducerTemplate producerTemplate) {
        this.employeeRepository = employeeRepository;
        this.producerTemplate = producerTemplate;
    }

    public BaseResponse createEmployeeData(CustomUserEntity customUserEntity, NewEmployeeRequest request) {
        BaseResponse response = new BaseResponse();
        Period period = Period.between(request.getDateOfBirth(), LocalDate.now());
        String employeeId = "STSL-" + generateCode(4);
        EmployeeEntity employeeEntity = EmployeeEntity.builder()
                .authUserId(customUserEntity.getUid())
                .authUserName(customUserEntity.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .employeeId(employeeId)
                .dateOfBirth(request.getDateOfBirth())
                .age(period.getYears())
                .currentSalary(request.getCurrentSalary())
                .lastEmployer(request.getLastEmployer())
                .qualification(request.getCurrentQualification())
                .designation(request.getDesignation())
                .profession(request.getProfession())
                .dateOfEmployment(request.getDateOfEmployment())
                .dateCreated(LocalDate.now())
                .timeCreated(LocalTime.now())
                .dateUpdated(LocalDate.now())
                .timeUpdated(LocalTime.now())
                .build();
        employeeRepository.save(employeeEntity);
        response.setDescription("Employee record created.");
        response.setStatusCode(HttpStatus.OK.value());
        response.setData(employeeEntity);

        String body = "Hi " + employeeEntity.getAuthUserName() + ", \n\nYour employee record has been created successfully.\n\nYour Employee ID is: " + employeeEntity.getEmployeeId() + ". \n\nWelcome to STSL. \n\nCheers \nSTSL - Team.";
        NewNotificationRequest newNotificationRequest = NewNotificationRequest.builder()
                .authUserId(employeeEntity.getAuthUserId())
                .authUserName(employeeEntity.getAuthUserName())
                .authUserEmail(customUserEntity.getUsername())
                .subject("Employee Record Creation")
                .body(body)
                .withInternal("Yes")
                .withEmail("Yes").build();
        producerTemplate.sendBody("direct:sendNewNotification", newNotificationRequest);
        return response;
    }

    public BaseResponse getEmployeeData() {
        BaseResponse response = new BaseResponse();
        List<EmployeeEntity> employeeEntityList = employeeRepository.findAll();
        if (!employeeEntityList.isEmpty()) {
            response.setDescription("Records found.");
            response.setStatusCode(HttpStatus.OK.value());
            response.setData(employeeEntityList);
        } else {
            response.setDescription("No record found.");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
        }
        return response;
    }


    public BaseResponse updateEmployeeData(Long id, NewEmployeeRequest request, String email) {
        BaseResponse response = new BaseResponse();
        Optional<EmployeeEntity> employeeEntity = employeeRepository.findById(id);
        if (employeeEntity.isPresent()) {
            employeeEntity.get().setCurrentSalary(request.getCurrentSalary());
            employeeEntity.get().setPhone(request.getPhone());
            employeeEntity.get().setAddress(request.getAddress());
            employeeEntity.get().setQualification(request.getCurrentQualification());
            employeeEntity.get().setDesignation(request.getDesignation());
            employeeEntity.get().setDateUpdated(LocalDate.now());
            employeeEntity.get().setTimeUpdated(LocalTime.now());
            employeeRepository.save(employeeEntity.get());
            response.setDescription("Employee record updated.");
            response.setStatusCode(HttpStatus.OK.value());
            response.setData(employeeEntity.get());

            String body = "Hi " + employeeEntity.get().getAuthUserName() + ", \n\nYour employee record has been updated successfully. \n\nWelcome to STSL. \n\nCheers \nSTSL - Team.";
            NewNotificationRequest newNotificationRequest = NewNotificationRequest.builder()
                    .authUserId(employeeEntity.get().getAuthUserId())
                    .authUserName(employeeEntity.get().getAuthUserName())
                    .authUserEmail(email)
                    .subject("Employee Record Update")
                    .body(body)
                    .withInternal("Yes")
                    .withEmail("Yes").build();
            producerTemplate.sendBody("direct:sendNewNotification", newNotificationRequest);
        } else {
            response.setDescription("Employee record not found.");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
        }
        return response;
    }


    public BaseResponse getUserEmployeeDataByUserAuthId(String authUserId) {
        BaseResponse response = new BaseResponse();
        Optional<EmployeeEntity> employeeEntity = employeeRepository.findByAuthUserId(authUserId);
        if (employeeEntity.isPresent()) {
            response.setDescription("Employee record created.");
            response.setStatusCode(HttpStatus.OK.value());
            response.setData(employeeEntity.get());
        } else {
            response.setDescription("Employee record not found.");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
        }
        return response;
    }


    public String generateCode(int count) {
        String verificationCode = randomNumeric(count);
        Optional<EmployeeEntity> employeeEntity = employeeRepository.findByEmployeeId(verificationCode);
        if (employeeEntity.isPresent()) {
            verificationCode = generateCode(count);
        }
        return verificationCode;
    }

    public String randomNumeric(int count) {
        String generatedString = RandomStringUtils.randomNumeric(count).trim();
        return !generatedString.isEmpty() && generatedString.length() == count ? generatedString : this.randomNumeric(count);
    }

    public BaseResponse getUserEmployeeDataByEmployeeId(String employeeId) {
        BaseResponse response = new BaseResponse();
        Optional<EmployeeEntity> employeeEntity = employeeRepository.findByEmployeeId(employeeId);
        if (employeeEntity.isPresent()) {
            response.setDescription("Employee record created.");
            response.setStatusCode(HttpStatus.OK.value());
            response.setData(employeeEntity.get());
        } else {
            response.setDescription("Employee record not found.");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
        }
        return response;
    }
}
