# Best Practices for REST Development

The implementation of REST services must follow certain architectural principles to facilitate maintenance, evolution and the desired vision. This summary outlines the governance principles that should be applied to ease the design and conception of REST services.

## REST PRINCIPLES

REST services are based on specific principles and constraints. This architectural style is described by Roy Fielding in his doctoral thesis. It relies on the following six (6) principles:

**Uniform Interface**  
The uniform interface constraint defines the contract between clients and servers. It simplifies and decouples the architecture, allowing each part to evolve independently.  
The four principles of the uniform interface are:

- **Resource-Oriented**  
    Individual resources are identified in requests using URIs as resource identifiers. Resources themselves are conceptually distinct from the representations returned to the client. This representation can be HTML, XML, or JSON, always encoded in UTF-8.

- **Manipulation of Resources Through Representations**  
    A client with a representation of a resource (including metadata) has enough information to modify or delete the resource on the server, depending on the permissions granted.

- **Self-Descriptive Messages**  
    Each message contains enough information to describe how to process it. Example: specifying the desired MIME type. Responses also explicitly indicate their cacheability.

- **Hypermedia as the Engine of Application State (HATEOAS)**  
    A client requests state via content, parameters, headers, and URI. The service delivers state to the client via content, response codes, and headers. HATEOAS also means (when required) links to retrieve the resource itself and/or other related resources.

**Stateless**  
Essentially, this means that the state required to handle the request is contained within the request itself, either in the URI, query parameters, body, or headers. The URI only identifies the resource, and the content contains the state (or state change) of that resource.

**Cacheable**  
Clients can cache responses. Responses must implicitly or explicitly define themselves as "cacheable" or not. This allows the client to behave responsibly with the resource.

**Client-Server**  
The uniform interface separates the client from the server. This separation of concerns means the client is not concerned with data storage, and the server is not concerned with the user interface.

**Layered System**  
A client cannot tell whether it is connected directly to the end server or to an intermediary along the way. Intermediary servers can improve system scalability by enabling load balancing, providing shared caches, and enforcing security policies.

**Code on Demand (optional)**  
Servers can temporarily extend or customize client functionality by transferring logic that the client can execute. Examples include compiled components like Java applets and client-side scripts such as JavaScript. The standard for the AGL system prohibits this usage.

### HTTP VERBS

Most implementations use the HTTP transport layer. HTTP verbs are an important part of our "uniform interface" constraint. The primary and most used HTTP verbs are: POST, GET, PUT, and DELETE. These correspond to create, read, update, and delete operations (CRUD). Other verbs exist but are less frequently used: PATCH, OPTIONS, and HEAD.

### UNIFORM RESOURCE LOCATOR (URL)

Defining a standard for identifying a resource via a URL is essential. We establish this standard using "IBM Cloud Managed Services" as a reference. The structure of a URL and its rules are as follows:

```code
https://{host}:{port}/{srv}/{node}/{resource}-{id}/{operation}?{query_parameters}
```

Example: <https://cloudserver.mycompany.com:9443/agi/users/user-1234>

The following table describes the possible values for the variables that make up a URL.

| Variable   | Description |
|------------|-------------|
| host       | Server name or IP address |
| port       | Port number |
| srv        | Service endpoint (AGL only) |
| node       | Node identification (always plural, e.g., tickets) |
| resource   | Resource, possibly with a prefix (e.g., ticket-1234) |
| operation  | Optional operation specifying an action beyond standard CRUD (e.g., exclude) |
| query      | Optional query parameters to filter the returned resources |

Example for operation:  
<https://cloudserver.mycompany.com:9443/agi/users/user-1234/exclude>  

Exclusion may involve changing the user's status, resetting quotas, banks, and reserves, and sending an email. The response may be a custom resource for this operation.

**IMPORTANT**  
Query parameters can only be specified for requests using the HTTP/GET verb. Note: a resource may include a sub-resource. Example: orders/products

