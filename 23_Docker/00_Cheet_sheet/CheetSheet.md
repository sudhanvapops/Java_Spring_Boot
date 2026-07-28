1. To get image from docker hub
docker pull "imagename"

### Search Hub
docker search imagename


2. docker run hello-world

Hello from Docker!
This message shows that your installation appears to be working correctly.

To generate this message, Docker took the following steps:
1. The Docker client contacted the Docker daemon.
2. The Docker daemon pulled the "hello-world" image from the Docker Hub.
(amd64)
3. The Docker daemon created a new container from that image which runs the
executable that produces the output you are currently reading.
4. The Docker daemon streamed that output to the Docker client, which sent it
to your terminal.



3. docker images
- list all the images u have


4. docker ps 
list all the running containers

List all containers
docker ps -a


5. docker rmi IMAGE_ID Or docker rmi -f IMAGE_ID
remove an image Or force remove


6. docker run -d nginx
run in detached mode

7. docker run --name my-nginx nginx
Run with Name



8. docker start container_name
Start Container 


9. Stop Container
docker stop container_name


10. Restart Container
docker restart container_nam


11. Logs
docker logs container_name



### Execute Commands Inside Container

docker exec -it container_name bash

if no bash

docker exec -it container_name sh

Run command
    docker exec container_name ls


### Build

if Dockerfile present
docker build .

With tag
docker build -t my-app .

Version
docker build -t my-app:v1 .


### Run Your Own Image

docker run my-app

Port mapping
docker run -p 8080:8080 my-app


### Environment Variables
docker run \
-e MYSQL_ROOT_PASSWORD=password \
mysql


### Container Resource Usage
docker stats


