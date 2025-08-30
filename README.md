# Bidflux Backend

Backend для платформы аукционов **Bidflux**, реализованный на микросервисной архитектуре.

## Структура микросервисов

| Микросервис         | Порт | Функционал                                               |
| ------------------- | ---- | -------------------------------------------------------- |
| **auction-service** | 8080 | Управление аукционами                                    |
| **auth-service**    | 8083 | Управление пользователями, авторизация, выдача JWT       |
| **bid-service**     | 8081 | Работа со ставками                                       |
| **gateway**         | 8086 | Единая точка входа, проксирует запросы на нужные сервисы |

> **Важно:** все запросы могут идти через **gateway** на порт `8086`

Документация Swagger доступна по адресу:

```bash 
http://localhost:<порт-микросервиса>/swagger-ui/index.html
```

Каждый микросервис также предоставляет health check:

```bash 
http://localhost:<порт-микросервиса>/actuator/health
````

Это позволяет проверить состояние сервиса.

---

## Настройка и запуск

### 1. Создание сертификатов для gRPC

**auth-service**

```bash
# Создаем приватный ключ
openssl genrsa -out auth-service/src/main/resources/certs/grpc/server-auth-key.pem 2048

# Создаем самоподписанный сертификат
openssl req -new -x509 -key auth-service/src/main/resources/certs/grpc/server-auth-key.pem -out auth-service/src/main/resources/certs/grpc/server-auth-cert.pem -days 365
```

**auction-service**

```bash
# Приватный ключ
openssl genrsa -out auction-service/src/main/resources/certs/grpc/server-auction-key.pem 2048

# Самоподписанный сертификат
openssl req -new -x509 -key auction-service/src/main/resources/certs/grpc/server-auction-key.pem -out auction-service/src/main/resources/certs/grpc/server-auction-cert.pem -days 365
```

**bid-service**

Копируем сертификат из `auction-service`:

```bash
cp auction-service/src/main/resources/certs/grpc/server-auction-cert.pem bid-service/src/main/resources/certs/grpc/
```

---

### 2. Создание ключей для JWT

```bash
# Приватный ключ
openssl genpkey -algorithm RSA -out auth-service/src/main/resources/certs/jwt/private_jwt_key.pem -pkeyopt rsa_keygen_bits:2048

# Публичный ключ
openssl rsa -pubout -in auth-service/src/main/resources/certs/jwt/private_jwt_key.pem -out auth-service/src/main/resources/certs/jwt/public_jwt_key.pem
```

### 3. Создание JWKS

Для генерации `jwks.json` можно использовать [pem2jwk](https://pem2jwk.vercel.app) или любой аналогичный инструмент.
Разместить результат в:

```
auth-service/src/main/resources/certs/jwt/jwks.json
```

---

### 4. Подготовка файлов

* Удалить все сертификаты и json-файлы с припиской `*-example.*` (они нужны, только чтобы показать структуру).
* Переименовать остальные файлы, чтобы убрать `-example`.

---

### 5. Настройка `application.yaml`

* Указать пути к сертификатам и ключам.
* Ввести данные для подключения к БД (из `init.sql`).

---

### 6. Запуск базы данных

Запустить базу данных из `docker-compose.yaml`:

---

### 7. Запуск микросервисов

1. Сначала **auth-service**.
2. Затем остальные сервисы в любом порядке:

    * auction-service
    * bid-service
    * gateway

