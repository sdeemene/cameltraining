package com.stsl.authservice.entity;

import com.stsl.authservice.entity.enums.Gender;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Table
@Entity(name = "camel_user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserEntity extends BaseEntity implements UserDetails  {

    @NotBlank
    @Column(unique = true)
    private String username;

    @NotBlank
    private String lastname;

    @NotBlank
    private String firstname;

    @NotBlank
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<RoleEntity> roles;

    @NotBlank
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Builder.Default
    private boolean enabled = true;

    @Builder.Default
    private boolean accountNonLocked = true;

    @Builder.Default
    private boolean accountNonExpired = true;

    @Builder.Default
    private boolean credentialsNonExpired = true;


    @Transient
    private String name;

    public String getName() {
        return String.format("%s %s", getLastname(), getFirstname());
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "read");
    }

}
