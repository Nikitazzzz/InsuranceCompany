# Этап сборки
FROM maven:3.8.4-openjdk-11 AS build
WORKDIR /app
COPY . .
RUN mvn clean package

# Этап запуска
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/target/*.war app.war

# Render автоматически устанавливает переменную PORT
ENV PORT 8080
EXPOSE 8080

# Запуск приложения
CMD ["java", "-jar", "app.war"]
