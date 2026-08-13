# =========================================================
# STAGE 1: Solo para construir el archivo .jar (Se descarta al terminar)
# =========================================================
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY . .
# Compila tu código Java y genera el booking-api.jar
RUN ./mvnw clean package -DskipTests

# =========================================================
# STAGE 2: La imagen real que se desplegará en el servidor
# =========================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Trae ÚNICAMENTE el .jar desde el Stage 1 (los archivos de código se tiran a la basura)
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]