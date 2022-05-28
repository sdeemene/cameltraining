package com.stsl.authservice.controller;

import com.stsl.authservice.response.BaseResponse;
import com.stsl.authservice.response.UpdateUserRequest;
import com.stsl.authservice.service.UserService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("login")
    public String getLoginPage(Model model) {
        return "login";
    }


    @DeleteMapping("{id}")
    public BaseResponse deleteUser(@PathVariable(name = "id") String authUserId) {
        return userService.deleteUser(authUserId);
    }

    @GetMapping("{id}")
    public BaseResponse getUser(@PathVariable(name = "id") String authUserId) {
        return userService.getUser(authUserId);
    }

    @GetMapping()
    public BaseResponse getUsers() {
        return userService.getAllUsers();
    }


    @PutMapping("{id}")
    public BaseResponse updateUser(@PathVariable(name = "id") String authUserId, @RequestBody UpdateUserRequest request) {
        return userService.updateUser(authUserId, request);
    }

    @PatchMapping("block/{id}")
    public BaseResponse blockUser(@PathVariable(name = "id") String authUserId) {
        return userService.blockUser(authUserId);
    }

    @PatchMapping("unblock/{id}")
    public BaseResponse ubBlockUser(@PathVariable(name = "id") String authUserId) {
        return userService.ubBlockUser(authUserId);
    }


}
