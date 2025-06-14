# Getting Started

This project simulate the usage of PostgreSQL and Reactive Repository. 
When start the application some registry will be saved in the Table.

### Running
- DB can be created using docker:
``` shell
docker run \
    --name myPostgresDb \
    -p 5455:5432 \
    -e POSTGRES_USER=postgresUser \
    -e POSTGRES_PASSWORD=postgresPW \
    -e POSTGRES_DB=postgresDB \
    -d postgres
```
- Create table as R2dbc are not creating automatically
Using Liquibase for creation of tables and inital insert
 - [structure](src/main/resources/db/initial-structure.xml)
 - [initial data](src/main/resources/db/initial-data.xml)
 - Connection of springboot to db configured in the application.properties
