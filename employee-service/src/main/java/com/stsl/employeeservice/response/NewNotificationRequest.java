package com.stsl.employeeservice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewNotificationRequest {

    private String authUserId;

    private String authUserName;

    private String authUserEmail;

    private String subject;

    private String body;

    private String withEmail;

    private String withInternal;

}
