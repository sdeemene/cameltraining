package com.stsl.attendanceservice.custom;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;


import java.util.Collection;
import java.util.Map;
import java.util.function.Function;


public class CustomAuthenticationToken<T> extends AbstractOAuth2TokenAuthenticationToken<Jwt> {

    private final String name;

    public CustomAuthenticationToken(Jwt jwt) {
        super(jwt);
        name = jwt.getTokenValue();
    }

    public CustomAuthenticationToken(Jwt jwt, Collection<? extends GrantedAuthority> authorities,
                                     Function<? super Jwt, CustomUserEntity> userFromJwtFunc) {
        super(jwt, userFromJwtFunc.apply(jwt), jwt, authorities);
        setAuthenticated(true);
        name = jwt.getTokenValue();
    }


    @Override
    public Map<String, Object> getTokenAttributes() {
        return getToken().getClaims();
    }

    @Override
    public String getName() {
        return name;
    }
}
