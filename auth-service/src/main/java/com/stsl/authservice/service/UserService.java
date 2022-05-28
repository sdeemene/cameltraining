package com.stsl.authservice.service;

import com.stsl.authservice.entity.RoleEntity;
import com.stsl.authservice.entity.UserEntity;
import com.stsl.authservice.repository.RoleRepository;
import com.stsl.authservice.repository.UserRepository;
import com.stsl.authservice.response.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ProducerTemplate template;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, ProducerTemplate template) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.template = template;
    }


    public BaseResponse registerUser(RegisterRequest request) {
        BaseResponse response = new BaseResponse();
        var user = userRepository.findByUsername(request.getUsername());
        if (user.isEmpty()) {
            Optional<RoleEntity> roleEntity = roleRepository.findByName(request.getRole());
            if (roleEntity.isPresent()) {
                UserEntity userEntity = UserEntity.builder()
                        .firstname(request.getFirstname())
                        .lastname(request.getLastname())
                        .gender(request.getGender())
                        .username(request.getUsername())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .enabled(true)
                        .accountNonExpired(true)
                        .accountNonLocked(true)
                        .credentialsNonExpired(true)
                        .roles(List.of(roleEntity.get()))
                        .build();
                userRepository.save(userEntity);
                response.setData(userEntity);
                response.setStatusCode(HttpStatus.OK.value());
                response.setDescription("User created successfully.");
                //send registration email and in-app notification
                String body = "Hi " + userEntity.getName() + ", \n\nYour account has been created successfully. Please complete your employee data for your EmployeeId. \n\nWelcome to STSL. \n\nCheers \nSTSL - Team.";
                NewNotificationRequest newNotificationRequest = NewNotificationRequest.builder()
                        .authUserId(userEntity.getUid())
                        .authUserName(userEntity.getName())
                        .authUserEmail(userEntity.getUsername())
                        .subject("Account Creation")
                        .body(body)
                        .withInternal("Yes")
                        .withEmail("Yes").build();
                template.sendBody("direct:sendRegistrationData", newNotificationRequest);
            }else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setDescription("User Role does not exist.");
            }
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setDescription("User with the email provided exist.");
        }
        return response;
    }


    public BaseResponse getUser(String authUserId) {
        BaseResponse response = new BaseResponse();
        var user = userRepository.findByUid(authUserId);
        if (user.isPresent()) {
            response.setData(user);
            response.setStatusCode(HttpStatus.OK.value());
            response.setDescription("User Found");
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setDescription("User not found");
        }
        return response;
    }

    public BaseResponse getAllUsers() {
        BaseResponse response = new BaseResponse();
        List<UserEntity> userEntityList = userRepository.findAll();
        if (!userEntityList.isEmpty()) {
            response.setData(userEntityList);
            response.setStatusCode(HttpStatus.OK.value());
            response.setDescription("User Found");
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setDescription("Users not found");
        }
        return response;
    }

    public BaseResponse deleteUser(String authUserId) {
        BaseResponse response = new BaseResponse();
        var user = userRepository.findByUid(authUserId);
        if (user.isPresent()) {
            userRepository.delete(user.get());
            response.setStatusCode(HttpStatus.OK.value());
            response.setDescription("User deleted successfully");
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setDescription("User not found");
        }

        return response;
    }

    public BaseResponse updateUser(String authUserId, UpdateUserRequest request) {
        BaseResponse response = new BaseResponse();
        Optional<UserEntity> userEntity = userRepository.findByUid(authUserId);
        if (userEntity.isPresent()) {
            userEntity.get().setFirstname(request.getFirstname());
            userEntity.get().setLastname(request.getLastname());
            userRepository.save(userEntity.get());
            response.setStatusCode(HttpStatus.OK.value());
            response.setDescription("User updated successfully");
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setDescription("User not found");
        }
        return response;
    }


    public BaseResponse blockUser(String authUserId) {
        BaseResponse response = new BaseResponse();
        Optional<UserEntity> userEntity = userRepository.findByUid(authUserId);
        if (userEntity.isPresent()) {
            userEntity.get().setEnabled(false);
            userRepository.save(userEntity.get());
            response.setStatusCode(HttpStatus.OK.value());
            response.setDescription("User account disabled successfully");
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setDescription("User not found");
        }
        return response;
    }

    public BaseResponse ubBlockUser(String authUserId) {
        BaseResponse response = new BaseResponse();
        Optional<UserEntity> userEntity = userRepository.findByUid(authUserId);
        if (userEntity.isPresent()) {
            userEntity.get().setEnabled(true);
            userRepository.save(userEntity.get());
            response.setStatusCode(HttpStatus.OK.value());
            response.setDescription("User account enabled successfully");
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setDescription("User not found");
        }
        return response;
    }
}
