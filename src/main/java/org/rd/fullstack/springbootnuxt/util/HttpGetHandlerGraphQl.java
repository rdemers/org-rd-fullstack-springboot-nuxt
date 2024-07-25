/*
 * Copyright 2024; Réal Demers.
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

//::TODO::
package org.rd.fullstack.springbootnuxt.util;

public class HttpGetHandlerGraphQl /* extends AbstractGraphQlHttpHandler  */{

        /*
    @SuppressWarnings("unchecked")
    @Override
    protected Mono<ServerResponse> prepareResponse(ServerRequest request, WebGraphQlResponse response) {
        //throw new UnsupportedOperationException("Unimplemented method 'prepareResponse'");
        return (Mono<ServerResponse>) new ServerResponse();
    }


       //private static final Log logger = LogFactory.getLog(GetRequestGraphQlHttpHandler.class);

        private static final List<MediaType> SUPPORTED_MEDIA_TYPES = Arrays.asList(MediaType.APPLICATION_GRAPHQL_RESPONSE, MediaType.APPLICATION_JSON);

        // private final IdGenerator idGenerator = new AlternativeJdkIdGenerator();

        private final WebGraphQlHandler graphQlHandler;

        GetRequestGraphQlHttpHandler(WebGraphQlHandler graphQlHandler) {
            this.graphQlHandler = graphQlHandler;
        }

        public ServerResponse handleRequest(ServerRequest serverRequest) {
            String query = serverRequest.param("query").orElseThrow(() -> new RuntimeException("'query' parameter not set"));

            WebGraphQlRequest graphQlRequest = new WebGraphQlRequest(
                serverRequest.uri(), serverRequest.headers().asHttpHeaders(), Map.of("query", query));
                //this.idGenerator.generateId().toString(), LocaleContextHolder.getLocale());

            //if (logger.isDebugEnabled()) {
            //    logger.debug("Executing: " + graphQlRequest);
            //}

            Mono<ServerResponse> responseMono = this.graphQlHandler.handleRequest(graphQlRequest)
                .map(response -> {
                    //if (logger.isDebugEnabled()) {
                    //    logger.debug("Execution complete");
                    //}
                    ServerResponse.BodyBuilder builder = ServerResponse.ok();
                    builder.headers(headers -> headers.putAll(response.getResponseHeaders()));
                    builder.contentType(selectResponseMediaType(serverRequest));
                    return builder.body(response.toMap());
                });

            return ServerResponse.async(responseMono);
        }

        private static MediaType selectResponseMediaType(ServerRequest serverRequest) {
            for (MediaType accepted : serverRequest.headers().accept()) {
                if (SUPPORTED_MEDIA_TYPES.contains(accepted)) {
                    return accepted;
            }
        }
        return MediaType.APPLICATION_JSON;
    }
 */
    }                                                                                                                                                                                                                                                        