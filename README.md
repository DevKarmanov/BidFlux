# Bidflux Backend

Backend для платформы аукционов **Bidflux**, реализованный на микросервисной архитектуре.

---

## Содержание

1. [Структура микросервисов](#структура-микросервисов)
2. [Настройка и запуск](#настройка-и-запуск)
3. [Логика аукционов](#логика-аукционов)
4. [Логика очистки пользователей](#логика-очистки-пользователей)
5. [Юридические и финансовые ограничения](#юридические-и-финансовые-ограничения)

---

## Структура микросервисов

| Микросервис           | Порт | Функционал                                               |
| --------------------- | ---- | -------------------------------------------------------- |
| **auction-service**   | 8080 | Управление аукционами                                    |
| **auth-service**      | 8083 | Управление пользователями, авторизация, выдача JWT       |
| **bid-service**       | 8081 | Работа со ставками                                       |
| **complaint-service** | 8084 | Управление жалобами                                      |
| **gateway**           | 8086 | Единая точка входа, проксирует запросы на нужные сервисы |

> Все запросы можно направлять через **gateway** на порт `8086`.

Документация Swagger доступна по адресу:

```bash
http://localhost:<порт-микросервиса>/swagger-ui/index.html
```

Каждый микросервис также предоставляет **health check**:

```bash
http://localhost:<порт-микросервиса>/actuator/health
```

---

## Настройка и запуск

### 1. Создание сертификатов для gRPC

**auth-service**

```bash
openssl genrsa -out auth-service/src/main/resources/certs/grpc/server-auth-key.pem 2048
openssl req -new -x509 -key auth-service/src/main/resources/certs/grpc/server-auth-key.pem -out auth-service/src/main/resources/certs/grpc/server-auth-cert.pem -days 365
```

**auction-service**

```bash
openssl genrsa -out auction-service/src/main/resources/certs/grpc/server-auction-key.pem 2048
openssl req -new -x509 -key auction-service/src/main/resources/certs/grpc/server-auction-key.pem -out auction-service/src/main/resources/certs/grpc/server-auction-cert.pem -days 365
```

Копирование сертификатов между сервисами:

```bash
cp bid-service/src/main/resources/certs/grpc/server-bid-cert.pem auction-service/src/main/resources/certs/grpc/
cp auth-service/src/main/resources/certs/grpc/server-auth-cert.pem auction-service/src/main/resources/certs/grpc/
cp auction-service/src/main/resources/certs/grpc/server-auction-cert.pem bid-service/src/main/resources/certs/grpc/
cp auth-service/src/main/resources/certs/grpc/server-auth-cert.pem bid-service/src/main/resources/certs/grpc/
```

**bid-service**

```bash
openssl genrsa -out bid-service/src/main/resources/certs/grpc/server-bid-key.pem 2048
openssl req -new -x509 -key bid-service/src/main/resources/certs/grpc/server-bid-key.pem -out bid-service/src/main/resources/certs/grpc/server-bid-cert.pem -days 365
```

---

### 2. Создание ключей для JWT

```bash
openssl genpkey -algorithm RSA -out auth-service/src/main/resources/certs/jwt/private_jwt_key.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in auth-service/src/main/resources/certs/jwt/private_jwt_key.pem -out auth-service/src/main/resources/certs/jwt/public_jwt_key.pem
```

---

### 3. Создание JWKS

* Генерация `jwks.json` с помощью [pem2jwk](https://pem2jwk.vercel.app) или аналогичного инструмента.
* Разместить результат:

```
auth-service/src/main/resources/certs/jwt/jwks.json
```

---

### 4. Подготовка файлов

* Переименовать все файлы *-example.*, убрав -example, чтобы сделать их рабочими.

---

### 5. Настройка `application.yaml`

* Указать пути к сертификатам и ключам.

---

### 6. Запуск базы данных

```bash
docker-compose up -d
```

---

### 7. Генерация классов из `.proto` файлов

```bash
./gradlew generateProto
```

---

### 8. Запуск микросервисов

1. **auth-service**
2. **auction-service**
3. Остальные сервисы в любом порядке: `bid-service`, `complaint-service`, `gateway`.

---

## Логика аукционов

| Статус       | Условие                                                 |
|--------------|---------------------------------------------------------|
| **ACTIVE**   | Аукцион не завершился                                   |
| **INACTIVE** | Нет ставок или все ставки были сделаны владельцем       |
| **FINISHED** | Аукцион завершился, есть ставки от других пользователей. При этом устанавливаются winnerId и finalAmount |   

**Очистка аукционов**:

* Удаляются `INACTIVE` аукционы.
* Удаляются `ACTIVE` аукционы, если аккаунт владельца удалён (полностью или частично).

**Архивирование**:

* Только `FINISHED` аукционы.
* Копируются поля:
  `id`, `title`, `description`, `startPrice`, `bidIncrement`, `reservePrice`, `isPrivate`, `status`, `startDate`, `endDate`, `ownerId`, `currency`, `winnerId`, `finalAmount`.
* После архивации исходный аукцион удаляется.

---

## Логика очистки пользователей

| Тип удаления       | Условие                                                                                  | Действие                                     |
| ------------------ | ---------------------------------------------------------------------------------------- | -------------------------------------------- |
| Полное удаление    | Не был онлайн >1 года, нет активных аукционов, не владел архивами, не выигрывал аукционы | Физическое удаление через `deleteUsers()`    |
| Частичное удаление | Забанен >6 месяцев                                                                       | Пометка `deleted = true`, данные сохраняются |

* После обеих операций удаляются **токены** и **доступы к аукционам**.

---

## Юридические и финансовые ограничения

Согласно принципам юридической и финансовой отчётности, данные о ставках и выигранных аукционах:

* **являются частью сделки** и могут использоваться в налоговой, судебной или иной проверке;
* подлежат хранению в соответствии с требованиями законодательства (в ряде юрисдикций — до нескольких лет);
* затрагивают интересы других участников торгов.

Поэтому при удалении таких аккаунтов применяется **частичное удаление** — пользовательская часть становится недоступной, а информация, необходимая для правовой прозрачности, остаётся в системе.

---

## Информация о частично удалённом аккаунте

* Аккаунт **не отображается** в поиске и ленте.
* Никнейм освобождается.
* Исторические данные доступны **только администраторам в БД**.
* Для других пользователей аккаунт полностью невидим, если он не пересекался с ними в торгах.

---

## Условия полного удаления аккаунта

Аккаунт может быть полностью удалён **только если**:

* Не побеждал в аукционах.
* Не имеет архивных аукционов.
* Не создавал аукционы или все созданные находятся в статусе `INACTIVE`.

В противном случае применяется **частичное удаление**.

