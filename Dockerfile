# Build stage
FROM maven:3.9.9-amazoncorretto-21-debian as build
WORKDIR /app
COPY . .

RUN mvn clean package -DskipTests=true
RUN mvn install -DskipTests=true

# Runtime stage
FROM amazoncorretto:21.0.6-alpine3.20
WORKDIR /run
COPY --from=build /app/target/task_manager-0.0.1.jar /run/task_manager-0.0.1.jar

EXPOSE 8080

ENTRYPOINT java -jar /run/task_manager-0.0.1.jar