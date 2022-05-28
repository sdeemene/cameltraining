package com.stsl.authservice.response;


import lombok.Data;

@Data
public class UpdateUserRequest {

    private String lastname;

    private String firstname;
}
