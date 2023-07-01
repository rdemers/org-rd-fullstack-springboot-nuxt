# ----------------------------------------------------------------------------------------
# org.rd.fullstack.springboot-nuxt
# ----------------------------------------------------------------------------------------
# 
# Dockerfile for containerizing a Springboot application that supports
# the composition per layer (layer). It also includes a WEB application in
# static mode (JamStack). This app is made with Nuxt.
#
# This approach promotes the reuse of different layers and boundaries
# disk space usage of images. Moreover, it allows to have in
# a single deliverable SOA services (BFF only) of the WEB application.
#
# IMPORTANT:
# Including a WEB application with SOA services (BBF only) is not
# a crime (nor a recommendation). The solution architect's judgment is
# required. If the approach is used, then limit the SOA services to the needs
# of type BFF only.
#
# R. Demers; 2023.
# ----------------------------------------------------------------------------------------
# Specifications and constraints:
#
# 1. You must provide a valid Maven configuration to build your
#    image locally (on your workstation - Cause: the proxy).
#    - See line: RUN mvn --settings maven-with-proxy.xml ...
#
# 2. Need to remove "--settings ./maven-with-proxy.xml" for build which
#    is done with the Github pipeline and use:
#    - RUN mvn -U -B -e -f pom.xml clean prepare-package package
#
# 3. You can also suppress test execution using the
#    next parameter: -Dmaven.test.skip=true.
#
# 4. Building the WEB application is done with Node.js. This module
#    is dynamically added to the BUILDER image. The following instructions
#    are used for the base image version (ALPINE):
#    - RUN sed -i 's/https/http/g' /etc/apk/repositories
#    - RUN apk --no-cache add nodejs npm
#    - RUN npm set registry=https://registry.npmjs.org/
#    - RUN npm config set strict-ssl false --global
#
#    It is important to note that these instructions may vary depending on
#    base image used. For reference only.
#
# 5. The command to use to build the image:
#    - docker build --no-cache -t sorg-rd-fullstack/springboot-nuxt:unspecified .
#
# 6. The command to run the image:
#    - docker run -it -p8080:8080 -p8081:8081 org-rd-fullstack/springboot-nuxt:unspecified
#
# 7. To clear cache and local registry to do a full test
#    of the construction of your image:
#    - docker system prune -a
# ----------------------------------------------------------------------------------------
# Building the builder.
# 
   FROM maven:3.8.7-eclipse-temurin-17-alpine as builder 
   # FROM maven:3.8.3-amazoncorretto-17 as builder
WORKDIR /application
#
# We copy the sources required for the construction.
   COPY src ./src
   COPY pom.xml .
   COPY layers.xml .
   COPY maven-with-proxy.xml .
   COPY web-app-jamstack.xml .
#
# We delete the modules of the Web/App application.
# If modules are present... Possibly Windows, we are under Linux.
    RUN rm -rf src/frontend/node_modules
#
# Build requires Node.js and configuration tweaks.

# For ... maven:3.8.7-eclipse-temurin-17-alpine.
    RUN sed -i 's/https/http/g' /etc/apk/repositories
    RUN apk --no-cache add nodejs npm

# For ... maven:3.8.3-amazoncorretto-17. WARNING ... BUG TO FIX.
    # RUN sed -i 'sslverify=false' /etc/yum.conf  
    # RUN yum --disablerepo=AmazonCorretto update
    # RUN yum --disablerepo=AmazonCorretto install nodejs

# Setup NPM
    RUN npm set registry=https://registry.npmjs.org/
    RUN npm config set strict-ssl false --global
#
# Always helpful when there are problems.
    RUN mvn --version
    RUN node --version
    RUN npm --version
#
#   Build on Gitlab/SAAS... No proxy configuration required.
#   RUN mvn -U -B -e -f pom.xml clean prepare-package package
#   Build on local post... Att. proxy. See maven-with-proxy.xml.
#   RUN mvn --settings maven-with-proxy.xml -U -B -e -f pom.xml clean prepare-package package
    RUN mvn -U -B -e -f pom.xml clean prepare-package package
#
# Preparation of the layers (layers) of the SpringBoot application.
    ARG JAR_FILE=target/*.jar
    RUN cp ${JAR_FILE} application.jar
    RUN java -Djarmode=layertools -jar application.jar extract
#
# ----------------------------------------------------------------------------------------
# Construction of our image using the result of our builder.
#
# According to your preferences. If AWS, then we recommend aws/corretto.
#
#  FROM maven:3.8.7-eclipse-temurin-17-alpine
   FROM amazoncorretto:17
#
# Identification.
    ARG LABEL_TITLE="Please, provide a title."
    ARG LABEL_DESCRIPTION="Please, provide a description."
    ARG LABEL_CREATED="2023-99-99"
#
# Version : MAJOR.MINOR.REVISION-BUILD.
    ARG LABEL_VERSION_MAJOR="1"
    ARG LABEL_VERSION_MINOR="0"
    ARG LABEL_VERSION_REVISION="0"
    ARG LABEL_VERSION_BUILD="#1"
  LABEL app.image.title=${LABEL_TITLE}
  LABEL app.image.description=${LABEL_DESCRIPTION}
  LABEL app.image.created=${LABEL_CREATED}
  LABEL app.image.version.major=${LABEL_VERSION_MAJOR}
  LABEL app.image.version.minor=${LABEL_VERSION_MINOR}
  LABEL app.image.version.revison=${LABEL_VERSION_REVISION}
  LABEL app.image.version.build=${LABEL_VERSION_BUILD}
WORKDIR /application
   COPY --from=builder application/framework-dependencies/ ./
   COPY --from=builder application/spring-boot-loader/ ./
   COPY --from=builder application/corpo-dependencies/ ./
   COPY --from=builder application/snapshot-dependencies/ ./
   COPY --from=builder application/application/ ./
#
# ----------------------------------------------------------------------------------------
# Entry point.
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
#
