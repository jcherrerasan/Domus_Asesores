FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY backend/crm_domus/pom.xml pom.xml
COPY backend/crm_domus/src src

RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
