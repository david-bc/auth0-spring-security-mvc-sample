package com.auth0.example.security;

import com.auth0.AuthenticationController;
import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import java.io.UnsupportedEncodingException;

@SuppressWarnings("unused")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AppConfig extends WebSecurityConfigurerAdapter {
    /**
     * This is your auth0 domain (tenant you have created when registering with auth0 - account name)
     */
    @Value(value = "${com.auth0.domain}")
    private String domain;

    /**
     * This is the client id of your auth0 application (see Settings page on auth0 dashboard)
     */
    @Value(value = "${com.auth0.clientId}")
    private String clientId;

    /**
     * This is the client secret of your auth0 application (see Settings page on auth0 dashboard)
     */
    @Value(value = "${com.auth0.clientSecret}")
    private String clientSecret;

    @Value(value = "${com.auth0.apiToken}")
    private String apiToken;

    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver
                = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

    @Bean
    public AuthenticationController authenticationController() throws UnsupportedEncodingException {
        return AuthenticationController.newBuilder(domain, clientId, clientSecret)
                .withResponseType("code")
                .build();
    }

    @Bean
    public ManagementAPI managementAPI() {
        return new ManagementAPI(domain, apiToken);
    }

    @Bean
    public AuthAPI authAPI() {
        return new AuthAPI(domain, clientId, clientSecret);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http
                .authorizeRequests()
                .antMatchers("/callback", "/login").permitAll()
                .antMatchers("/**").authenticated()
                .and()
                .logout().permitAll();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
    }

    public String getDomain() {
        return domain;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
