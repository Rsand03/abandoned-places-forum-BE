# ITI0302-2024 tiim32

## Overview
This project is a Spring Boot application with a PostgreSQL database. It uses Liquibase for database migrations.
The project is containerized using Docker and utilizes two separate `docker-compose` configurations:
1. One for the database (Liquibase and PostgreSQL)
2. One for the entire application

## Prerequisites
Before running the application, ensure you have the following installed:
- [Java JDK 21+](https://adoptopenjdk.net/)
- [Gradle](https://gradle.org/) (if building manually)
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

## How to Run the application

### 1. Running the Database (Liquibase and PostgreSQL)

Navigate to the `Database` folder and run the `docker-compose` file for the database:
```bash
cd database
docker-compose up -d
```

### 2. Running the application
Run Iti0302Application file (for example in IntelliJ)

