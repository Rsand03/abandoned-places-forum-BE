# ITI0302-2024 tiim32

## Overview
This project aims to create an interactive map that displays both public and private abandoned locations. Public places are visible to all users, while private locations are only accessible to the user who added them. Users can freely add private places, while adding public places requires a request to maintain map quality and avoid clutter. Each location can be tagged with labels like "visited" or "remember", allowing users to categorize and track their experiences. Every abandoned location will include details such as name, location, categories, condition, accessibility, and additional notes.

In the map view, users can filter visible locations by visibility (public/private), categories, and the tags they’ve assigned. Clicking on a location opens a sidebar with detailed information, including a link to a subpage dedicated to that place. This subpage aggregates posts and discussions about the location, including images, comments, and upvotes. Private locations can be deleted from the detailed view, allowing users to manage their entries effectively. The project provides a user-friendly platform to explore, document, and discuss abandoned places while ensuring proper privacy and data management.


## Prerequisites
Before running the application, ensure you have the following installed:
- [Java JDK 21+](https://adoptopenjdk.net/)
- [Gradle](https://gradle.org/) (if building manually)
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

## Technology stack listing

Java 21, Spring Boot, PostgreSQL, Liquibase, Docker

## How to run application

It is recommended to use IntelliJ IDE.

### 1. Running the Database

- Run Docker application in the background if you don't have PostgreSQL installed.

- Ensure that the predefined Postgres username and password match in both docker-compose and application.properties files.

- Navigate to the `Database` folder and run the `docker-compose` file:
```bash
cd Database
docker-compose up -d
```

### 2. Running the application locally

- Test PostgreSQL database connection status in the Database menu in Intellij (upper right corner).

- Run Iti0302Application.java located in src/main/java/ee/taltech/iti0302project/app.

- After that you should be able to send http requests to for example http://localhost:8080/api/locations


## How to Build the Project

- To build the project, you can use **Gradle** directly without the need for Docker. Make sure you have **Gradle** installed on your local machine or use a compatible environment like the `gradle:jdk21` Docker image if desired.
- **PostgreSQL Connection**: Building the application requires a PostgreSQL connection. Ensure that a PostgreSQL instance (e.g., version 14.1) is running locally or via Docker if needed.
- **Build Script**:
  - Perform a clean build of the application by running:
    ```bash
    ./gradlew clean build
    ```
- The compiled `.jar` file will be stored as an artifact in the `build/libs` directory.


## How to Build and Run Docker Containers

The project is containerized using Docker and utilizes two separate `docker-compose` configurations:
1. One for the database (PostgreSQL)
2. One for the entire application

To containerize and run your Spring Boot application using Docker, follow these steps:

### 1. Build the docker image

To build the Docker image for your application, you need to have a `Dockerfile` and the compiled `.jar` file available. The `Dockerfile` provided uses a base image with Java 21, sets up a non-root user for security, copies the `.jar` file, and specifies how the container should run your application.

To build a container do the following and replace $PLACEHOLDER with your project-specific values:

1. **Login to the Docker Registry**:

   First, authenticate with your Docker registry using your username and password:
    ```bash
    docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD $DOCKER_REGISTRY
    ```
2. Build the Docker Image:

Build the Docker image using the Dockerfile in the current directory.
The image is tagged with a unique commit hash to differentiate it:
  ```bash
   docker build -t $DOCKER_REGISTRY/$DOCKER_IMAGE-$CI_COMMIT_SHORT_SHA .
   ```
3. Push the Docker Image:

   Push the newly built image to the Docker registry:
   ```bash
   docker push $DOCKER_REGISTRY/$DOCKER_IMAGE-$CI_COMMIT_SHORT_SHA
   ```
4. Tag the Docker Image:
   After pushing the commit-specific image, tag it with a more generic tag (e.g., latest) for easier access:
    ```bash
   docker tag $DOCKER_REGISTRY/$DOCKER_IMAGE-$CI_COMMIT_SHORT_SHA $DOCKER_REGISTRY/$DOCKER_IMAGE
   ```
5. Push the Tagged Image:
   Finally, push the newly tagged image (e.g., latest) to the registry:
    ```bash
   docker push $DOCKER_REGISTRY/$DOCKER_IMAGE
   ```

### 2. Run the Docker Container

1. Create a `docker-compose.yml` File:
   Ensure the following `docker-compose.yml` file is in your project directory:

   ```yaml
   version: "3.7"  # Use this version unless you upgrade Docker Compose
   services:
     web-project:
       image: <registry-name>/<project-name>:latest
       container_name: web-project
       restart: always
       ports:
         - "8080:8080"
       volumes:
         - "/opt/web-project/application.properties:/app/application.properties"
    ```

2. From the directory containing your docker-compose.yml, run the following command to start the container:
    ```bash
   docker-compose up -d
   ```
