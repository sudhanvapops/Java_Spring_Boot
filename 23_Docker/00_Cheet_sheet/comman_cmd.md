docker images
docker ps
docker ps -a

docker build -t app .

docker run -d -p 8080:8080 app

docker stop container

docker start container

docker restart container

docker logs -f container

docker exec -it container bash

docker rm container

docker rmi image

docker compose up -d

docker compose down