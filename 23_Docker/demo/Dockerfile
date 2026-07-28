FROM openjdk:22-jdk

# Sets the current working directory inside the container.
WORKDIR /app

# Copies a file from your computer into the container. to app.jar
COPY hello-demo.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]