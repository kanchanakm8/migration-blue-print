FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY . /app
RUN ./mvnw -q -DskipTests package || mvn -q -DskipTests package
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/target/spring-app-0.0.1-SNAPSHOT.jar"]
