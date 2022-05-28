package com.stsl.employeeservice.controller;

import com.stsl.employeeservice.custom.CustomUserEntity;
import com.stsl.employeeservice.entity.enums.Status;
import com.stsl.employeeservice.response.BaseResponse;
import com.stsl.employeeservice.response.NewQueryRequest;
import com.stsl.employeeservice.service.QueryService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("query")
public class QueryController {

    private final QueryService queryService;

    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }


    @PostMapping("send")
    public BaseResponse createQuery(@RequestBody NewQueryRequest request) {
        return queryService.createQuery(request);
    }

    @GetMapping("user")
    public BaseResponse getUserEmployeeQuery(@ApiIgnore @AuthenticationPrincipal CustomUserEntity customUserEntity) {
        return queryService.getUserEmployeeQuery(customUserEntity.getUid());
    }


    @GetMapping()
    public BaseResponse getEmployeeQueries() {
        return queryService.getEmployeeQueries();
    }


    @PutMapping("manage/{id}/{status}")
    public BaseResponse updateEmployeeData(@PathVariable Long id, @PathVariable Status status) {
        return queryService.manageEmployeeQuery(id, status);
    }

    @PutMapping("update/{id}")
    public BaseResponse updateEmployeeQuery(@PathVariable Long id, @RequestParam String queryResponse, @ApiIgnore @AuthenticationPrincipal CustomUserEntity customUserEntity) {
        return queryService.updateEmployeeQuery(id, queryResponse, customUserEntity.getUsername());
    }


}
