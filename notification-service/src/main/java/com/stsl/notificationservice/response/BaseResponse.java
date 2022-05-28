package com.stsl.notificationservice.response;

import lombok.Data;

@Data
public class BaseResponse {
    private int statusCode;

    private String description;

    private Object data;

    private Object errors;
}
