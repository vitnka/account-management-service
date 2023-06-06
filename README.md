# account-management-service

# Getting Started


Запустить сервис можно с помощью команды `docker compose-up`
Выполнить скрипт из папки `resources`


## Examples

Некоторые примеры запросов
- [Пополнение счёта](#accounts-deposit)



### Пополнение счёта <a name="accounts-deposit"></a>

Пополнение счёта пользователя на определённую сумму:
```curl
curl --location --request POST 'http://localhost:8080/api/v1/account/deposit' \
--header X-Request-Id: 3b6acfd4-a548-4e32-a8d6-da55b4b968fb \
--header 'Content-Type: application/json' \
--data-raw '{
  "amount": "123",
  "accountId": "1"
}'
```
Пример ответа:
```json
{
  "message": "success"
}
```


### Получение истории операций пользователя <a name="accounts-history"></a>

Пополнение счёта пользователя на определённую сумму:
```curl
curl --location --request POST 'http://localhost:8080/api/v1/account/history' \
--header X-Request-Id: 3b6acfd4-a548-4e32-a8d6-da55b4b968fb \
--header 'Content-Type: application/json' \
--data-raw '{
  "accountId": "1"
}'
```
Пример ответа:
```json
{
  "operations": [
    {
      "amount": 10,
      "operation": "refund",
      "time": "2022-10-24T11:06:06.896409Z",
      "order": 15
    },
    {
      "amount": 10,
      "operation": "reservation",
      "time": "2022-10-24T11:06:02.431726Z",
      "order": 15
    }
  ]
}
```

### Резервирование средств <a name="reservations-create"></a>

Резервирование средств по указанной услуге и номеру заказа:
```curl
curl --location --request POST 'http://localhost:8080/api/v1/reservations/create' \
--header X-Request-Id: 3b6acfd4-a548-4e32-a8d6-da55b4b968fb \
--header 'Content-Type: application/json' \
--data-raw '{
    "account_id": 1,
    "product_id": 1,
    "order_id": 15,
    "amount": 10
}'
```
Пример ответа, с указанием id резервирования:
```json
{
  "id": 1
}
```


### Признание выручки <a name="reservations-revenue"></a>

Признание выручки по указанному резервированию:
```curl
curl --location --request POST 'http://localhost:8080/api/v1/reservations/revenue' \
--header X-Request-Id: 3b6acfd4-a548-4e32-a8d6-da55b4b968fb \
--header 'Content-Type: application/json' \
--data-raw '{
    "account_id": 1,
    "product_id": 1,
    "order_id": 15,
    "amount": 10
}'
```
Пример ответа:
```json
{
  "message": "success"
}
```