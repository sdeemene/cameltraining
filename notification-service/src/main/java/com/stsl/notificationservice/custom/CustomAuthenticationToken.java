package com.stsl.notificationservice.custom;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public class CustomAuthenticationToken<T> extends AbstractOAuth2TokenAuthenticationToken<Jwt> {

    private final String name;

//    public CustomAuthenticationToken(Jwt jwt) {
//        super(jwt);
//        name = jwt.getSubject();
//    }

    public CustomAuthenticationToken(Jwt jwt, Collection<? extends GrantedAuthority> authorities,
                                     Function<? super Jwt, CustomUserEntity> userFromJwtFunc) {
        super(jwt, userFromJwtFunc.apply(jwt), jwt, authorities);
        setAuthenticated(true);
        name = jwt.getSubject();
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
