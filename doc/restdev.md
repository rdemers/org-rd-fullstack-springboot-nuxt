# Best practices for cloud development

[12factor.net](https://12factor.net/) provided an excellent starting point, a yardstick to measure applications along an axis of cloud suitability. As you will see throughout this documentation, these factors often feed each other. Properly following one factor makes it easier to follow another, and so on, throughout a virtuous cycle. Once people get caught up in this cycle, they often wonder how they ever built applications any other way.

Whether you are developing a brand new application without the burden of a single line of legacy code or you are analyzing an enterprise portfolio with hundreds of legacy applications, this book will give you the guidance you need to get ready for developing cloudnative applications. Taking into account the changes in priority order, definition, and additions, we describe the following facets of cloud-native
applications:

## 1. One codebase, one application

The first of the original factors, codebase, originally stated: “One codebase tracked in revision control, many deploys.” When managing myriad aspects of a development team, the organization of code, artifacts, and other apparent minutia is often considered a minor detail or outright neglected. However, proper application of discipline and organization can mean the difference between a one-month production lead time and a one-day lead time.

Cloud-native applications must always consist of a single codebase that is tracked in a version control system. A codebase is a source code repository or a set of repositories that share a common root.

## 2. API first

You are building cloud-native applications, and after code gets checked in to your repository, tests are automatically run, and you have release candidates running in a lab environment within minutes. The world is a beautiful place, and your test environment is populated by rainbows and unicorns.

Now another team in your organization starts building services with which your code interacts. Then, another team sees how much fun you’re all having, and they get on board and bring their services. Soon you have multiple teams all building services with horizontal that are all on a different release cadence. 

What can happen if no discipline is applied to this is a nightmare of
integration failures. To avoid these integration failures, and to formally recognize your API as a first-class artifact of the development process, API first gives teams the ability to work against each other’s public contracts without interfering with internal development processes.

Even if you’re not planning on building a service as part of a larger ecosystem, the discipline of starting all of your development at the API level still pays enough dividends to make it worth your time. This project establishes simple API contracts through the use of controllers:

* [Source : AuthController.java](../src/main/java/org/rd/fullstack/springbootnuxt/controller/AuthController.java)
* [Source : BookController.java](../src/main/java/org/rd/fullstack/springbootnuxt/controller/BookController.java)
* [Source : HealthController.java](../src/main/java/org/rd/fullstack/springbootnuxt/controller/HealthController.java)
* [Source : ReportController.java](../src/main/java/org/rd/fullstack/springbootnuxt/controller/ReportController.java)

## 3. Dependency management

Most contemporary programming languages have some facility for managing application dependencies. Maven and Gradle are two of the most popular tools in the Java world, while NuGet is popular for .NET developers and npm  is available for Web developers.

A cloud-native application never relies on implicit existence of system-wide packages. Properly managing your application’s dependencies is all about the
concept of repeatable deployments. Nothing about the runtime into which an application is deployed should be assumed that isn’t automated. In an ideal world, the application’s container is bundled (or bootstrapped, as some frameworks called it) inside the app’s release artifact.

Applying discipline to dependency management will bring your applications one step closer to being able to thrive in cloud environments. This project uses Maven for dependency management for Springboot and NPM for the web application:

* [Source : pom.xml](../pom.xml)
* [Source : package.json](../src/frontend/package.json)

## 4. Design, build, release, and run

A single codebase is taken through the build process to produce a compiled artifact. This artifact is then merged with configuration information that is external to the application to produce an immutable release. For a number of reasons, WAR (and EAR) files are looked upon as less cloud native than JAR files, as they imply reliance upon an externally provided server or container. 

The immutable release is then delivered to a cloud environment (development, QA, production, etc.) and run. The key takeaway from this chapter is that each of the following deployment stages is isolated and occurs separately.

## 5. Configuration, credentials, and code

That may sound a bit harsh, but failing to follow this rule will likely cause you untold frustration that will only escalate the closer you get to production with your application. In order to be able to keep configuration separate from code and credentials, we need a very clear definition of configuration. Configuration refers to any value that can vary across deployments (e.g., developer workstation, QA, and production). This could include:

* URLs and other information about backing services, such as web services, and SMTP servers;
* Information necessary to locate and connect to databases;
* Credentials to third-party services such as Amazon AWS or APIs like Google Maps, Twitter, and Facebook;
* Information that might normally be bundled in properties files or configuration XML, or YML.

Configuration does not include internal information that is part of the application itself. Again, if the value remains the same across all
deployments (it is intentionally part of your immutable build artifact), then it isn’t configuration.

Credentials are extremely sensitive information and have absolutely no business in a codebase. Oftentimes, developers will extract credentials from the compiled source code and put them in properties files or XML configuration, but this hasn’t actually solved the problem. Bundled resources, including XML and properties files, are still part of the codebase. This means credentials bundled in resource files that ship with your application are still violating this rule.

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

Logs should be treated as event streams, that is, logs are a sequence of events emitted from an application in time-ordered sequence. The key point about dealing with logs in a cloud-native fashion is, as the original 12 factors indicate, a truly cloud-native application never concerns itself with routing or storage of its output stream. Sometimes this concept takes a little bit of getting used to. Application developers, especially those working in large enterprises, are often accustomed to rigidly controlling the shape and destination of their logs. Configuration files or config-related code set up the location on disk where the log files go, log rotation and rollover policies to deal with log file size and countless other minutiae.

Cloud applications can make no assumptions about the file system on which they run, other than the fact that it is ephemeral. A cloud-native application writes all of its log entries to stdout and stderr. This might scare a lot of people, fearing the loss of control that this implies.

You should consider the aggregation, processing, and storage of logs as a nonfunctional requirement that is satisfied not by your application, but by your cloud provider or some other tool suite running in cooperation with your platform. You can use tools like the ELK stack (ElasticSearch, Logstash, and Kibana), Splunk, Sumologic, or any number of other tools to capture and analyze your log emissions.

Your application should log to STDOUT/STDERR using the LOGSTASH format. The project includes a dependency to do this activity:

```xml
<dependency> <!-- Allow encoding of logging in JSON/LOGSTASH format. -->
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>${logstash.logback.encoder.version}</version>
</dependency>
```

* [Source : pom.xml](../pom.xml)

Subsequently, you must configure logging to use this dependency. This configuration is done as follows:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true" scanPeriod="120 seconds">
    ...
    <appender name="console" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <includeContext>false</includeContext>
            <customFields>{"host_name":"${host_name}","ctx_name":"${ctx_name}"}</customFields>
        </encoder>
    </appender>
    ...
    <root level="info">
        <appender-ref ref="console"/>
        ...
    </root>

    <logger name="org.rd.fullstack.springbootnuxt" level="trace" additivity="false">
        <appender-ref ref="console"/>
        ...
    </logger>
</configuration>
```

* [Source : logback-spring.xml](../src/main/resources/logback-spring.xml)

## 7. Disposability

On a cloud instance, an application’s life is as ephemeral as the infrastructure that supports it. A cloud-native application’s processes are disposable, which means they can be started or stopped rapidly. An application cannot scale, deploy, release, or recover rapidly if it cannot start rapidly and shut down gracefully. We need to build applications that not only are aware of this, but also embrace it to take full advantage of the cloud platform.

**Soft stop (graceful)**
The Springboot Framework allows configuring a graceful shutdown of the application. The system requirements are as follows:

```yaml
#
server:
  shutdown: graceful
#
```

* [Source : application.yml](../src/main/resources/application.yml)

## 8. Backing services

You should treat backing services as bound resources. This sounds like good advice, but in order to follow this advice, we need to know what backing services and bound resources are. A backing service is any service on which your application relies for its functionality. This is a fairly broad definition, and its wide scope
is intentional. Some of the most common types of backing services include data stores, messaging systems, caching systems, and any number of other types of service, including services that perform line-of-business functionality or security.

When building applications designed to run in a cloud environment where the filesystem must be considered ephemeral, you also need to treat file storage or disk as a backing service. You shouldn’t be reading to or writing from files on disk like you might with regular enterprise applications. Instead, file storage should be a backing service that is bound to your application as a resource.

## 9. Environment parity

While some organizations have done more evolving, many of us have likely worked in an environment like this: the shared development environment has a different scaling and reliability profile than QA, which is also different than production. The database drivers used in dev and QA are different than production. Security rules firewalls, and other environmental configuration settings are also different. Some people have the ability to deploy to some environments, but not others. And finally, the worst part of it all, is people fear deployment, and they have little to no confidence that if the product works in one environment, it will work in another.

The purpose of applying rigor and discipline to environment parity is to give your team and your entire organization the confidence that the application will work everywhere.

## 10. Administrative processes

There is nothing inherently wrong with the notion of an administrative process, but there are a number of reasons why you should not use them. In certain situations, the use of administrative processes is actually a bad idea, and you should always be asking yourself whether an administrative process is what you want, or whether
a different design or architecture would suit your needs better. Examples of administrative processes that should probably be refactored into something else include:

* Database migrations;
* Interactive programming consoles (REPLs);
* Running timed scripts, such as a nightly batch job or hourly import;
* Running a one-off job that executes custom code only once.

## 11. Port binding

One extremely common pattern in an enterprise that manages its own web servers is to host a number of applications in the same container, separating applications by port number (or URL hierarchy) and then using DNS to provide a user-friendly facade around that server. 

Embracing platform-as-a-service here allows developers and devops alike to not have to perform this kind of micromanagement anymore. Your cloud provider should be managing the port assignment for you because it is likely also managing routing, scaling, high availability, and fault tolerance, all of which require the cloud provider to manage certain aspects of the network, including routing host names to ports and mapping external port numbers to container internal ports. The project uses configuration to achieve this goal:

```yaml
#
server:
  shutdown: graceful
  port: 8080
#
management:
  server:
    port: 8081
#
```

* [Source : application.yml](../src/main/resources/application.yml)

## 12. Stateless processes

Applications should execute as a single, stateless process. As mentioned earlier a modern cloud-native applications should each consist of a single stateless process. A stateless application makes no assumptions about the contents of memory prior to handling a request, nor does it make assumptions about memory contents after handling that request. The application can create and consume transient state in the middle of handling a request or processing a transaction, but that data should all be gone
by the time the client has been given a response.

To put it as simply as possible, all long-lasting state must be external to the application, provided by backing services. So the concept isn’t that state cannot exist; it is that it cannot be maintained within your application.

## 13. Concurrency

Concurrency advises us that cloud-native applications should scale out using the process model. There was a time when, if an application reached the limit of its capacity, the solution was to increase its size. If an application could only handle some number of requests per minute, then the preferred solution was to simply make
the application bigger.

A much more modern approach, one ideal for the kind of elastic scalability that the cloud supports, is to scale out, or horizontally. Rather than making a single big process even larger, you create multiple processes, and then distribute the load of your application among those processes. Most cloud providers have perfected this capability to the point where you can even configure rules that will dynamically scale the number of instances of your application based on load or other runtime telemetry available in a system.

If you are building disposable, stateless, share-nothing processes then you will be well positioned to take full advantage of horizontal scaling and running multiple, concurrent instances of your application so that it can truly thrive in the cloud.

## 14. Telemetry

Telemetry’s dictionary definition implies the use of special equipment to take specific measurements of something and then to transmit those measurements elsewhere using radio. There is a connotation here of remoteness, distance, and intangibility to the source of the telemetry. The use of telemetry should be an essential part of any cloud-native application. When it comes to monitoring your application, there are generally a few different categories of data:

* Application performance monitoring (APM);
* Domain-specific telemetry;
* Health and system logs.

The first of these, APM, consists of a stream of events that can be used by tools outside the cloud to keep tabs on how well your application is performing. This is something that you are responsible for, as the definition and watermarks of performance are specific to your application and standards. The data used to supply APM dashboards is usually fairly generic and can come from multiple applications across multiple lines of business.

The second, domain-specific telemetry, is also up to you. This refers to the stream of events and data that makes sense to your business that you can use for your own analytics and reporting. This type of event stream is often fed into a “big data” system for warehousing, analysis, and forecasting.

Finally, health and system logs are something that should be provided by your cloud provider. They make up a stream of events, such as application start, shutdown, scaling, web request tracing, and the results of periodic health checks. The project implements telemetry using the capabilities of Sprinboot Framework:

```yaml
#
management:
  server:
    port: 8081
  endpoints:
    web:
      exposure:
        include: "info,health,prometheus"
#
spring:
  main:
    cloud-platform: kubernetes
#
```

* [Source : application.yml](../src/main/resources/application.yml)

This configuration will allow the application to expose the following probes for telemetry:

* [List of available probes](http://domaine:8081/actuator)
* [Informational probe](http://domaine:8081/actuator/info)
* [Health status probe](http://domaine:8081/actuator/health)
* [Activity Status Probe](http://domaine:8081/actuator/health/liveness)
* [Probe availability status](http://domaine:8081/actuator/health/readiness)
* [Statistics probe (prometheus](http://domaine:8081/actuator/prometheus)

## 15. Authentication and authorization

Security is a vital part of any application and cloud environment. Security should never be an aferthought. All too often, we are so focused on getting the functional requirements of an application out the door that we neglect one of the most important aspects of delivering any application, regardless of whether that app is destined for an enterprise, a mobile device, or the cloud.

A cloud-native application is a secure application. Your code, whether compiled or raw, is transported across many data centers, executed within multiple containers, and accessed by countless clients—some legitimate, most nefarious. Even if the only reason you implement security in your application is so you have an audit trail of which user made which data change, that alone is benefit enough to justify the relatively small amount of time and effort it takes to secure your application’s endpoints.

In an ideal world, all cloud-native applications would secure all of their endpoints with RBAC (role-based access control). Every request for an application’s resources should know who is making the request, and the roles to which that consumer belongs.

### Dependencies

Enabling security is done by adding the following dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

* [Source : pom.xml](../pom.xml)

### Configuration

You must also configure the security that is appropriate for the application:

```java
http.cors(withDefaults());

http.csrf(csrf -> 
    csrf.disable());

http.authorizeHttpRequests(auth -> 
    auth.requestMatchers(AUTH_WHITELIST).permitAll().anyRequest().authenticated());

http.exceptionHandling(except -> 
    except.authenticationEntryPoint(exceptionHandlingAuthEntryPoint()));

http.sessionManagement(sm -> 
    sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

http.authenticationProvider(authenticationProvider());

http.addFilterBefore(authentificationTokenFilter(jwtUtils(), userDetailsService()),
                        UsernamePasswordAuthenticationFilter.class);

return http.build();
```

* [Source : SecurityConfig.java](../src/main/java/org/rd/fullstack/springbootnuxt/config/SecurityConfig.java)
* [Documentation : SpringSecurity](https://docs.spring.io/spring-security/reference/index.html)

## Links/references used for writing

* [The original 12 factors](https://12factor.net/fr/)
* [Beyond the 12 factors: 15-factor cloud-native Java applications](https://developer.ibm.com/articles/15-factor-applications/)
