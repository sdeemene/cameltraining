package com.stsl.authservice.controller;

import com.stsl.authservice.response.BaseResponse;
import com.stsl.authservice.response.RegisterRequest;
import com.stsl.authservice.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("guest")
public class GuestController {

    private final UserService userService;

    public GuestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("register")
    public BaseResponse registerUser(@RequestBody RegisterRequest request) {
        return userService.registerUser(request);
    }
}
