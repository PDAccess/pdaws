package com.h2h.pda.jwt;

import com.h2h.pda.service.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;


@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurity extends WebSecurityConfigurerAdapter {
    private Logger log = LoggerFactory.getLogger(WebSecurity.class);

    @Autowired
    SessionService sessionService;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    UsersOps usersOps;

    @Autowired
    MfaService mfaService;

    @Autowired
    ActionPdaService actionPdaService;

    @Autowired
    @Qualifier("PDAwsAuthenticationProvider")
    AuthenticationProvider pdAwsAuthenticationProvider;

    @Autowired
    @Qualifier("InternalAuthenticationProvider")
    AuthenticationProvider internalAuthenticationProvider;

    @Autowired
    @Qualifier("LDAPAuthenticationProvider")
    AuthenticationProvider ldapAuthenticationProvider;

    @Autowired
    CustomAccessDeniedHandler customAccessDeniedHandler;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/api/mfa/check").permitAll()
                .antMatchers("/api/mfa/auth").permitAll()
                .antMatchers("/api/user/resetPassword").permitAll()
//                .antMatchers("/api/v1/action/pushcommand").permitAll()
                .antMatchers("/api/qrDBControl").permitAll()
                .antMatchers("/api/qrMatch").permitAll()
                .antMatchers("/api/v1/system/version").permitAll()
                .antMatchers("/api/rest/notification").permitAll()
                .antMatchers("/rest/serviceDown/*").permitAll()
                .antMatchers("/api/serviceDown/*").permitAll()
                .antMatchers("/ws/*").permitAll()
                .antMatchers("/ws/socket/**").permitAll()
                // new paths.
                .antMatchers("/api/v1/system/status/**").permitAll()
                .antMatchers("/api/v1/tag/**").permitAll()
                .antMatchers("/api/v1/vault/*").permitAll()
                .antMatchers("/api/v1/share/link/*").permitAll()
                .antMatchers("/api/v1/mfa/*").permitAll()
                .antMatchers("/api/v1/user/password/reset").permitAll()
                .antMatchers("/api/v1/ldap/external/**").permitAll()
                .antMatchers("/api/v1/internal/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), sessionService, usersOps, mfaService, actionPdaService))
                .addFilter(new JWTAuthorizationFilter(authenticationManager()))
                // this disables session creation on Spring Security
                .exceptionHandling().accessDeniedHandler(customAccessDeniedHandler)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationEventPublisher(authenticationService)
                .authenticationProvider(pdAwsAuthenticationProvider)
                .authenticationProvider(internalAuthenticationProvider)
                .authenticationProvider(ldapAuthenticationProvider).eraseCredentials(false);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "Origin"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}