package com.stsl.authservice.custom;

import com.stsl.authservice.configuration.AppConfiguration;
import com.stsl.authservice.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomTokenEnhancer implements TokenEnhancer {

    private final AppConfiguration appConfiguration;


    public CustomTokenEnhancer(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Map<String, Object> additionalInfo = new HashMap<>();
        UserEntity userEntity = (UserEntity) authentication.getUserAuthentication().getPrincipal();
        additionalInfo.put("username", userEntity.getUsername());
        additionalInfo.put("iss", appConfiguration.getOauth().getIssuer());
        additionalInfo.put("name", userEntity.getName());
        additionalInfo.put("uid", userEntity.getUid());
        additionalInfo.put("firstname", userEntity.getFirstname());
        additionalInfo.put("lastname", userEntity.getLastname());
        additionalInfo.put("description", "Login Successful");
        additionalInfo.put("roles", userEntity.getRoles().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toSet()));
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }


}
