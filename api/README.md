# Wedding API

Spring Boot 3 / Java 21 application for wedding events, guests, invitations, RSVPs.

## Build

mvn clean package

## Run locally

export DB_HOST=192.168.50.5
export DB_PORT=5432
export DB_NAME=wedding
export DB_USER=wedding_app
export DB_PASSWORD=wedding_app_123

java -jar target/wedding-api-0.0.1-SNAPSHOT.jar

## Docker

docker build -t wedding-api:latest .

docker run -e DB_HOST=192.168.50.5 -e DB_PORT=5432 -e DB_NAME=wedding -e DB_USER=wedding_app -e DB_PASSWORD=wedding_app_123 -p 8080:8080 wedding-api:latest

## Swagger UI

Visit [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

