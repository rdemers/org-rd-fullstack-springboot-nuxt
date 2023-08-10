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

package org.rd.fullstack.springbootnuxt.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.stream.Collectors;

import org.rd.fullstack.springbootnuxt.dto.ERole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    final private String CST_SECRET;
    @SuppressWarnings("unused")
	final private int    CST_EXPIRATION; // Not used. Activate as needed.
    final private String CST_AUTHORITIES;

    protected JwtUtils() {
        super();
        this.CST_SECRET = null;
        this.CST_EXPIRATION = -1;
        this.CST_AUTHORITIES = null;
    }

    public JwtUtils(@Value("${org.rd.fullstack.springbootnuxt.secret}") String secret,
                    @Value("${org.rd.fullstack.springbootnuxt.expiration}") int expiration,
                    @Value("${org.rd.fullstack.springbootnuxt.authorities}") String authorities) {
        super();
        this.CST_SECRET = secret;
        this.CST_EXPIRATION = expiration;
        this.CST_AUTHORITIES = authorities;
    }

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        String authorities            = authentication.getAuthorities()
                                                      .stream()
                                                      .map(GrantedAuthority::getAuthority)
                                                      .collect(Collectors.joining(","));
        return Jwts.builder()
                   .setId(UUID.randomUUID().toString())
                   .setSubject(userPrincipal.getUsername())
                   .claim(CST_AUTHORITIES, authorities)
                   .setIssuedAt(new Date())
                   //.setExpiration(new Date((new Date()).getTime() + CST_EXPIRATION))
                   // An API/KEY does not expire. Add an expiration for testing. Att: Update timeout.
                   .signWith(SignatureAlgorithm.HS256, CST_SECRET) // Should be an asymmetric key algorithm.
                   .compact();                                     // Demonstration only.
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(CST_SECRET).parseClaimsJws(token).getBody().getSubject();
    }

    public List<ERole> getAuthoritiesFromJwtToken(String token) {
        List<ERole> roles = new ArrayList<ERole>();
        String authorities = Jwts.parser().setSigningKey(CST_SECRET)
                                          .parseClaimsJws(token)
                                          .getBody()
                                          .get(CST_AUTHORITIES, String.class);
        if (authorities != null && ! authorities.isEmpty()) {
            StringTokenizer tokenizer = new StringTokenizer(authorities, ",");
            while (tokenizer.hasMoreElements()) {
                roles.add(ERole.valueOf(tokenizer.nextToken()));
            }
        }
        return roles;
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(CST_SECRET).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature: {}.", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token: {}.", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("JWT token expired: {}.", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("JWT token is not supported: {}.", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("The JWT token claims string is empty: {}.", ex.getMessage());
        }
        return false;
    }

    public String decodeJwtToken(String jwtToken) {
        Jws<Claims> jws;
        try {
            jws = Jwts.parser().setSigningKey(CST_SECRET).parseClaimsJws(jwtToken);
        }
        catch (JwtException ex) {
            logger.error("JWT token is invalid: {}.", ex.getMessage());
            return ex.getLocalizedMessage(); // Simplicity ... This is an example only.
        }
        return jws.getBody().toString();
    }
}