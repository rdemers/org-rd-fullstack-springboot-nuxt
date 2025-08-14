/*
 * Copyright 2023; Réal Demers.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.rd.fullstack.springbootnuxt.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Arrays;

import org.rd.fullstack.springbootnuxt.dto.ERole;
import org.rd.fullstack.springbootnuxt.util.AuthentificationTokenFilter;
import org.rd.fullstack.springbootnuxt.util.ExceptionHandlerAuthEntryPoint;
import org.rd.fullstack.springbootnuxt.util.JwtUtils;
import org.rd.fullstack.springbootnuxt.util.UserDetailsServiceImpl;
import org.rd.fullstack.springbootnuxt.util.UserUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.Filter;

@Configuration
@EnableMethodSecurity(
    securedEnabled = true,  // Determines whether the use of the @Secured annotation is allowed.
    jsr250Enabled = true,   // Determines whether using the @RoleAllowed annotation is allowed.
    prePostEnabled = true)  // Determines whether the use of Spring Security pre/post annotations is allowed.
public class SecurityConfig {

    private static final String[] AUTH_WHITELIST = {
        "/app/**",          // Our Nuxt/Vue app.
        "/jwt/**",          // JWT token management and generation.
        "/swagger-ui/**",   // API implementation through the use of Swagger.
        "/graphql/**",      // GraphQL API implementation. 
        "/graphiql/**",     // GraphQL API implementation.
        "/v3/api-docs/**",  // The API documentation (OpenAPI).
        "/actuator/**"      // Probe for liveness/readiness.
    };

    public SecurityConfig() {
        super();
    }

    @Value("${org.rd.fullstack.springbootnuxt.secret}")
    private String secret;

    @Value("${org.rd.fullstack.springbootnuxt.expiration}")
    private int expiration;

    @Value("${org.rd.fullstack.springbootnuxt.authorities}")
    private String authorities;

    @Bean
    JwtUtils jwtUtils() {
        return new JwtUtils(secret, expiration, authorities);
    }

    @Bean
    ExceptionHandlerAuthEntryPoint exceptionHandlingAuthEntryPoint() {
        return new ExceptionHandlerAuthEntryPoint();
    }

    @Bean
    UserUtils userUtils() {
        UserUtils userUtils = new UserUtils();
        PasswordEncoder passwordEncoder = passwordEncoder();

        userUtils.add("root", passwordEncoder.encode("root"),
                Arrays.asList(ERole.ROLE_SELECT, ERole.ROLE_INSERT, ERole.ROLE_UPDATE, ERole.ROLE_DELETE));

        userUtils.add("support", passwordEncoder.encode("support"),
                Arrays.asList(ERole.ROLE_SELECT, ERole.ROLE_UPDATE));

        userUtils.add("guest", passwordEncoder.encode("guest"),
                Arrays.asList(ERole.ROLE_SELECT));

        return userUtils;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
      return authConfig.getAuthenticationManager();
    }

    @Bean
    WebMvcConfigurer forwardToIndex() {
        return new WebMvcConfigurer() {
            @Override
            @SuppressWarnings("null")
            public void addViewControllers(ViewControllerRegistry registry) {

                // To access Swagger UX/UI interface.
                registry
                        .addViewController("/swagger-ui") 
                        .setViewName("redirect:/swagger-ui/index.html");
                
                // To access Nuxt UX/UI interface.
                // ----------------------------------------------------------------------
                // @EnableWebMvc annotation switch off all the things that Spring Boot does for you in WebMvcAutoConfiguration. 
                // You could remove that annotation, or you could add back the view controller that you switched off.
                //
                // IMPORTANT (SSR)
                // Must be synchronize with the configuration of Nuxt/Vue JS application.
                // See the following files :
                // - src/frontend/nuxt.config.ts
                // - src/frontend/plugins/vuetify.ts
                registry
                        .addViewController("/favicon.ico")
                        .setViewName("redirect:/app/favicon.ico");
                registry
                        .addViewController("/app")                        
                        .setViewName("redirect:/app/index.html");
                registry
                        .addViewController("/app/login")                        
                        .setViewName("redirect:/app/login/index.html");
            }
        };
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
        http.cors(withDefaults());

        http.csrf(csrf -> csrf
            .disable());

        http.authorizeHttpRequests(auth -> auth
            .requestMatchers(AUTH_WHITELIST).permitAll()
            .anyRequest().authenticated());

        http.exceptionHandling(except -> except
            .authenticationEntryPoint(exceptionHandlingAuthEntryPoint()));

        http.sessionManagement(sm -> sm
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authentificationTokenFilter(jwtUtils(), userDetailsService()),
                             UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private Filter authentificationTokenFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        return new AuthentificationTokenFilter(jwtUtils, userDetailsService);
    }
}