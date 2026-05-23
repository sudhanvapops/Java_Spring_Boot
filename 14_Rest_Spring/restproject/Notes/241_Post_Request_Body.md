### Post Request and Request Body

use @PostMapping

and use @RequestBody to accept data from client 
or send data from cliet to server


@RequestBody converts JSON → Java object

in return: You are returning to the HTTP client (browser / curl / React / Postman).
and sends it back as HTTP Response Body.


Client
 ↓ POST
Controller
 ↓ return
Spring
 ↓
HTTP Response
 ↓
Client receives JSON



###  testing

Invoke-RestMethod `
-Uri "http://localhost:8080/jobPost" `
-Method POST `
-ContentType "application/json" `
-Body '{
    "postId": 4,
    "postProfile": "Java Backend Spring Developer",
    "postDesc": "Build REST APIs",
    "reqExperience": 2,
    "postTechStack": [
        "Java",
        "Spring",
        "Spring Boot"
    ]
}'