package com.stsl.notificationservice.custom;


import com.stsl.notificationservice.entity.BaseEntity;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor


public class CustomUserEntity extends BaseEntity implements UserDetails  {


    private String username;

    private String lastname;

    private String firstname;


    private String password;


    private Collection<CustomRoleEntity> roles;

    private boolean enabled = true;


    private boolean accountNonLocked = true;


    private boolean accountNonExpired = true;


    private boolean credentialsNonExpired = true;

    private String name;

    public String getName() {
        return String.format("%s %s", getLastname(), getFirstname());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "read");
    }

}
