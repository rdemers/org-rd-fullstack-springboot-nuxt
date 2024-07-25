# Best practices for graphQL caching

Although GraphQL has the capability to cache data at both the server and client levels, it has been critiqued for adding complexity to the caching process. The problem lies not so much in GraphQL's inherent inability to cache, but rather in how flexible and dynamic it may be when it comes to caching. In other words, because of the different structure and intricacy of the data being requested, standard caching strategies could not function as effectively with GraphQL. As a result, caching with GraphQL necessitates a more deliberate and sophisticated strategy that considers the unique requirements of every application.

What then is the remedy? Using the standards—specifically, HTTP caching—is the solution. GraphQL by itself is not able to employ HTTP caching, but since we want to leverage this standard, we can tackle the issue in a different way. Rather than pondering about how to cache GraphQL, let's see whether we can use GraphQL to facilitate HTTP caching. In this article, let's investigate this concept.

## 1. Challenges with GraphQL caching
Compared to conventional RESTful APIs, caching in GraphQL is a little more complicated due to its own architecture and execution methodology. Caching becomes difficult because all GraphQL queries need a POST request to run against a single endpoint and pass parameters in the request body. Because GraphQL's endpoint URL generates distinct replies than RESTful APIs, where the URL can be used as an identification, caching based on URL is not feasible.

Nevertheless, client-side caching tools such as Apollo Client provide cache support for GraphQL. Using their distinct global IDs, individual objects are cached independently in this caching technique, which improves GraphQL query performance. However, this caching strategy is seen as more of a workaround than an ideal one.

The primary issue is that GraphQL is unable to identify and cache data for all entities in the response at once utilising the URL to do so on the server. Actually, there are some disadvantages to client-side caching as well. For example, it increases the amount of client-side JavaScript, which might cause low-end smartphones to perform worse; additionally, the application becomes more sophisticated; and lastly, implementing the caching layer requires knowledge of JavaScript.

## 2. Accessing GraphQL via GET
The initial step in implementing HTTP caching in GraphQL is to cache the response from the query using the URL as the identifier. However, there are two major implications of this strategy that must be considered. First, it needs to use the GET method to visit GraphQL's single endpoint. Second, the query and variables are passed as URL parameters. For instance, we can do a GET operation on the URL /graphql?query=...&variables= if there is only one endpoint, graphql.
It's important to note that this approach is only applicable when retrieving data from the server via the query operation. When it comes to mutating data via the mutation operation, we must continue to use the POST method. This is because mutations are always executed fresh, which means we cannot cache their results. Therefore, HTTP caching is not suitable for mutations.

Although this approach is suggested to be used in the official GraphQL site, there are certain considerations we must bear in mind. For instance, caching responses using the URL as the identifier can lead to caching the same response for different queries. This can result in inaccurate or stale data being served to the client. Thus, it's essential to ensure that the caching strategy is well-designed and that the cache is invalidated appropriately to avoid serving stale data.


## 3. Coding GraphQL queries via URL param
When constructing a GraphQL query, it's common to have queries spanning multiple lines. However, we cannot input such multi-line strings directly in the URL parameter. To overcome this, we can encode the query string. For instance, the GraphQL client will encode a query like this:

```json
query {
    user(id: 482) {
        name
        email
    }
}
```

into this:

%7B%0A++user(id%3A+482)+%7B%0A++++name%0A++++email%0A++

Although this tactic works well, it can make the question difficult to understand and interpret. Although the queries in GraphQL are easy to comprehend, once they are encoded, they are only machine-readable.

Since newlines give no semantic significance to the query, an alternate method would be to replace every newline with a space. This method can be challenging to see and comprehend, so it can not be appropriate for complicated queries with a lot of brackets, field arguments, and directives.

As an illustration, have a look at this intricate query, which uses a full GraphQL query to retrieve details on five product plans, including plan names, prices, and sessions:

