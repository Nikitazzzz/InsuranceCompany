FROM eclipse-temurin:11-jre
WORKDIR /app
COPY target/*.war app.jar
ENV PORT 8080
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
