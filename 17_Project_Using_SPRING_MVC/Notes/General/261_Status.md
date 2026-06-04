### Sednign Http Server Status Code

### 1xx – Informational

Request received, processing continues.

100 Continue → Continue sending the request.
101 Switching Protocols → Server is switching protocols.


### 2xx – Success

Request was successful.

200 OK → Request succeeded.
201 Created → Resource created successfully.
202 Accepted → Request accepted, processing later.
204 No Content → Success, but no response body.


### 3xx – Redirection

Further action is needed.

301 Moved Permanently → Resource moved permanently.
302 Found → Temporary redirect.
304 Not Modified → Use cached version.


### 4xx – Client Error

Problem with the client's request.

400 Bad Request → Invalid request.
401 Unauthorized → Authentication required.
403 Forbidden → Access denied.
404 Not Found → Resource doesn't exist.
405 Method Not Allowed → HTTP method not supported.
409 Conflict → Resource conflict.
415 Unsupported Media Type → Wrong content type.
429 Too Many Requests → Rate limit exceeded.


### 5xx – Server Error

Problem on the server side.

500 Internal Server Error → Generic server error.
501 Not Implemented → Feature not supported.
502 Bad Gateway → Invalid response from upstream server.
503 Service Unavailable → Server temporarily unavailable.
504 Gateway Timeout → Upstream server took too long.


Most Important for Spring Boot APIs
| Code | Meaning               |
| ---- | --------------------- |
| 200  | Success               |
| 201  | Created               |
| 204  | Success, no content   |
| 400  | Bad Request           |
| 401  | Unauthorized          |
| 403  | Forbidden             |
| 404  | Not Found             |
| 409  | Conflict              |
| 500  | Internal Server Error |


### How to use In Spring Boot

In return Type wrap it up with
ResponseEntity<>


While returning
ResponseEntity<>(Data,HttpStatus.)

