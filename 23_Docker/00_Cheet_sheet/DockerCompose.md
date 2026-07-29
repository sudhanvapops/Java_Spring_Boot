### Docker Compose

- when you are working with multiple containers one way is to use compose

Docker → Runs one container.
Docker Compose → Runs multiple containers together as one application

use docker-compose.yml


Example:

Your application needs:
    Spring Boot backend
    PostgreSQL database
    Redis cache

Without Compose, you would run three separate docker run commands.


With Compose, you write one YAML file and simply run:
    docker compose up
    Everything starts together.


Without Compose you must:

    Start PostgreSQL
    Start Redis
    Create Docker network
    Connect containers
    Mount volumes
    Set environment variables
    Start backend

every time


### What does Docker Compose do?

It lets you describe your entire application in one file.

Example:

services:
  backend:
    ...

  postgres:
    ...

  redis:
    ...



### Main Components

1. services
    Every container is called a service.

Example

services:
  backend:
  postgres:
  redis:

three containers


2. image
Use an existing Docker image.
postgres:
  image: postgres:16


3. build
    Instead of downloading an image,
    build one from your Dockerfile.

backend:
  build: .


4. ports
Maps container port to your computer.

ports:
  - "8080:8080"

Your PC:8080
      ↓
Container:8080


5. volumes

Keeps data even after container deletion.

Without volume

    Delete container
    ↓
    Database gone


With volume

    Delete container
    ↓
    Database survives


6. environment

Pass environment variables.

environment:
  POSTGRES_USER: admin
  POSTGRES_PASSWORD: secret



7. depends_on

Start another service first.
backend:
  depends_on:
    - postgres


Start PostgreSQL
↓
Start Backend



8. networks

Allows containers to communicate.

Usually Compose creates one automatically.

Backend
    │
    │
Postgres

Backend can simply connect to

postgres:5432

instead of an IP address.




### Common Commands

Start everything
docker compose up

Start in background
docker compose up -d

Stop containers
docker compose stop

Stop and remove containers
docker compose down

Rebuild images
docker compose up --build

View running services
docker compose ps



### Why do many developers prefer Docker for databases and Redis?

Because you don't have to:
    Install PostgreSQL on your computer.
    Install Redis.
    Worry about version mismatches (e.g., PostgreSQL 15 vs. 16).
    Manually create databases and configure services every time.

