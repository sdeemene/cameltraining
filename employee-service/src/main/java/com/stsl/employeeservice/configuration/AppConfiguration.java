package com.stsl.employeeservice.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@Data
@ConfigurationProperties("app")
@RefreshScope
public class AppConfiguration {

    private OauthClient oauth;

    private MailtrapConfig mailtrapConfig;

    private MTrapConfig mTrapConfig;

    @Data
    public static  class OauthClient{
        private String uri;
    }


    @Data
    public static class MailtrapConfig {
        private String host;

        private Integer port;

        private String username;

        private String password;

        private String protocol;
    }

    @Data
    public static class MTrapConfig {
        private String from;
    }
}
