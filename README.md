# org-rd-fullstack-springboot-nuxt (2023, 2024)

## Fullstack application with Springboot and Nuxt.

This project relies on the use of Springboot, Nuxt, Apache/Maven and docker. It allows you to build an application container (OCI) that embeds  micro-services compatible with the AWS/EKS architecture. In addition, it includes a WEB application based on the Nuxt Framework (Vue/Vuetify) build with the principles of JamStack architecture (SSG - Static Site Generation). Note : Springboot is used as a simple static HTTP server (CDN).

![alt text](./doc/asserts/springboot-nuxt.png "Springboot-Nuxt")

* [Source : AppLogin.png](./doc/asserts/applogin.png)
* [Source : AppCRUD.png](./doc/asserts/appcrud.png)
* [Source : AppPDF.png](./doc/asserts/apppdf.png)
* [Source : Swagger.png](./doc/asserts/swagger.png)
* [Source : GraphQL.png](./doc/asserts/graphql.png)

## Important

Building a WEB application with SOA services (BBF only) included in a single artefact is not a crime (nor a recommendation). The solution architect's judgment is required. If this approach is used, then limit SOA services to BFF (Backend For Frontend) type requirements only. Also, for this project, we embebed a authentification/autorisation services and a an memory database services. Of course, those services should be an external resources. This project is for learning and demonstration only.

## Prerequisites

The following software must be installed on your workstation to build this project:

* [NodeJS](https://nodejs.org/en)
* [Java SDK](https://www.oracle.com/java/technologies/downloads/)
* [Apache/Maven](https://maven.apache.org/download.cgi)
* [Optional - GIT or Zip download](https://git-scm.com/downloads)
* [Optional - VSCode/IDE](https://code.visualstudio.com/download)
* [Optional - VSCode/Plugin](https://marketplace.visualstudio.com/items?itemName=Vue.vscode-typescript-vue-plugin)
* [Optional - Docker to build image](https://www.docker.com/products/docker-desktop/)

## Springboot - Getting started

To get started quickly, please consider the following commands:

```bash
mvn clean                                        # Clear compilation and artifacts.
mvn install                                      # Compile, build and prepare the deployment artifact.
mvn spring-boot:run                              # Start the app.

mvn wrapper:wrapper                              # Regenerate context for Apache/Maven.
mvn dependency:sources                           # Obtain sources and libraries.
mvn dependency:resolve -Dclassifier=javadoc      # Obtain source documentation (javadoc).

mvn spring-boot:build-image                      # Build an OCI image with Paketo Buildpack.
                                                 # See also the Docker alternative with the Dockerfile.

java -jar target/springboot-nuxt-unspecified.jar # Directly execute the jar.

docker build --no-cache .                        # Building an OCI image with unnamed docker.
                                                 # Uses the current directory (.) of the project.

docker build --no-cache -t org-rd-fullstack/springboot-nuxt:unspecified . 
                                                 # Building an OCI image with docker.
                                                 # Uses the current directory (.) of the project.
                                                 # -t to specify a repository/tag on this image.

docker run -it -p8080:8080 -p8081:8081  org-rd-fullstack/springboot-nuxt:unspecified
                                                 # Run the docker image of this application.

docker system prune -a                           # If it crashes during construction without a specific cause.
                                                 # You can try this. Will erase docker registry and all images.
                                                 # USE WITH CARE !!!

docker image ls                                  # List of local docker registry images. 
docker rmi -f imageID                            # Force the destruction of an image using its ID.
dive org-rd-fullstack/springboot-nuxt:unspecified
                                                 # Explore/browse your image with the dive tool.
                                                 # Tool link: https://github.com/wagoodman/dive

java -Djarmode=layertools -jar target/springboot-nuxt-unspecified.jar list
                                                 # See the build of the layers.
java -Djarmode=layertools -jar target/springboot-nuxt-unspecified.jar extract --destination target/tmp
                                                 # Extract the build from the layers of the jar.
```

When the application is started, please consider the following links:

* [Web Application (Nuxt3)](http://localhost:8080/app)
* [Ping to verify startup](http://localhost:8080/jwt/ping)
* [Swagger UI to test the API](http://localhost:8080/swagger-ui)
* [GraphQL UI to test the API](http://localhost:8080/graphiql)
* [OpenAPI Definition](http://localhost:8080/v3/api-docs)
* [Available probes](http://localhost:8081/actuator)
* [Informational probe](http://localhost:8081/actuator/info)
* [Health status probe](http://localhost:8081/actuator/health)
* [Liveness probe](http://localhost:8081/actuator/health/liveness)
* [Readiness probe](http://localhost:8081/actuator/health/readiness)
* [Statistics probe (prometheus)](http://localhost:8081/actuator/prometheus)

## Nuxt3 - Getting started

To get started quickly, please consider the following commands:

```bash
cd src/frontend                              # Go to the root of the WEB application.

npm i -D vuetify vite-plugin-vuetify         # Vuetify plugins for nuxt.
npm i @mdi/font                              # Font for Vuetify.

npm cache clean --force                      # Force empty cache.
npm install                                  # Installing dependencies.
npm run dev                                  # Start the application with hot loading.
npm run preview                              # View a quick production build locally.
npm run build & npm run start                # Build a production and start-up version.
npm run generate                             # Generation of the static site.
npx nuxi@latest upgrade                      # Update Nuxt version.
npx nuxi cleanup                             # Allows you to delete temporary files and directories.
npm outdated                                 # View packages that are out of date.

npm set registry=https://registry.npmjs.org/ # Configuration (warn: proxy).
npm config set strict-ssl false --global     # Don't be picky with SSL/TLS certificates.

npm i nuxi                                   # Installing the nuxi module -- OPTIONAL.
npx nuxi init frontend                       # Creation of an empty application under the "frontend" directory.
```

## Best practices for development

This section describes development best practices for different use cases.

* [REST API and error handling](./doc/restdev.md)
* [GraphQL HTTP caching](./doc/graphqldev.md)
* [Cloud development](./doc/clouddev.md)
* [Building image (OCI/Docker)](./doc/ocidev.md)

## Others

A valid JWT token:

eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIzNzJjMDhjMS02OGY5LTQ3YmQtODdhOS1iNjY0YTIxZTZhMWMiLCJzdWIiOiJyb290IiwicmQucm9sZXMiOiJST0xFX1NFTEVDVCxST0xFX0lOU0VSVCxST0xFX1VQREFURSxST0xFX0RFTEVURSIsImlhdCI6MTcxODM4NjQ1NH0.AIS4YFJCUIIv0IzTxdDp3MqQXDyuhQBFSJnJmF6b06c


Note:

* The JWT token represents the root user (password: root). It is generated with the service: /jwt/token. See the Swagger interface for more information.
* The security configured on this project is minimal and MUST NOT be used in a production environment.
* Have fun !
