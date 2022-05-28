package com.stsl.employeeservice.custom;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();


    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(), extractResourceRoles(jwt).stream())
                .collect(Collectors.toSet());
        return new CustomAuthenticationToken(jwt, authorities, this::getUserFromJwt);
    }


    public static Collection<? extends GrantedAuthority> extractResourceRoles(final Jwt jwt) {
        Collection<String> userRoles = jwt.getClaimAsStringList("authorities");
        if (!CollectionUtils.isEmpty(userRoles)) {
            return userRoles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }


    public CustomUserEntity getUserFromJwt(Object jwtObj) {
        Jwt jwt = (Jwt) jwtObj;
        CustomUserEntity customUserEntity = new CustomUserEntity();
        customUserEntity.setUsername(jwt.getSubject());
        customUserEntity.setName(jwt.getClaim("name"));
        customUserEntity.setUid(jwt.getClaim("uid"));
        customUserEntity.setFirstname(jwt.getClaim("firstname"));
        customUserEntity.setLastname(jwt.getClaim("lastname"));
        customUserEntity.setUsername(jwt.getClaim("username"));
        customUserEntity.setRoles(jwt.getClaim("roles"));
        return customUserEntity;
    }
}
