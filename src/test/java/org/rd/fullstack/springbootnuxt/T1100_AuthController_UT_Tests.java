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

package org.rd.fullstack.springbootnuxt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.rd.fullstack.springbootnuxt.config.Application;
import org.rd.fullstack.springbootnuxt.dto.MessageResponse;
import org.rd.fullstack.springbootnuxt.dto.User;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


/*
 * See POM.XML file
 * - Plugins section: maven-surefire-plugin
 * - Unit tests VS integrated tests.
 */
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
// @ActiveProfiles("test") If specific configuration file. Example: application-test.yml
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Controller tests for authentication and authorizations.")
public class T1100_AuthController_UT_Tests extends AbstractMVC {

    private final String uriJwt   = "/jwt/token";
    private final String username = "root";
    private final String password = "root";

    public T1100_AuthController_UT_Tests() {
        super();
        mvcInstance = null;
    }

    @BeforeAll
    static public void setUpBeforeAll() {
        logger.info("@BeforeAll - Runs once before all test methods of this class.");
    }

    @AfterAll
    static void tearDownAfterAll() {
         logger.info("@BeforeAll - Run once after all test methods of this class.");
    }

    @BeforeEach
    void setUpBeforeEach() {
        logger.info("@BeforeEach - Executes before each test method of this class.");
    }

    @AfterEach
    void tearDownAfterEach() {
        logger.info("@BeforeEach - Executes after each test method of this class.");
    }

    @Test
    public void postJwtToken() throws Exception {
        MockMvc mvcInstance  = getMvcInstance();
        assertNotNull(mvcInstance,"***** The getMvcInstance() method return NULL.");

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        String inputJson = mapToJson(user);
        assertNotNull(mvcInstance,"***** mapToJson(user) return NULL.");

        MvcResult mvcResult = mvcInstance.perform(MockMvcRequestBuilders.post(uriJwt)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(inputJson))
            .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);

        String content = mvcResult.getResponse().getContentAsString();
        MessageResponse mr =mapFromJson(content, MessageResponse.class);

        assertTrue(mr.getMessage() != null);
        logger.info("***** The message (JWT token): {}.", mr.getMessage());
    }

    @Test
    public void getJwtTokenInfo() throws Exception {

        MockMvc mvcInstance  = getMvcInstance();
        assertNotNull(mvcInstance,"***** The getMvcInstance() method return NULL.");

        // Encoding according to configuration file settings.
        // - secret: the.beautiful.secret.key.to.change
        // - expiration: 30000
        // - authorities: rd.roles
        String jetonJWT = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJmZTc1OGJhOS1jYjhmLTRlNmItOTgyNC1hYjRhZDlmMDYxZmQiLCJzdWIiOiJyb290IiwicmQucm9sZXMiOiJST0xFX1NFTEVDVCxST0xFX0lOU0VSVCxST0xFX1VQREFURSxST0xFX0RFTEVURSIsImlhdCI6MTY4NTQ1MTQ5NX0.d0aF0ukJDRuxRQEAzejbxiYLYDNx6GY6ImY6QB_Qqb4";

        MvcResult mvcResult = mvcInstance.perform(MockMvcRequestBuilders.get(uriJwt + "/" + jetonJWT)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);

        String content = mvcResult.getResponse().getContentAsString();
        MessageResponse mr = mapFromJson(content, MessageResponse.class);

        assertTrue(mr.getMessage() != null);
        logger.info("***** The content (JWT token): {}.", mr.getMessage());
    }
}