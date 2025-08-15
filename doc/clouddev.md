# Best practices for cloud development

[12factor.net](https://12factor.net/) provides an excellent foundation for evaluating the cloud readiness of applications. Throughout this documentation, you will see how these factors interconnect—adhering to one often facilitates compliance with others, creating a virtuous cycle that improves cloud-native development.

Whether you are building a new application from scratch or modernizing a portfolio of legacy systems, this guide offers practical advice to prepare for cloud-native development. Considering updated priorities, definitions, and new factors, we outline the key facets of cloud-native applications below:

## 1. One codebase, one application

The first of the original factors, codebase, states: “One codebase tracked in revision control, many deploys.” Managing the organization of code, artifacts, and related details is often overlooked, but applying discipline here can significantly reduce production lead times.

A cloud-native application should always have a single codebase tracked in a version control system. This codebase may be a single repository or a set of repositories with a shared root, but it represents one application. Multiple deployments—such as development, staging, and production—should all originate from this codebase, ensuring consistency and traceability across environments.

## 2. API first

When building cloud-native applications, adopting an API-first approach is essential. After code is checked into your repository, automated tests are executed, and release candidates are deployed to a lab environment within minutes. This streamlined workflow enables rapid feedback and continuous integration.

By designing and documenting APIs before implementation, teams can collaborate more effectively, reduce integration issues, and ensure that services interact seamlessly. API-first development also facilitates parallel workstreams, allowing front-end and back-end teams to work independently against well-defined contracts.

As your organization grows, multiple teams may begin developing services that interact with each other. Each team might follow its own release schedule, leading to a complex web of dependencies. Without disciplined API management, this can quickly result in integration failures and miscommunication between teams.

Adopting an API-first approach helps prevent these issues by treating APIs as first-class artifacts in the development process. By formally defining and documenting public contracts up front, teams can work independently and integrate seamlessly, reducing the risk of breaking changes and facilitating parallel development.

Even if your application is not part of a larger ecosystem, starting development at the API level brings significant benefits. It ensures clarity, consistency, and maintainability. In this project, simple API contracts are established through the use of controllers:

The following controllers define the API contracts for this project:

* [AuthController.java](../src/main/java/org/rd/fullstack/springbootnuxt/controller/AuthController.java)
* [BookController.java](../src/main/java/org/rd/fullstack/springbootnuxt/controller/BookController.java)
* [HealthController.java](../src/main/java/org/rd/fullstack/springbootnuxt/controller/HealthController.java)
* [ReportController.java](../src/main/java/org/rd/fullstack/springbootnuxt/controller/ReportController.java)

Each controller is responsible for a specific domain area, ensuring clear separation of concerns and maintainable API endpoints.

## 3. Dependency management

Modern programming languages provide robust tools for managing application dependencies. In the Java ecosystem, Maven and Gradle are widely used, while NuGet serves .NET developers and npm is common for web development.

Cloud-native applications should never depend on the implicit presence of system-wide packages. Effective dependency management ensures repeatable deployments and eliminates assumptions about the runtime environment. All dependencies should be explicitly declared and managed through automated processes.

Ideally, the application's container is bundled or bootstrapped within the release artifact, ensuring consistency across environments.

This project uses Maven for managing dependencies in the Spring Boot backend and NPM for the frontend application:

* [Source: pom.xml](../pom.xml)
* [Source: package.json](../src/frontend/package.json)

## 4. Design, build, release, and run

A single codebase is taken through a standardized build process to produce a compiled artifact. This artifact is then combined with external configuration to create an immutable release. In cloud-native environments, JAR files are preferred over WAR or EAR files, as they do not rely on an externally provided server or container.

The immutable release is deployed to various cloud environments (development, QA, production, etc.) and executed. Each deployment stage—design, build, release, and run—is isolated and occurs independently, ensuring consistency and repeatability across environments.

## 5. Configuration, credentials, and code

It is critical to keep configuration and credentials separate from your application code. Failing to do so can lead to significant issues, especially as you approach production deployment. Configuration refers to any value that may change between environments (e.g., developer workstation, QA, production), such as:

* URLs and connection details for backing services (e.g., web services, SMTP servers)
* Database connection information
* Credentials for third-party services (e.g., AWS, Google Maps, Twitter, Facebook APIs)
* Values typically stored in properties files, XML, or YAML configuration

Configuration does not include internal values that remain constant across all deployments and are intentionally part of the immutable build artifact.

Credentials are highly sensitive and should never be included in the codebase. Moving credentials from source code to resource files (such as XML or properties files) is not sufficient, as these files are still bundled with the application. Credentials must be managed securely outside of the codebase, using environment variables, secret management tools, or cloud provider services.

Example configuration in `application.yml`:

```yaml
org:
  rd:
    fullstack:
      springbootnuxt:
        secret: the.beautiful.secret.key.to.change
        expiration: 30000
        authorities: rd.roles
```

* [Source : application.yml](../src/main/resources/application.yml)
* [Documentation : Springboot/External configuration](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.external-config)

## 6. Logs

Logs should be treated as event streams—a sequence of time-ordered events emitted by the application. In cloud-native development, applications should not manage the routing or storage of their log output. Instead, logs are written to stdout and stderr, allowing the cloud platform or external tools to handle aggregation, processing, and storage.

Traditional approaches often involve configuring log file locations, rotation policies, and other file system details. However, in cloud environments, the underlying file system is ephemeral and cannot be relied upon for persistent storage. By writing logs to stdout/stderr, applications remain portable and platform-agnostic.

Log aggregation and analysis are handled by external systems such as the ELK stack (Elasticsearch, Logstash, Kibana), Splunk, or cloud-native logging solutions. These tools collect, process, and visualize log data for monitoring and troubleshooting. For structured logging, it is recommended to use the ecs format.

```yaml
logging: 
  file:
    name: log/springboot-nuxt-log.json
  structured: 
    format:
      console: ecs
      file: ecs
    ecs:
      service:
        name: org.rd.fullstack.springboot-nuxt
        version: unspecified
        environment: DEV
        node-name: DEV
```

* [Source : application.yml](../src/main/resources/application.yml)

## 7. Disposability

In cloud environments, both infrastructure and application processes are ephemeral. Cloud-native applications must be designed so their processes are disposable—able to start quickly and shut down gracefully. This enables rapid scaling, deployment, release, and recovery.

**Graceful shutdown**
Spring Boot supports graceful shutdown, allowing the application to complete ongoing requests before terminating. This ensures reliability and minimizes disruptions during scaling or redeployment.

```yaml
server:
  shutdown: graceful
```

For more details, refer to the [Spring Boot documentation on graceful shutdown](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#application-properties.server.server.shutdown).

* [Source : application.yml](../src/main/resources/application.yml)

## 8. Backing services

Treat all backing services as external resources that are attached to your application. A backing service is any service your application consumes over the network as part of its normal operation—such as databases, messaging systems, caches, file storage, authentication providers, or third-party APIs.

In cloud-native development, backing services should be loosely coupled and easily replaceable. Your application must not assume that any backing service is locally available or persistent. Instead, it should connect to these services using configuration, such as environment variables or externalized settings.

For example, instead of reading or writing files directly to the local disk, use cloud storage solutions (e.g., AWS S3, Azure Blob Storage) as backing services. This approach ensures portability, scalability, and resilience, as the cloud platform manages the lifecycle and availability of these resources.

By treating backing services as bound resources, you enable your application to scale, recover, and evolve independently of the underlying infrastructure.

## 9. Environment parity

Environment parity ensures that your application behaves consistently across all stages—development, QA, staging, and production. Differences in scaling, reliability, database drivers, security rules, firewalls, and configuration can lead to unpredictable behavior and deployment anxiety.

To achieve environment parity, standardize your environments as much as possible. Use infrastructure-as-code tools, containerization, and automated configuration management to minimize discrepancies. Ensure that dependencies, configuration, and deployment processes are consistent across all environments.

By maintaining environment parity, teams gain confidence that code tested in one environment will work reliably in others, reducing deployment risks and streamlining the release process.

## 10. Administrative processes

Administrative processes are tasks that run separately from the main application, such as database migrations, batch jobs, or one-off scripts. While these processes are sometimes necessary, cloud-native best practices recommend minimizing their use and considering alternatives that better fit scalable, resilient architectures.

Instead of embedding administrative logic within the application or relying on manual intervention, use automated tools and workflows that can be triggered independently. For example, database migrations should be managed by dedicated migration tools and executed as part of your deployment pipeline. Scheduled jobs or imports can be handled by cloud-native services (such as managed schedulers or serverless functions) to ensure reliability and scalability.

Whenever possible, design administrative tasks to be stateless, repeatable, and easily automated. This approach reduces operational complexity and aligns with cloud-native principles. Typical examples of administrative processes include:

* Database migrations
* Interactive programming consoles (REPLs)
* Timed scripts (e.g., nightly batch jobs, hourly imports)
* One-off jobs that execute custom code

Evaluate each administrative process to determine if it can be refactored into a more cloud-native solution, such as automated pipelines or managed services.

## 11. Port binding

In traditional enterprise environments, multiple applications are often hosted on the same server and separated by port numbers or URL paths, with DNS providing user-friendly access. In cloud-native development, this manual management is replaced by platform-as-a-service (PaaS) solutions, where the cloud provider handles port assignment, routing, scaling, and high availability.

Cloud platforms manage network routing, mapping external ports to internal container ports, and ensuring seamless connectivity. Applications should be configured to listen on a port specified by the environment, allowing the platform to handle traffic routing and service discovery. This project uses configuration to define port bindings:

```yaml
server:
  shutdown: graceful
  port: 8080

management:
  server:
    port: 8081
```

* [Source: application.yml](../src/main/resources/application.yml)

By externalizing port configuration, the application remains portable and adaptable to different cloud environments.

## 12. Stateless processes

Cloud-native applications should execute as stateless processes. This means the application does not rely on any in-memory state persisting between requests. Any transient data created during request handling must be discarded once the response is sent. All persistent or long-lived state should be managed externally, typically through backing services such as databases or caches. This approach ensures scalability, reliability, and ease of deployment.

## 13. Concurrency

Cloud-native applications are designed to scale horizontally by running multiple instances of stateless processes. Instead of increasing the size of a single process, you deploy additional instances and distribute the workload among them. Most cloud platforms support automatic scaling based on demand, allowing applications to handle increased load efficiently. Building stateless, disposable processes enables seamless horizontal scaling and high availability.

## 14. Telemetry

Telemetry is essential for monitoring and maintaining cloud-native applications. It involves collecting and transmitting data about application performance, domain-specific events, and system health. Key telemetry categories include:

* **Application Performance Monitoring (APM):** Tracks metrics such as response times, throughput, and error rates to assess application health and performance.
* **Domain-specific telemetry:** Captures business-relevant events and data for analytics and reporting.
* **Health and system logs:** Includes events like application startup, shutdown, scaling actions, request tracing, and health checks.

Spring Boot provides built-in support for telemetry through its actuator endpoints, enabling easy integration with monitoring and observability tools.

```yaml
management:
  server:
    port: 8081
  endpoints:
    web:
      exposure:
        include: "info,health,prometheus"
spring:
  main:
    cloud-platform: kubernetes

```

* [Source : application.yml](../src/main/resources/application.yml)

This configuration enables the application to expose the following telemetry endpoints:

* [Actuator root endpoint](http://domaine:8081/actuator) — lists all available probes
* [Info endpoint](http://domaine:8081/actuator/info) — provides build and environment information
* [Health endpoint](http://domaine:8081/actuator/health) — reports overall health status
* [Liveness probe](http://domaine:8081/actuator/health/liveness) — indicates if the application is running
* [Readiness probe](http://domaine:8081/actuator/health/readiness) — indicates if the application is ready to serve requests
* [Prometheus metrics](http://domaine:8081/actuator/prometheus) — exposes application metrics for monitoring

These endpoints support monitoring, alerting, and automated orchestration in cloud environments.

## 15. Authentication and authorization

Security is essential for any application, especially in cloud environments. It should be considered from the start, not as an afterthought. While functional requirements often take priority, neglecting security can expose applications to significant risks.

A cloud-native application must be secure by design. Application code and data traverse multiple data centers, containers, and clients—some trusted, others potentially malicious. Implementing authentication and authorization ensures that only legitimate users can access resources and provides an audit trail for accountability.

Role-based access control (RBAC) is recommended for cloud-native applications. Every request should be authenticated, and user roles should determine access to resources. This approach protects sensitive data, supports compliance requirements, and helps maintain application integrity.

### Dependencies

To enable security in your Spring Boot application, add the following dependency to your `pom.xml`:

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

* [Source: pom.xml](../pom.xml)

### Configuration

You must configure security settings appropriate for your application. Below is an example of a Spring Security configuration using Java:

```java
http.cors(withDefaults());

http.csrf(csrf -> csrf.disable());

http.authorizeHttpRequests(auth -> 
  auth.requestMatchers(AUTH_WHITELIST).permitAll()
    .anyRequest().authenticated());

http.exceptionHandling(except -> 
  except.authenticationEntryPoint(exceptionHandlingAuthEntryPoint()));

http.sessionManagement(sm -> 
  sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

http.authenticationProvider(authenticationProvider());

http.addFilterBefore(authentificationTokenFilter(jwtUtils(), userDetailsService()),
  UsernamePasswordAuthenticationFilter.class);

return http.build();
```

This configuration enables CORS, disables CSRF protection, sets up request authorization rules, configures exception handling, enforces stateless session management, specifies an authentication provider, and adds a JWT authentication filter.

* [Source: SecurityConfig.java](../src/main/java/org/rd/fullstack/springbootnuxt/config/SecurityConfig.java)
* [Documentation: Spring Security](https://docs.spring.io/spring-security/reference/index.html)
