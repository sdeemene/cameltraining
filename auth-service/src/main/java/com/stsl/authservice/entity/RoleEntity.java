package com.stsl.authservice.entity;


import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Table
@Entity(name = "camel_role")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RoleEntity extends BaseEntity implements GrantedAuthority {

    @Column(unique = true)
    private String name;

    private String description;

    @Override
    public String getAuthority() {
        return name;
    }
}
