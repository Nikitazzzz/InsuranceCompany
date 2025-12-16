# Быстрый старт для Tomcat 10

## Шаг 1: Сборка проекта
```bash
mvn clean package
```

## Шаг 2: Настройка БД
1. Создайте базу данных:
```bash
mysql -u root -p < database_schema_mysql.sql
```

2. Отредактируйте `src/main/webapp/WEB-INF/web.xml`:
   - Измените `db.user` и `db.password` на ваши данные MySQL

3. Пересоберите проект:
```bash
mvn clean package
```

## Шаг 3: Развертывание
1. Скопируйте `target/InsuranceCompany.war` в `$CATALINA_HOME/webapps/`
2. Запустите Tomcat
3. Откройте: `http://localhost:8080/InsuranceCompany/`

## Важно!
- Java версия: 17 (Tomcat 10 требует Java 11-17)
- MySQL должен быть запущен
- Проверьте параметры подключения к БД в web.xml

## Тестовые данные
После создания БД можно зарегистрировать первого пользователя через `/register`

