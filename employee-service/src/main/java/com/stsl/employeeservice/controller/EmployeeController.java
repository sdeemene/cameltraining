package com.stsl.employeeservice.controller;

import com.stsl.employeeservice.custom.CustomUserEntity;
import com.stsl.employeeservice.response.BaseResponse;
import com.stsl.employeeservice.response.NewEmployeeRequest;
import com.stsl.employeeservice.service.EmployeeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("data")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    @PostMapping()
    public BaseResponse createEmployeeData(@ApiIgnore @AuthenticationPrincipal CustomUserEntity customUserEntity, @RequestBody NewEmployeeRequest request){
        return  employeeService.createEmployeeData(customUserEntity, request);
    }

    @GetMapping("user")
    public BaseResponse getUserEmployeeData(@ApiIgnore @AuthenticationPrincipal CustomUserEntity customUserEntity){
        return  employeeService.getUserEmployeeDataByUserAuthId(customUserEntity.getUid());
    }

    @GetMapping("{id}")
    public BaseResponse getUserEmployeeData(@PathVariable(name = "id") String employeeId){
        return  employeeService.getUserEmployeeDataByEmployeeId(employeeId);
    }


    @GetMapping()
    public BaseResponse getEmployeeData(){
        return  employeeService.getEmployeeData();
    }


    @PutMapping("{id}")
    public BaseResponse updateEmployeeData(@PathVariable Long id, @RequestBody NewEmployeeRequest request, @ApiIgnore @AuthenticationPrincipal CustomUserEntity customUserEntity){
        return  employeeService.updateEmployeeData(id, request, customUserEntity.getUsername());
    }



}
