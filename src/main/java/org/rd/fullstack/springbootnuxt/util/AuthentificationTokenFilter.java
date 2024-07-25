/*
 * Copyright 2023, 2024; Réal Demers.
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

package org.rd.fullstack.springbootnuxt.util;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthentificationTokenFilter extends OncePerRequestFilter {

    private static final String  CST_AUTHORIZATION_HEADER = "Authorization";
    private static final Pattern CST_BEARER_PATTERN       = Pattern.compile("^Bearer (.+?)$"); // https://regex101.com/.
    private static final String  CST_API_URL_PATH         = "/api/**";    // URL for books API.
    private static final String  CST_REPORT_URL_PATH      = "/report/**"; // URL for books report.
    private static final String  CST_GRAPHPQL_URL_PATH    = "/graphql**"; // URL for GrapQL query.
    private static final String  CST_HEALTH_URL_PATH      = "/health/**"; // URL for health check.

    private static final Logger logger = LoggerFactory.getLogger(AuthentificationTokenFilter.class);

    private JwtUtils jwtUtils;
    private UserDetailsService userDetailsService;

    private final RequestMatcher uriApiMatcher;
    private final RequestMatcher uriReportMatcher;
    private final RequestMatcher uriGraphQLMatcher;
    private final RequestMatcher uriHealthMatcher;

    public AuthentificationTokenFilter() {
        super();
        uriApiMatcher     = new AntPathRequestMatcher(CST_API_URL_PATH);
        uriReportMatcher  = new AntPathRequestMatcher(CST_REPORT_URL_PATH);
        uriGraphQLMatcher = new AntPathRequestMatcher(CST_GRAPHPQL_URL_PATH);
        uriHealthMatcher  = new AntPathRequestMatcher(CST_HEALTH_URL_PATH);

        this.jwtUtils = null;
        this.userDetailsService = null;
    }

    public AuthentificationTokenFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        super();
        uriApiMatcher     = new AntPathRequestMatcher(CST_API_URL_PATH);
        uriReportMatcher  = new AntPathRequestMatcher(CST_REPORT_URL_PATH);
        uriGraphQLMatcher = new AntPathRequestMatcher(CST_GRAPHPQL_URL_PATH);
        uriHealthMatcher  = new AntPathRequestMatcher(CST_HEALTH_URL_PATH);

        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    public void setJwtUtils(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override 
    @SuppressWarnings("null")
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);

            // Check the "whitelist".
            // Valid API/JWT tokens should be in a VAULT.
            // Put your logic here.

            if (jwtUtils.validateJwtToken(jwt)) {
                UserDetails userDetails = ((UserDetailsServiceImpl) userDetailsService).loadUserByToken(jwt); // Notre implémentation.
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.debug("The request does not have a valid JWT token. The exception is: {}", ex);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    @SuppressWarnings("null")
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (uriApiMatcher.matcher(request).isMatch()     ||
            uriReportMatcher.matcher(request).isMatch()  || 
            uriGraphQLMatcher.matcher(request).isMatch() ||
            uriHealthMatcher.matcher(request).isMatch())
            return false;   // Don't forget... The question is "shouldNotFilter".
                            // This is a negation.
        return true;
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuthorization = request.getHeader(CST_AUTHORIZATION_HEADER);

        if (! StringUtils.hasText(headerAuthorization))
            throw new JwtException("The HTTP <Authorization Bearer> header is empty (or not present).");

        Optional<String> jetonJWT = Optional
            //.ofNullable(request.getHeader(CST_AUTHORIZATION_HEADER)) For this example ... Let's support without Bearer.
            .ofNullable(headerAuthorization)
            .filter(Predicate.not(String::isEmpty))
            .map(CST_BEARER_PATTERN::matcher)
            .filter(Matcher::find)
            .map(matcher -> matcher.group(1));

        if (jetonJWT.isEmpty()) {
            logger.warn("The HTTP <Authorization> header does not have the term <Bearer> to qualify the credentials.");
            return headerAuthorization;
        }

        return jetonJWT.get();
    }
}