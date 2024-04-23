##### Server to calculate the sequence of Fibo.

```shell
###
# Simple Get to return "Server is running"
GET http://localhost:8090/api

###
# Post a sequence of values to calculate using async call the sequence of Fibonacci
POST http://localhost:8090/api/calculate-fibos
accept: */*
Content-Type: application/json

{
  "numbers": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 133]
}
```