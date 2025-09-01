# Estágio 1: Build com Maven
# Usa uma imagem com Maven e JDK para compilar o projeto e gerar o .jar
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Imagem final
# Usa uma imagem Java "slim" (menor) e copia apenas o .jar gerado no estágio anterior
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]