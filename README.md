# org-rd-fullstack-springboot-nuxt (2023, 2024, 2025)

## Fullstack application with Springboot and Nuxt

This project uses Spring Boot, Nuxt, Apache Maven, and Docker to build an OCI-compliant application container. It includes microservices designed for compatibility with AWS/EKS environments and features a web application built with Nuxt (Vue/Vuetify) following JamStack principles. Note: In this setup, Spring Boot serves as a static HTTP server (CDN).

![alt text](./doc/asserts/springboot-nuxt.png "Springboot-Nuxt")

* [Source : AppLogin.png](./doc/asserts/applogin.png)
* [Source : AppCRUD.png](./doc/asserts/appcrud.png)
* [Source : AppPDF.png](./doc/asserts/apppdf.png)
* [Source : Swagger.png](./doc/asserts/swagger.png)
* [Source : GraphQL.png](./doc/asserts/graphql.png)

## Important

Building a web application with SOA services (BFF only) included in a single artifact is neither recommended nor prohibited. The solution architect’s judgment is required. If this approach is used, limit SOA services to BFF (Backend For Frontend) type requirements only. In this project, authentication/authorization services and an in-memory database are embedded. These services should ideally be external resources. This project is intended for learning and demonstration purposes only.

## Prerequisites

The following software must be installed on your workstation to build and run this project:

