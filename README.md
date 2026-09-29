# Parking System

Backend-система парковки. Позволяет вести учёт автомобилей и парковочных мест, регистрировать въезд и выезд, автоматически считать стоимость парковки.

## Стек

- Java 17+
- Spring Boot 3.3
- Spring Data JPA (Hibernate)
- PostgreSQL 16
- REST API
- Swagger / OpenAPI (springdoc)
- Maven
- Docker Compose (для запуска БД)

## Функциональность

### Vehicle (автомобиль)

- создание, список, получение по ID, изменение, удаление
- типы: `CAR`, `MOTORCYCLE`, `TRUCK`

### Parking Spot (парковочное место)

- создание, список, получение по ID, изменение, удаление
- получение свободных мест (с необязательным фильтром по типу)

### Parking Session (парковочная сессия)

- въезд: проверка автомобиля и места, совместимости типов, что место свободно
- выезд: расчёт стоимости, освобождение места
- история сессий с фильтром по статусу

### Тарифы

- CAR: 100 сом/час
- MOTORCYCLE: 50 сом/час
- TRUCK: 150 сом/час

Каждый начатый час оплачивается полностью, минимальная оплата 1 час. Расчёт выполняется в backend (`PricingService`).

Тип автомобиля должен совпадать с типом парковочного места.

---

## Как запустить проект

### 1. Что должно быть установлено

- JDK 17 или новее
- Maven 3.9+ (или IntelliJ IDEA со встроенным Maven)
- Docker (для PostgreSQL) или установленный PostgreSQL

### 2. Скачать проект

```
git clone https://github.com/NERNER52/parking-system.git
cd parking-system
```

### 3. Подключить PostgreSQL

**Вариант A. Через Docker (рекомендуется)**

```
docker compose up -d
```

Будет запущен контейнер `parking-postgres` со следующими параметрами:

- база данных: `parking_db`
- пользователь: `postgres`
- пароль: `postgres`
- порт на вашем компьютере: `5440`

**Вариант B. Свой PostgreSQL**

Создайте базу данных:

```sql
CREATE DATABASE parking_db;
```

Затем задайте переменные окружения с вашими настройками (см. раздел ниже). Например, для PostgreSQL на стандартном порту 5432 в Windows PowerShell:

```
$env:DB_URL="jdbc:postgresql://localhost:5432/parking_db"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="ваш_пароль"
```

Таблицы создаются автоматически при первом запуске приложения.

### 4. Запустить приложение

Через Maven:

```
mvn spring-boot:run
```

Или из IntelliJ IDEA: открыть класс `ParkingApplication` и нажать Run.

Приложение стартует на порту `8080`. В консоли должна появиться строка `Started ParkingApplication`.

---

## Настройки и переменные окружения

Настройки находятся в файле `src/main/resources/application.properties`. Их можно переопределить переменными окружения.

- `DB_URL` — адрес базы данных. По умолчанию: `jdbc:postgresql://localhost:5440/parking_db`
- `DB_USERNAME` — пользователь БД. По умолчанию: `postgres`
- `DB_PASSWORD` — пароль БД. По умолчанию: `postgres`
- `SERVER_PORT` — порт приложения. По умолчанию: `8080`

---

## Swagger

После запуска приложения Swagger UI доступен по адресу:

**http://localhost:8080/swagger-ui.html**

OpenAPI-описание в формате JSON: http://localhost:8080/v3/api-docs

---

## API

### Vehicles

- `POST /api/vehicles` — создать автомобиль
- `GET /api/vehicles` — список автомобилей
- `GET /api/vehicles/{id}` — получить по ID
- `PUT /api/vehicles/{id}` — изменить
- `DELETE /api/vehicles/{id}` — удалить

### Parking Spots

- `POST /api/parking-spots` — создать место
- `GET /api/parking-spots` — список мест
- `GET /api/parking-spots/free` — свободные места (параметр `type` необязательный)
- `GET /api/parking-spots/{id}` — получить по ID
- `PUT /api/parking-spots/{id}` — изменить
- `DELETE /api/parking-spots/{id}` — удалить

### Parking Sessions

- `POST /api/parking-sessions/entry` — въезд автомобиля
- `POST /api/parking-sessions/{id}/exit` — выезд, расчёт стоимости, освобождение места
- `GET /api/parking-sessions` — список сессий (параметр `status`: `ACTIVE` или `COMPLETED`)
- `GET /api/parking-sessions/{id}` — получить по ID

### Примеры запросов

Создать автомобиль:

```json
{
  "licensePlate": "01KG123ABC",
  "type": "CAR",
  "ownerName": "Алексей"
}
```

Создать парковочное место:

```json
{
  "number": "A-01",
  "type": "CAR"
}
```

Въезд автомобиля:

```json
{
  "vehicleId": 1,
  "spotId": 1
}
```

---

## HTTP-статусы и ошибки

- `200 OK` — успешный запрос
- `201 Created` — объект создан
- `204 No Content` — объект удалён
- `400 Bad Request` — ошибка валидации или некорректный JSON
- `404 Not Found` — объект не найден
- `409 Conflict` — конфликт: дубликат, место занято, автомобиль уже на парковке, сессия уже завершена, удаление связанных данных
- `422 Unprocessable Entity` — тип автомобиля не совместим с типом места
- `500 Internal Server Error` — внутренняя ошибка сервера

Все ошибки возвращаются в едином формате:

```json
{
  "timestamp": "2026-09-29T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Ошибка валидации",
  "path": "/api/vehicles",
  "validationErrors": {
    "licensePlate": "Госномер обязателен"
  }
}
```

---

## Тесты

```
mvn test
```

## Структура проекта

- `controller` — REST-контроллеры
- `service` — бизнес-логика, в том числе `PricingService` (расчёт стоимости)
- `repository` — репозитории Spring Data JPA
- `entity` — JPA-сущности
- `dto` — объекты запросов и ответов, валидация
- `exception` — собственные исключения и глобальный обработчик ошибок
- `enums` — типы автомобилей (с тарифами) и статусы сессий
- `config` — конфигурация OpenAPI
