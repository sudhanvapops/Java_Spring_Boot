### How to run


### create at root 
Dockerfile



FROM eclipse-temurin:21-jdk


# Sets the current working directory inside the container.
WORKDIR /app

# Copies a file from your computer into the container. to app.jar
COPY target/hello-demo.jar app.jar

EXPOSE 8080

# run the cmd at entry
ENTRYPOINT ["java", "-jar", "app.jar"]

# Build an image. 
# Give the image:
# Name: sudhello
# Tag: v1
# . Use the current directory as the build context.
docker build -t sudhello:v1 .


docker run -p 8080:8080 sudhello:v1