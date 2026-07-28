### Dockerfile Basics

steps matter 

Example:

FROM openjdk:21
WORKDIR /app
COPY . .
RUN ./mvnw clean package
EXPOSE 8080
CMD ["java","-jar","target/app.jar"]



### Volumes

Create volume
docker volume create my-volume

List
docker volume ls

Mount
docker run -v my-volume:/data mongo

Delete
docker volume rm my-volume



### Networks

List
docker network ls

Create
docker network create my-network

Run on network
docker run --network my-network mongo

Delete
docker network rm my-network



### Copy Files

Host → Container
docker cp file.txt container:/app

Container → Host
docker cp container:/app/file.txt .



### Clean Up

Remove stopped containers
docker container prune

Remove unused images
docker image prune

Remove everything unused
docker system prune

Including volumes
docker system prune -a --volumes