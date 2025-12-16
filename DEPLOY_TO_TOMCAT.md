# Инструкция по развертыванию на Tomcat 10

## Требования
- Java 17 (Tomcat 10 поддерживает Java 11-17)
- Apache Tomcat 10.x
- MySQL 8.0+
- Maven 3.6+

## Шаг 1: Подготовка базы данных

1. Убедитесь, что MySQL запущен
2. Выполните SQL скрипт:
```bash
mysql -u root -p < database_schema_mysql.sql
```

## Шаг 2: Настройка подключения к БД

Отредактируйте файл `src/main/webapp/WEB-INF/web.xml` и измените параметры подключения:

```xml
<context-param>
    <param-name>db.url</param-name>
    <param-value>jdbc:mysql://localhost:3306/InsuranceCompany?useSSL=false&amp;serverTimezone=UTC&amp;characterEncoding=UTF-8</param-value>
</context-param>
<context-param>
    <param-name>db.user</param-name>
    <param-value>ваш_пользователь</param-value>
</context-param>
<context-param>
    <param-name>db.password</param-name>
    <param-value>ваш_пароль</param-value>
</context-param>
```

## Шаг 3: Сборка проекта

Выполните в корне проекта:
```bash
mvn clean package
```

WAR файл будет создан в папке `target/InsuranceCompany.war`

## Шаг 4: Развертывание на Tomcat

### Вариант 1: Автоматическое развертывание
1. Скопируйте `target/InsuranceCompany.war` в папку `webapps` Tomcat
2. Tomcat автоматически развернет приложение

### Вариант 2: Через Manager App
1. Откройте `http://localhost:8080/manager/html`
2. Войдите с учетными данными администратора Tomcat
3. В разделе "Deploy" выберите WAR файл и загрузите

### Вариант 3: Через IntelliJ IDEA
1. Run → Edit Configurations
2. Добавьте конфигурацию Tomcat Server
3. Укажите путь к Tomcat 10
4. В Deployment добавьте артефакт `InsuranceCompany:war`
5. Запустите конфигурацию

## Шаг 5: Проверка

Откройте в браузере:
```
http://localhost:8080/InsuranceCompany/
```

Должен произойти редирект на страницу входа.

## Структура URL

- `/login` - страница входа
- `/register` - регистрация
- `/user/home` - личный кабинет пользователя
- `/user/vehicles` - управление автомобилями
- `/user/policies` - управление полисами
- `/user/insurances` - страховые случаи
- `/admin/home` - панель администратора
- `/admin/tariffs` - управление тарифами
- `/admin/insurances` - управление страховыми случаями
- `/admin/payouts` - управление выплатами

## Устранение проблем

### Ошибка подключения к БД
- Проверьте, что MySQL запущен
- Проверьте параметры подключения в web.xml
- Убедитесь, что база данных создана

### Ошибка 404
- Проверьте, что приложение развернуто
- Проверьте правильность URL
- Проверьте логи Tomcat в `logs/catalina.out`

### Ошибка компиляции
- Убедитесь, что используется Java 17
- Выполните `mvn clean install`
- Проверьте, что все зависимости загружены

## Логи

Логи Tomcat находятся в:
- `logs/catalina.out` - общие логи
- `logs/localhost.YYYY-MM-DD.log` - логи приложения

## Остановка приложения

1. Через Manager App: выберите приложение и нажмите "Undeploy"
2. Вручную: удалите папку `webapps/InsuranceCompany` и файл `webapps/InsuranceCompany.war`

