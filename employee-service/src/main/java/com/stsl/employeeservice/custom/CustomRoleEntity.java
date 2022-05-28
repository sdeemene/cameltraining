package com.stsl.employeeservice.custom;


import com.stsl.employeeservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CustomRoleEntity extends BaseEntity implements GrantedAuthority {

    @Column(unique = true)
    private String name;

    private String description;

    @Override
    public String getAuthority() {
        return name;
    }
}
