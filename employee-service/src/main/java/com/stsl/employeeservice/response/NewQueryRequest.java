package com.stsl.employeeservice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewQueryRequest {

    private String authUserId;

    private String authUserName;

    private String authUserEmail;

    private String reason;

    private String description;

}
