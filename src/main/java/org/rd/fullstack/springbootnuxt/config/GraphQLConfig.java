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

import org.rd.fullstack.springbootnuxt.util.ExceptionHandlerGraphQL;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphQLConfig {

    public GraphQLConfig() {
        super();
    }

    @Bean
    ExceptionHandlerGraphQL graphQLExceptionResolver() {
        return new ExceptionHandlerGraphQL();
    }
   
    // No webflux, only a standard mvc controller.
    //@Bean
    // @Order(-1)
    //public RouterFunction<ServerResponse> graphQlGetRouterFunction(GraphQlHttpHandler GetRequestGraphQlHttpHandler, GraphQlProperties properties) {
    //    return RouterFunctions
    //          .route()
    //          .GET(properties.getHttp().getPath(), 
    //               RequestPredicates.param("query", Objects::nonNull), 
    //               gethttpHandler::handleRequest)
    //          .build();
    //}

    //@Bean
    //public RouterFunction<ServerResponse> graphQlRouter(HttpGetHandlerGraphQl handler) {
    //   return RouterFunctions.route()
    //          .GET("/graphql", handler::handle)
    //          .build();
    //}
}