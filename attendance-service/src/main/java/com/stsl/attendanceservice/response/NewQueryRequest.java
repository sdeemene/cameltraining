package com.stsl.attendanceservice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