* [NodeJS](https://nodejs.org/en)
* [Java SDK](https://www.oracle.com/java/technologies/downloads/)
* [Apache/Maven](https://maven.apache.org/download.cgi)
* [Optional - GIT or Zip download](https://git-scm.com/downloads)
* [Optional - VSCode/IDE](https://code.visualstudio.com/download)
* [Optional - VSCode/Plugin](https://marketplace.visualstudio.com/items?itemName=Vue.vscode-typescript-vue-plugin)
* [Optional - Docker to build image](https://www.docker.com/products/docker-desktop/)

## Springboot - Getting started

To get started quickly, run the following commands:

```bash
# Nuxt3 (Frontend)
cd src/frontend                                  # Navigate to the web application root.

npm i -D vuetify vite-plugin-vuetify             # Install Vuetify plugins for Nuxt.
npm i @mdi/font                                  # Install Material Design Icons font for Vuetify.

npm cache clean --force                          # Clear the npm cache.
npm install                                      # Install project dependencies.
npm run dev                                      # Start the app with hot reloading.
npm run preview                                  # Preview a production build locally.
npm run build && npm run start                   # Build and start the production version.
npm run generate                                 # Generate the static site.
npx nuxi@latest upgrade                          # Upgrade Nuxt to the latest version.
npx nuxi cleanup                                 # Remove temporary files and directories.
npm outdated                                     # List outdated packages.

npm set registry=https://registry.npmjs.org/     # Set npm registry (useful behind a proxy).
npm config set strict-ssl false --global         # Disable strict SSL checks (not recommended for production).

npm i nuxi                                       # Install the nuxi module (optional).
npx nuxi init frontend                           # Create a new Nuxt app in the "frontend" directory.

# Spring Boot (Backend)
mvn clean                                        # Remove compiled files and artifacts.
mvn install                                      # Build and package the application.
mvn spring-boot:run                              # Start the Spring Boot app.

mvn wrapper:wrapper                              # Regenerate Maven wrapper files.
mvn dependency:sources                           # Download source code for dependencies.
mvn dependency:resolve -Dclassifier=javadoc      # Download Javadoc for dependencies.

mvn spring-boot:build-image                      # Build an OCI image using Paketo Buildpack.
                                                 # Alternatively, use the Dockerfile for custom builds.

java -jar target/springboot-nuxt-unspecified.jar # Run the packaged JAR directly.

# Docker
docker build --no-cache .                        # Build an OCI image from the current directory.
docker build --no-cache -t org-rd-fullstack/springboot-nuxt:unspecified .
                                                 # Build and tag the Docker image.

docker run -it -p8080:8080 -p8081:8081 org-rd-fullstack/springboot-nuxt:unspecified
                                                 # Run the Docker image with port mappings.

docker system prune -a                           # Remove unused Docker data (use with caution).
docker image ls                                  # List local Docker images.
docker rmi -f <imageID>                          # Force remove an image by ID.

# Image Inspection
dive org-rd-fullstack/springboot-nuxt:unspecified
                                                 # Inspect image layers (see: https://github.com/wagoodman/dive).

# Spring Boot Layer Tools
java -Djarmode=layertools -jar target/springboot-nuxt-unspecified.jar list
                                                 # List JAR layers.
java -Djarmode=layertools -jar target/springboot-nuxt-unspecified.jar extract --destination target/tmp
                                                 # Extract JAR layers to a directory.
```

When the application is running, you can access the following endpoints:

* [Nuxt3 Web Application](http://localhost:8080/app)
* [Ping Endpoint (startup verification)](http://localhost:8080/jwt/ping)
* [Swagger UI (API testing)](http://localhost:8080/swagger-ui)
* [GraphiQL (GraphQL API testing)](http://localhost:8080/graphiql)
* [OpenAPI Specification](http://localhost:8080/v3/api-docs)
* [Actuator Endpoints](http://localhost:8081/actuator)
* [Info Probe](http://localhost:8081/actuator/info)
* [Health Probe](http://localhost:8081/actuator/health)
* [Liveness Probe](http://localhost:8081/actuator/health/liveness)
* [Readiness Probe](http://localhost:8081/actuator/health/readiness)
* [Prometheus Metrics](http://localhost:8081/actuator/prometheus)

## Nuxt3 - Getting started

To get started quickly, run the following commands:

```bash
cd src/frontend                                  # Navigate to the web application root.

npm i -D vuetify vite-plugin-vuetify             # Install Vuetify plugins for Nuxt.
npm i @mdi/font                                  # Install Material Design Icons font for Vuetify.

npm cache clean --force                          # Clear the npm cache.
npm install                                      # Install project dependencies.
npm run dev                                      # Start the app with hot reloading.
npm run preview                                  # Preview a production build locally.
npm run build && npm run start                   # Build and start the production version.
npm run generate                                 # Generate the static site.
npx nuxi@latest upgrade                          # Upgrade Nuxt to the latest version.
npx nuxi cleanup                                 # Remove temporary files and directories.
npm outdated                                     # List outdated packages.

npm set registry=https://registry.npmjs.org/     # Set npm registry (useful behind a proxy).
npm config set strict-ssl false --global         # Disable strict SSL checks (not recommended for production).

npm i nuxi                                       # Install the nuxi module (optional).
npx nuxi init frontend                           # Create a new Nuxt app in the "frontend" directory.
```

## Best Practices for Development

This section outlines recommended development practices for various scenarios:

* [Building Images (OCI/Docker)](./doc/ocidev.md)
* [Cloud Development](./doc/clouddev.md)
* [GraphQL HTTP Caching](./doc/graphqldev.md)
* [REST API and Error Handling](./doc/restdev.md)

## Other Information

### Example JWT Token

```code
eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIzNzJjMDhjMS02OGY5LTQ3YmQtODdhOS1iNjY0YTIxZTZhMWMiLCJzdWIiOiJyb290IiwicmQucm9sZXMiOiJST0xFX1NFTEVDVCxST0xFX0lOU0VSVCxST0xFX1VQREFURSxST0xFX0RFTEVURSIsImlhdCI6MTcxODM4NjQ1NH0.AIS4YFJCUIIv0IzTxdDp3MqQXDyuhQBFSJnJmF6b06c
```

**Notes:**

* This JWT token represents the `root` user (password: `root`). It is generated via the `/jwt/token` service. Refer to the Swagger UI for details.
* Security in this project is minimal and **must not** be used in production.
* This project is for learning and demonstration purposes only.

Enjoy experimenting!
