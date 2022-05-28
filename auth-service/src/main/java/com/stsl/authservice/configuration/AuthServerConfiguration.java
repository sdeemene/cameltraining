package com.stsl.authservice.configuration;




import com.stsl.authservice.custom.CustomAccessTokenConverter;
import com.stsl.authservice.custom.CustomTokenEnhancer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.util.List;


@Configuration
@EnableAuthorizationServer
public class AuthServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final AppConfiguration appConfiguration;

    private final PasswordEncoder passwordEncoder;

    private final ResourceLoader resourceLoader;


    public AuthServerConfiguration(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, AppConfiguration appConfiguration, PasswordEncoder passwordEncoder, ResourceLoader resourceLoader) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.appConfiguration = appConfiguration;
        this.passwordEncoder = passwordEncoder;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(appConfiguration.getOauth().getClientId())
                .secret(passwordEncoder.encode(appConfiguration.getOauth().getClientSecret()))
                .scopes(appConfiguration.getOauth().getScope())
                .autoApprove(true)
                .authorizedGrantTypes("password", "refresh_token", "authorization_code", "client_credentials")
                .redirectUris(appConfiguration.getOauth().getRedirectUris());
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .tokenStore(tokenStore())
                .accessTokenConverter(accessTokenConverter())
                .authenticationManager(authenticationManager)
                .tokenEnhancer(tokenEnhancer())
                .userDetailsService(userDetailsService);
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyPair());
        converter.setAccessTokenConverter(new CustomAccessTokenConverter());
        return converter;
    }

    @Bean
    public KeyPair keyPair() {
        AppConfiguration.AppKeyStore store = appConfiguration.getKeyStore();
        return new KeyStoreKeyFactory(
                resourceLoader.getResource(store.getPath()),
                store.getPassword().toCharArray())
                .getKeyPair(store.getPairName());
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(List.of(new CustomTokenEnhancer(appConfiguration), accessTokenConverter()));
        return tokenEnhancerChain;
    }

}