```graphql
{
  product_plans(limit: 5) {
    id
    plan_name
    pricing
    sessions(limit: 3, orderBy: "date|DESC") {
      id
      date
      developer {
        type
      }
      error_context
    }
  }
}
```

This query can be represented as a single-line query as follows:
{product_plans(limit:5){id plan_name pricing sessions(limit:3,orderBy:"date|DESC"){id date developer{type}error_context}}}
However, this approach makes it challenging to comprehend the query structure, especially if it also contains fragments. Therefore, an ideal solution would be to use a tool that allows us to construct queries using a more user-friendly interface while still preserving the benefits of encoding for HTTP caching.
GraphQL over HTTP
The good news is that the GraphQL community has noticed this problem and is working to improve the GraphQL over HTTP definition. By doing this, GraphQL servers, clients, and libraries will all be able to communicate GraphQL queries using URL parameters according to a single standard.
Nevertheless, it appears that this project is moving slowly, and the current specification is insufficient for everyday use. This leaves us with two choices: either we look into other options or we wait for an arbitrary amount of time for additional advances.

Persisted queries to the rescue
In situations where passing the query via URL is not optimal, an alternative approach is to employ a "persisted query." In this method, the query is stored on the server, and a unique identifier (such as a numeric ID or a hash produced by applying a hashing algorithm to the query) is used to retrieve it. The identifier is then passed as the URL parameter instead of the query.
Let's take an example where you have a GraphQL server that customers can use to query for product information. You wish to enforce a rigorous schema and limit the queries that may be conducted rather than enabling customers to send arbitrary queries. You choose to use persistent queries to accomplish this.
To begin, you define a few queries to allow and save them on the server. You could, for example, save the following queries:

```json
query ProductQuery1 {
  products(category: "electronics") {
    id
    name
    price
  }
}
```

```json
query ProductQuery2 {
  products(category: "clothing") {
    id
    name
    price
  }
}
```

After this, one can generate a unique identifier (such as a hash or a numeric ID) for each query and store it on the server alongside the query. For example, you might generate the following identifiers:    
ProductQuery1 -> "abc123"
ProductQuery2 -> "def456"
Now we can see that whenever a client wants to perform a query, it sends a POST request to the GraphQL server with the following JSON payload:

```json
{
  "id": "abc123",
  "variables": { "minPrice": 50 }
}
```

The "id" field specifies the ID of the query to execute, and the "variables" field specifies any variables that the query requires.
The GraphQL server looks up the query corresponding to the specified ID, and executes it with the specified variables. Since the query is already stored on the server, it doesn't need to be sent in the request body, which saves bandwidth and reduces the risk of injection attacks.
By utilizing persisted queries, implementing HTTP caching becomes much simpler and more efficient.
To make use of persisted queries in your GraphQL server, you can look for a GraphQL server that natively supports this feature or through a library. Once implemented, HTTP caching can be readily employed.
Calculating the max-age value
In HTTP caching, the Cache-Control header is used to determine how long a response can be cached by an HTTP client (such as a browser or a GraphQL client). One of the values that can be set for the Cache-Control header is max-age, which indicates the maximum amount of time in seconds that a response can be cached before it becomes stale.

Here's an example of how the max-age values could be calculated for different GraphQL queries:
Query 1:

```json
{
  users {
    id
    url
    name
    karma
  }
}
```

In this query, the lowest max-age value is 1 minute (for field karma), so the max-age for the entire response should be 60 seconds.
Query 2:

```json
{
  user(id: "123") {
    id
    url
    name
  }
}
```

In this query, the lowest max-age value is 1 hour (for field name), so the max-age for the entire response should be 3600 seconds.
Query 3:

```json
{
  me {
    id
    url
    name
    karma
  }
}
```

In this query, the max-age must be no-store, as it's querying data from the logged-in user.
To implement this behaviour in a GraphQL server, you could define a max-age value for each field in the GraphQL schema (using comments, for example), and then traverse the AST (Abstract Syntax Tree) of the incoming query to calculate the lowest max-age value for the requested fields. Finally, you would send the calculated max-age value as the Cache-Control header in the HTTP response.

