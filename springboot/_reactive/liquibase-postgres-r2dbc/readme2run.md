# Getting Started

This project simulate the usage of PostgreSQL and Reactive Repository. 
When start the application some registry will be saved in the Table.

### Running
- DB can be created using docker:
``` shell
docker run \
    --name liquibase-postgres \
    -p 5456:5432 \
    -e POSTGRES_USER=postgresUser \
    -e POSTGRES_PASSWORD=postgresPW \
    -e POSTGRES_DB=liquibase-Db \
    -d postgres
```
- Create table as R2dbc are not creating automatically. (/src/main/resources/schema.sql)
```
create table if not exists public.samples(id UUID NOT NULL DEFAULT gen_random_uuid () primary key, name VARCHAR(255), status VARCHAR(255));

```
- Connection of springboot to db configured in the application.properties
```
spring.r2dbc.url=r2dbc:postgresql://postgresUser:postgresPW@localhost:5456/liquibase-Db
```





