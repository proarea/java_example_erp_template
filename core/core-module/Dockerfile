FROM maven:3.8.4-openjdk-17 as build
COPY . /app
COPY ./pom.xml /app

WORKDIR /app
RUN mvn -f pom.xml clean install -DskipTests

FROM maven:3.8.4-openjdk-17
COPY --from=build /app/core/core-module/target/*.jar core-module-1.0-SNAPSHOT.jar
ENTRYPOINT [ "java", "-jar", "core-module-1.0-SNAPSHOT.jar"]