## 4. Adding directives to calculate the max-age value

In order to calculate the response's max-age value for a GraphQL query, directives can be added to the schema. Directives are assigned to fields and their configurations can be customized via directive arguments. In this case, a @cacheControl directive can be created with an argument maxAge of type Int, which measures seconds. If no value is provided, a predefined default max-age is used. The max-age value for each field can then be specified in the schema using the SDL. For example, the max-age value for the User entity's fields can be defined as follows:    

directive @cacheControl(maxAge: Int) on FIELD_DEFINITION

```json
type User {
  id: ID @cacheControl(maxAge: 31557600)
  url: URL @cacheControl(maxAge: 86400)
  name: String @cacheControl(maxAge: 3600)
  loginstatus: Int @cacheControl(maxAge: 60)
}
json

In this example, the max-age values for the User entity's fields are based on the behaviour assigned to each field. The ID field, which will never change, is given a max-age of 1 year. The URL field, which will be updated randomly if ever, is given a max-age of 1 day. The name field, which may change every now and then, is given a max-age of 1 hour. The loginstatus field, which can change at any time, is given a max-age of 1 minute. If querying the data from the logged-in user, then the response can't be cached at all, which is indicated by the max-age of 0 for the me field in the Root entity.
The importance of GraphQL HTTP Caching

GraphQL HTTP caching is a useful mechanism for data that remains static and can be shared among multiple users, especially in the case of gateway caches. However, there is a debate surrounding the effectiveness of HTTP caching for authenticated web APIs.
It is interesting to note that shared caches should not cache any request with an Authorization header, which is relevant for authenticated APIs. On the other hand, private caches such as browser and client-side caches can still benefit from GraphQL HTTP caching, but its usefulness is limited for GraphQL due to the frequency of query invalidation and the lack of shared data.

Moreover, web APIs that require up-to-date data cannot afford to have stale data for long, making freshness headers less useful. Validators like ETag and Last-Modified require the server to retrieve data and run business logic to be computed, which can be time-consuming. While serialization and bandwidth can be saved, implementing Etag or Last-Modified generation for a GraphQL query may not be practical in some cases.
GraphQL is well-suited for authenticated APIs and real-time data that changes frequently, rather than serving long-lived data as a public API. Therefore, if your API primarily serves long-lived data, it may be beneficial to consider an API architecture that utilizes HTTP caching in a more effective way.
Wrapping up

REST has always had the upper hand in the discussion between GraphQL and server-side caching. This does not preclude GraphQL from being able to enable HTTP caching, though.

GraphQL can additionally take use of caching by keeping the query on the server and obtaining it using a "persisted query" through GET with an ID argument. Implementing GraphQL caching is more than worth it, due to the potential benefits of this trade-off. If you want some trust-worthy source for modularization with graphQl then visit our page which talks about Learning how to use Modularization with GraphQL Modules.
Because GraphQL and caching are complementary ideas, developers can enhance the performance of their "GraphQL" APIs by adopting caching techniques. 

https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cache-Control

## 99. 

* [Source : AuthController.java](../src/main/java/org/rd/fullstack/springbootnuxt/controller/AuthController.java)
* [Source : BookController.java](../src/main/java/org/rd/fullstack/springbootnuxt/controller/BookController.java)
* [Source : HealthController.java](../src/main/java/org/rd/fullstack/springbootnuxt/controller/HealthController.java)
* [Source : ReportController.java](../src/main/java/org/rd/fullstack/springbootnuxt/controller/ReportController.java)


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

## Links/references used for writing

* [The original 12 factors](https://12factor.net/fr/)
* [Beyond the 12 factors: 15-factor cloud-native Java applications](https://developer.ibm.com/articles/15-factor-applications/)
