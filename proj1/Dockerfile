FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY *.java ./
RUN javac -d out *.java

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/out ./
COPY web ./web
EXPOSE 8080
CMD ["java", "AppServer"]