### OTHER NORMS AND STANDARDS

Implementing services may require additional norms and standards to address specific needs. Generally, these needs rely on header tags.

- **Idempotence**  
    An operation (or service call) is idempotent if a client can make the same call repeatedly and produce the same result, like an assignment in programming: X=5. In other words, making multiple identical requests to the same URL yields the same result. PUT and DELETE verbs are generally defined to be idempotent.

- **Authentication/Authorization**  
    The best practice is to use "OAuth" for authentication (or alternatives). The AGL system uses an alternative form. For more on OAuth specification, see: <http://oauth.net/documentation/spec/>.  
    Authorization occurs at the time of the service call. The token obtained during authentication is used to verify access rights (usually roles).

- **Limiting Results**  
    There are two approaches to limiting results: one via the header tag (Range: items=0-24 for items 1 to 25), and another via the request (HTTP/GET): ?offset=0&limit=24. The response must also specify the limitation with the header tag (Content-Range: items 0-24/66, where 66 is the total count and does not start at zero).

- **Pagination**  
    Pagination follows the same constraints as result limitation.

- **Cache/Scalability**

- **Language**  
    Language is specified with the header tag: Accept-Language.

- **Version**  
    The version must always be specified with the header tag: Accept. The response specifies the content using: Content-Type.

- **Date/Time**  
    Dates and timestamps can be tricky if not handled properly and consistently. Time zone issues and daylight saving changes are typical problems.  
    Internally, services should store, process, and cache using UTC or GMT. The AGL system does not currently follow these principles. This should be implemented in a future version.

## RETURN CODES (HTTP TOP 10)

The most commonly used HTTP status codes to signal the state of a REST service are shown below:

**HTTP Status Codes**  
**2xx – Success**  

- 200 OK: Success. Most used code to indicate success.  
- 201 Created: Successful creation. Also adjust the header with the "Location" tag with a link to the resource. The resource may or may not be present in the body.  
- 204 No Content: Used when responses are not needed and nothing is in the body. Example: DELETE.

**3xx – Redirection**  

- 304 Not Modified: Used in response to a conditional GET to reduce bandwidth usage. If used, then the date, Content-Location, and Etag tags must have the value that would have been on a standard call.

**4xx – Client Error**  

- 400 Bad Request: General error code when the request would cause an invalid state. Domain validation errors, missing data, etc.  
- 401 Unauthorized: Error code for missing or invalid authentication token.  
- 403 Forbidden: Error code for a user not authorized to perform the operation, lacking rights to access the resource, or the resource is unavailable for any reason.  
- 404 Not Found: Error code when the requested resource is not found, does not exist, or when a 401 or 403 is masked for security reasons.  
- 409 Conflict: Whenever a resource conflict would be caused by executing the request: duplicate, incomplete cascade deletion.

**5xx – Server Error**  

- 500 Internal Server Error: General catch-all for server-side errors.

Note: These are the top 10 HTTP status codes most used for REST service implementation.

**Design and Implementation**  
Using the specifications and governance rules for defining REST services, we get:

```code
https://{host}:{port}/{srv}/{node}/{resource}-{id}/{operation}?{query}
```

| Verb | URL | Description |
|------|-----|-------------|
| POST | agi/accounts | Creates a WebCash. The returned resource is AccountResource. |
| GET | agi/accounts/account-{accountID} | Retrieves information about a WebCash. The returned resource is AccountResource. |
| PUT | agi/accounts/account-{accountID}/consumes | Consuming a WebCash product is complex (multiple actions). The returned resource is a composition (custom information): ConsumesResource. We added an extension to the URL using an operation: consumes. Optionally, return HATEOAS links for account and transaction information. |

**Links Example:**

- rel: account  
    href: agi/accounts/account-{accountID}
- rel: transaction  
    href: agi/accounts/account-{accountID}/transactions/transaction-{transactionID}

Note: Ensure the verb is idempotent.
