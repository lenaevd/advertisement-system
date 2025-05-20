FROM openjdk:17
WORKDIR /app

COPY . .
RUN ./mvnw clean package -DskipTests
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/advertisements-1.0.0.jar"]