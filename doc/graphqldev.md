# Best Practices for GraphQL Caching

GraphQL supports caching at both server and client levels, but its flexibility and dynamic query structure introduce complexity. Unlike RESTful APIs, where URLs can be used for straightforward caching, GraphQL’s single endpoint and POST-based queries make standard caching strategies less effective. Therefore, caching in GraphQL requires a more thoughtful approach tailored to each application's needs.

## 1. Challenges with GraphQL Caching

GraphQL queries typically use POST requests to a single endpoint, passing parameters in the request body. This prevents caching based on URLs, as is common with REST APIs. While client-side libraries like Apollo Client offer object-level caching using global IDs, this is more of a workaround than a comprehensive solution. Client-side caching can also increase JavaScript payloads and complexity, potentially impacting performance on low-end devices.

## 2. Accessing GraphQL via GET

To leverage HTTP caching, you can use GET requests with queries and variables encoded as URL parameters (e.g., `/graphql?query=...&variables=...`). This method is suitable for query operations but not for mutations, which must use POST and cannot be cached. Care must be taken to avoid serving stale or incorrect data, so cache invalidation strategies are essential.

## 3. Encoding GraphQL Queries in URLs

Multi-line GraphQL queries must be encoded for use in URLs. For example:

```graphql
query {
  user(id: 482) {
    name
    email
  }
}
```

becomes:

```
%7B%0A++user(id%3A+482)+%7B%0A++++name%0A++++email%0A++
```

While encoding works, it reduces readability. Alternatively, newlines can be replaced with spaces, but this is less practical for complex queries. Ideally, tools should help construct and encode queries for HTTP caching.

### Persisted Queries

Persisted queries store queries on the server and use a unique identifier (e.g., a hash or numeric ID) to retrieve them. Clients send the ID and variables, reducing bandwidth and improving security. For example:

```json
{
  "id": "abc123",
  "variables": { "minPrice": 50 }
}
```

The server executes the stored query associated with the ID. Persisted queries simplify HTTP caching and can be implemented using native server support or libraries.

### Calculating `max-age` for Cache-Control

The `Cache-Control` header’s `max-age` value determines how long a response can be cached. For GraphQL, you can assign a `max-age` to each field in the schema and calculate the lowest value for a query. For example:

```graphql
type User {
  id: ID @cacheControl(maxAge: 31557600)
  url: URL @cacheControl(maxAge: 86400)
  name: String @cacheControl(maxAge: 3600)
  loginstatus: Int @cacheControl(maxAge: 60)
}
```

The server traverses the query’s AST to determine the appropriate `max-age` and sets it in the response header.

## 4. Using Directives for Cache Control

Custom directives like `@cacheControl(maxAge: Int)` allow you to specify cache durations for fields in your schema. This enables fine-grained control over caching behavior and helps ensure responses are cached appropriately.

## Importance of GraphQL HTTP Caching

HTTP caching is valuable for static, shared data, especially in gateway caches. However, shared caches should not cache requests with Authorization headers, limiting usefulness for authenticated APIs. Private caches (e.g., browsers) can still benefit, but frequent query invalidation and lack of shared data reduce effectiveness. For APIs requiring fresh data, validators like ETag and Last-Modified may be impractical.

GraphQL excels for authenticated APIs and real-time data. If your API serves mostly static, public data, consider architectures that better leverage HTTP caching.

## Wrapping Up

While REST has traditionally been better suited for server-side caching, GraphQL can benefit from caching through persisted queries and careful use of HTTP caching strategies. By combining GraphQL with caching techniques, developers can improve API performance.

For more on modularization with GraphQL, see our guide on [Modularization with GraphQL Modules](#).

[MDN: Cache-Control](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cache-Control)

## 99. Source Files

* [AuthController.java](../src/main/java/org/rd/fullstack/springbootnuxt/controller/AuthController.java)
* [BookController.java](../src/main/java/org/rd/fullstack/springbootnuxt/controller/BookController.java)
* [HealthController.java](../src/main/java/org/rd/fullstack/springbootnuxt/controller/HealthController.java)
* [ReportController.java](../src/main/java/org/rd/fullstack/springbootnuxt/controller/ReportController.java)

### Configuration Guidelines

Configuration includes:

- URLs and information about backing services (web services, SMTP servers)
- Database connection details
- Credentials for third-party services (AWS, Google Maps, Twitter, Facebook)
- Properties or configuration files (XML, YML)

Configuration does not include internal application details that remain constant across deployments. Credentials should never be stored in code or bundled resources.

Example configuration:

```yaml
org:
  rd:
    fullstack:
      springbootnuxt:
        secret: the.beautiful.secret.key.to.change
        expiration: 30000
        authorities: rd.roles
```

* [application.yml](../src/main/resources/application.yml)
* [Spring Boot External Configuration](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.external-config)
* [pom.xml](../pom.xml)

### Security Configuration Example

```java
http.cors(withDefaults());
http.csrf(csrf -> csrf.disable());
http.authorizeHttpRequests(auth -> auth.requestMatchers(AUTH_WHITELIST).permitAll().anyRequest().authenticated());
http.exceptionHandling(except -> except.authenticationEntryPoint(exceptionHandlingAuthEntryPoint()));
http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
http.authenticationProvider(authenticationProvider());
http.addFilterBefore(authentificationTokenFilter(jwtUtils(), userDetailsService()), UsernamePasswordAuthenticationFilter.class);
return http.build();
```

## References

* [The original 12 factors](https://12factor.net/fr/)
* [Beyond the 12 factors: 15-factor cloud-native Java applications](https://developer.ibm.com/articles/15-factor-applications/)
