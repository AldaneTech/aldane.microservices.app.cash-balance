FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/cash-balance.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
