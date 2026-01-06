# httpexchange-server/Dockerfile
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
# 因为 docker build 的上下文我们将设置为模块目录，所以直接 copy target 即可
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
