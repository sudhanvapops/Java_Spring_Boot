### Typical Spring Boot Workflow


# Build image
docker build -t spring-app .

# Run app
docker run -d -p 8080:8080 spring-app

# View logs
docker logs -f spring-app

# Stop
docker stop spring-app

# Remove
docker rm spring-app


