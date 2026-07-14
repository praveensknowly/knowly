FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN groupadd -r knowly && useradd -r -g knowly knowly
RUN mkdir -p /app/uploads/profile && chown -R knowly:knowly /app
COPY --from=build /app/target/knowly-*.jar app.jar
USER knowly
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod
VOLUME /app/uploads/profile
HEALTHCHECK --interval=30s --timeout=3s CMD wget -qO- http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
