package com.stsl.gatewayservice;

import com.stsl.gatewayservice.configuration.AppConfiguration;
import org.apache.commons.codec.binary.Base64;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@SpringBootApplication
@EnableEurekaClient
@EnableConfigurationProperties(value = AppConfiguration.class)
public class GatewayServiceApplication {


	private final String encodedClientCredentials;

	public GatewayServiceApplication(AppConfiguration appConfiguration) {
		byte[] clientCredentials = String.format("%s:%s", appConfiguration.getOauth().getClientId(), appConfiguration.getOauth().getClientSecret()).getBytes();
		encodedClientCredentials = String.format("Basic %s", Base64.encodeBase64String(clientCredentials));
	}



	public static void main(String[] args) {
		SpringApplication.run(GatewayServiceApplication.class, args);
	}


	@Bean
	SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {
		return http
				.csrf().disable()
				.authorizeExchange(
						authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().permitAll()
				).build();
	}


	@Bean
	public RouteLocator appRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("login", p -> p.order(-1)
						.path("/api/auth/oauth/token")
						.filters(f -> f.stripPrefix(2)
										.addRequestHeader(HttpHeaders.AUTHORIZATION, encodedClientCredentials)
//                                .addRequestParameter("grant_type", "password")// this was disabled to allow for refresh_token grant_type
						)
						.uri("lb://auth-service"))
				.build();
	}
}
