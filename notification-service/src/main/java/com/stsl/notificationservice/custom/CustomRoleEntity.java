package com.stsl.notificationservice.custom;


import com.stsl.notificationservice.entity.BaseEntity;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
