package com.stsl.gatewayservice.configuration;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("app")
public class AppConfiguration {
    private OauthClient oauth;


    @Data
    public static class OauthClient {
        private String clientId;
        private String clientSecret;
    }
}
