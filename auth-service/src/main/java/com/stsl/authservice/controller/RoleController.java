package com.stsl.authservice.controller;


import com.stsl.authservice.response.BaseResponse;
import com.stsl.authservice.service.RoleService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }


    @GetMapping("{name}")
    public BaseResponse findRoleByName(@PathVariable String name) {
        return roleService.getRoleByName(name);
    }

    @GetMapping()
    public BaseResponse getRoles() {
        return roleService.getRoles();
    }

    @PostMapping()
    public BaseResponse createRole(@RequestParam String name) {
        return roleService.createRole(name);
    }

}
