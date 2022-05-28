package com.stsl.authservice.service;


import com.stsl.authservice.entity.RoleEntity;
import com.stsl.authservice.repository.RoleRepository;
import com.stsl.authservice.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;


@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public BaseResponse createRole(String name) {
        BaseResponse response = new BaseResponse();
        var roleEntity = Optional.of(roleRepository.findByName(name)
                .map(roleRepository::save)
                .orElseGet(() -> roleRepository.save(RoleEntity.builder()
                        .name(name)
                        .description(name.toUpperCase(Locale.ROOT))
                        .build())));
        if (roleEntity.isPresent()) {
            response.setData(roleEntity.get());
            response.setStatusCode(HttpStatus.OK.value());
            response.setDescription("Role created");
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setDescription("Role not created");
        }
        return response;
    }

    public BaseResponse getRoles() {
        BaseResponse response = new BaseResponse();
        List<String> roleEntities = roleRepository.findAll().stream()
                .map(RoleEntity::getName).collect(Collectors.toList());
        if (!roleEntities.isEmpty()) {
            response.setData(roleEntities);
            response.setStatusCode(HttpStatus.OK.value());
            response.setDescription("Roles found");
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setDescription("Roles not found");
        }
        return response;
    }

    public BaseResponse getRoleByName(String name) {
        BaseResponse response = new BaseResponse();
        Optional<RoleEntity> roleEntity = roleRepository.findByName(name);
        if (roleEntity.isPresent()) {
            response.setData(roleEntity.get());
            response.setStatusCode(HttpStatus.OK.value());
            response.setDescription("Role found");
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setDescription("Role not found");
        }
        return response;
    }


}
