package com.stsl.authservice.response;

import com.stsl.authservice.entity.enums.Gender;
import lombok.Data;

@Data
public class RegisterRequest {

    private String username;

    private String password;

    private String lastname;

    private String firstname;

    private Gender gender;

    private String role;
}
