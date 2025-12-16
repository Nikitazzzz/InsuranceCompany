# Система автострахования (Insurance Company)

Веб-приложение для управления системой автострахования с поддержкой ОСАГО и КАСКО.

## Технологии

- **Java** (JDK 23)
- **Jakarta Servlet API** 6.1.0
- **MySQL** 8.0+
- **Maven** для управления зависимостями
- **JSP** для представлений
- **JSTL** для работы с JSP

## Структура проекта

```
InsuranceCompany/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/insurancecompany/
│   │   │       ├── config/          # Конфигурация (DBConnectionManager, AppContextListener)
│   │   │       ├── dao/             # Интерфейсы DAO
│   │   │       │   └── impl/        # Реализации DAO (JDBC)
│   │   │       ├── model/           # Модели данных (UserAccount, Owner, Vehicle, Policy, etc.)
│   │   │       ├── service/         # Бизнес-логика
│   │   │       └── servlet/         # Сервлеты
│   │   │           ├── auth/        # Авторизация (Login, Register, Logout)
│   │   │           ├── UserTasks/   # Функционал пользователя
│   │   │           └── AdminTasks/   # Функционал администратора
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   └── web.xml          # Конфигурация веб-приложения
│   │       ├── jsp/                 # JSP страницы
│   │       └── static/              # CSS, JS
│   └── test/
├── database_schema_mysql.sql         # SQL скрипт для создания БД
└── pom.xml                          # Maven конфигурация
```

## Настройка базы данных

### 1. Установка MySQL

Убедитесь, что MySQL установлен и запущен на вашем компьютере.

### 2. Создание базы данных

Выполните SQL скрипт для создания базы данных и всех необходимых таблиц:

```bash
mysql -u root -p < database_schema_mysql.sql
```

Или вручную в MySQL клиенте:

```sql
source database_schema_mysql.sql
```

### 3. Настройка подключения

Отредактируйте файл `src/main/webapp/WEB-INF/web.xml` и измените параметры подключения к БД:

```xml
<context-param>
    <param-name>db.url</param-name>
    <param-value>jdbc:mysql://localhost:3306/InsuranceCompany?useSSL=false&amp;serverTimezone=UTC&amp;characterEncoding=UTF-8</param-value>
</context-param>
<context-param>
    <param-name>db.user</param-name>
    <param-value>root</param-value>
</context-param>
<context-param>
    <param-name>db.password</param-name>
    <param-value>ваш_пароль</param-value>
</context-param>
```

## Сборка проекта

### С использованием Maven:

```bash
mvn clean package
```

WAR файл будет создан в папке `target/InsuranceCompany-1.0-SNAPSHOT.war`

## Развертывание

### Локальное развертывание (Tomcat)

1. Установите Apache Tomcat 10.x или выше
2. Скопируйте `target/InsuranceCompany-1.0-SNAPSHOT.war` в папку `webapps` Tomcat
3. Запустите Tomcat
4. Откройте браузер: `http://localhost:8080/InsuranceCompany-1.0-SNAPSHOT/login`

### Развертывание в общий доступ

#### Вариант 1: VPS с Tomcat

1. Установите Java, MySQL и Tomcat на VPS сервере
2. Настройте MySQL для удаленного доступа (если нужно)
3. Загрузите WAR файл на сервер
4. Настройте домен и SSL сертификат (Let's Encrypt)
5. Настройте firewall для портов 80/443

#### Вариант 2: Облачные платформы

- **Heroku**: Используйте Heroku Maven plugin
- **AWS Elastic Beanstalk**: Загрузите WAR файл через консоль
- **Google Cloud Platform**: Используйте App Engine или Compute Engine
- **Azure**: Используйте Azure App Service

## Основные функции

### Для пользователей (OWNER):
- Регистрация и авторизация
- Управление транспортными средствами
- Оформление полисов (ОСАГО, КАСКО)
- Подача заявок на страховые случаи
- Просмотр истории полисов и заявок

### Для администраторов (ADMIN):
- Управление тарифами
- Обработка страховых случаев
- Управление выплатами
- Управление пользователями

## Структура базы данных

Основные таблицы:
- `Users` - пользователи системы
- `Owners` - владельцы транспортных средств
- `Vehicle` - транспортные средства
- `Policies` - полисы страхования
- `Tariffs` - тарифы
- `Insurances` - страховые случаи
- `Payouts` - выплаты
- `Statuses` - статусы
- `Administrator` - администраторы

## Разработка

### Запуск в режиме разработки

1. Убедитесь, что MySQL запущен
2. Создайте базу данных (см. выше)
3. Настройте подключение в `web.xml`
4. Запустите проект через IDE (IntelliJ IDEA) или Maven:

```bash
mvn clean compile
```

5. Используйте встроенный сервер приложений или разверните на Tomcat

## Примечания

- Пароли в текущей версии хранятся в открытом виде. Рекомендуется добавить хеширование (BCrypt)
- Для продакшена рекомендуется использовать пул соединений (HikariCP)
- Добавьте валидацию данных на стороне клиента и сервера
- Настройте логирование (Log4j2 или SLF4J)

## Лицензия

Этот проект создан в образовательных целях.




