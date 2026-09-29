# Parking System

Backend-система парковки: учёт автомобилей и парковочных мест, въезд/выезд, автоматический расчёт стоимости.

**Стек:** Java 17, Spring Boot 3.3, Spring Data JPA, PostgreSQL, REST API, Swagger/OpenAPI (springdoc), Maven.

## Возможности

- **Vehicle** — CRUD автомобилей (CAR, MOTORCYCLE, TRUCK)
- **Parking Spot** — CRUD парковочных мест + список свободных мест (`/free`, с фильтром по типу)
- **Parking Session** — въезд (проверка авто, места, совместимости типов, что место свободно), выезд (расчёт стоимости, освобождение места), история сессий

### Тарифы (сом/час)

| Тип | Цена |
|---|---|
| CAR | 100 |
| MOTORCYCLE | 50 |
| TRUCK | 150 |

Правило расчёта: каждый начатый час оплачивается полностью, минимум 1 час.
Стоимость = тариф × количество часов. Логика — в `PricingService`.

## Требования

- JDK 17+
- Maven 3.9+ (или встроенный Maven в IntelliJ IDEA)
- PostgreSQL 14+ (локально или через Docker)

## Подключение PostgreSQL

### Вариант 1. Docker (рекомендуется)

```bash
docker compose up -d
```

Поднимется PostgreSQL с базой `parking_db`, пользователь `postgres`, пароль `postgres`, порт `5432`.

### Вариант 2. Локальный PostgreSQL

Создайте базу данных:

```sql
CREATE DATABASE parking_db;
```

Таблицы создаются автоматически при первом запуске (`spring.jpa.hibernate.ddl-auto=update`).

## Настройки / переменные окружения

Значения по умолчанию заданы в `src/main/resources/application.properties`. Их можно переопределить переменными окружения:

| Переменная | По умолчанию | Описание |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/parking_db` | JDBC-адрес БД |
| `DB_USERNAME` | `postgres` | Пользователь БД |
| `DB_PASSWORD` | `postgres` | Пароль БД |
| `SERVER_PORT` | `8080` | Порт приложения |

## Запуск

```bash
mvn spring-boot:run
```

или

```bash
mvn clean package
java -jar target/parking-system-1.0.0.jar
```

## Swagger

После запуска откройте: **http://localhost:8080/swagger-ui.html**

OpenAPI JSON: http://localhost:8080/v3/api-docs

## Основные эндпоинты

| Метод | URL | Описание |
|---|---|---|
| POST | `/api/vehicles` | Создать автомобиль |
| GET | `/api/vehicles` | Список |
| GET | `/api/vehicles/{id}` | По ID |
| PUT | `/api/vehicles/{id}` | Изменить |
| DELETE | `/api/vehicles/{id}` | Удалить |
| POST | `/api/parking-spots` | Создать место |
| GET | `/api/parking-spots` | Список мест |
| GET | `/api/parking-spots/free?type=CAR` | Свободные места |
| GET | `/api/parking-spots/{id}` | По ID |
| PUT | `/api/parking-spots/{id}` | Изменить |
| DELETE | `/api/parking-spots/{id}` | Удалить |
| POST | `/api/parking-sessions/entry` | Въезд |
| POST | `/api/parking-sessions/{id}/exit` | Выезд + расчёт стоимости |
| GET | `/api/parking-sessions?status=ACTIVE` | Список сессий |
| GET | `/api/parking-sessions/{id}` | Сессия по ID |

## HTTP-статусы и ошибки

| Код | Когда |
|---|---|
| 200 / 201 / 204 | Успех / создано / удалено |
| 400 | Ошибка валидации или некорректный JSON/параметр |
| 404 | Сущность не найдена |
| 409 | Конфликт: дубликат, место занято, авто уже на парковке, сессия уже завершена, удаление связанных данных |
| 422 | Тип автомобиля не совместим с типом места |
| 500 | Внутренняя ошибка |

Формат ошибки:

```json
{
  "timestamp": "2026-09-29T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Ошибка валидации",
  "path": "/api/vehicles",
  "validationErrors": { "licensePlate": "Госномер обязателен" }
}
```

## Тесты

```bash
mvn test
```

## Структура проекта

```
controller/   REST-контроллеры (только HTTP)
service/      бизнес-логика (+ PricingService — расчёт стоимости)
repository/   Spring Data JPA репозитории
entity/       JPA-сущности
dto/          запросы/ответы API + валидация
exception/    свои исключения и GlobalExceptionHandler
enums/        VehicleType (с тарифом), SessionStatus
config/       конфигурация OpenAPI
```
