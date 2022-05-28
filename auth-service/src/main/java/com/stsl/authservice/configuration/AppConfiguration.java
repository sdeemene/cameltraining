package com.stsl.authservice.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@ConfigurationProperties("app")
@Data
@RefreshScope
public class AppConfiguration {

    private OauthClient oauth;

    private AppKeyStore keyStore;



    @Data
    public static  class OauthClient{
        private String clientId;

        private String clientSecret;

        private String redirectUris;

        private String issuer;

        private String scope;

    }

    @Data
    public static  class AppKeyStore{
        private String path;

        private String password;

        private String pairName;
    }
}
