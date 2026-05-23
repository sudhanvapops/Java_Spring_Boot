### Get Method

Invoke-RestMethod `
-Uri "http://localhost:8080/jobPosts" `
-Method GET


### Put REQUEST

Invoke-RestMethod `
-Uri "http://localhost:8080/jobPost" `
-Method PUT `
-ContentType "application/json" `
-Body '{
    "postId": 4,
    "postProfile": "Java Backend Spring Developer",
    "postDesc": "Build REST APIs and Microservices",
    "reqExperience": 3,
    "postTechStack": [
        "Java",
        "Spring",
        "Spring Boot"
    ]
}'


OR

Invoke-RestMethod `
-Uri "http://localhost:8080/jobPost/4" `
-Method PUT `
-ContentType "application/json" `
-Body '{
    "postId": 4,
    "postProfile": "Java Backend Spring Developer",
    "postDesc": "Build REST APIs and Microservices",
    "reqExperience": 3,
    "postTechStack": [
        "Java",
        "Spring",
        "Spring Boot"
    ]
}'


### POST

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

### DELETE

Invoke-RestMethod `
-Uri "http://localhost:8080/jobPost/4" `
-Method DELETE