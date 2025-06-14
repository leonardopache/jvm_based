
## How to Run
1. Running local:
   
    1.1 **Start the Database**:
    - Use Docker to start a PostgreSQL database:
        ```shell
        docker run \
            --name myPostgresDb \
            -p 5455:5432 \
            -e POSTGRES_USER=postgresUser \
            -e POSTGRES_PASSWORD=postgresPW \
            -e POSTGRES_DB=postgresDB \
            -d postgres
        ```
    - Ensure the database is running on `localhost:5455`.
 
    1.2 **Create Tables and Insert Initial Data**:
    - Liquibase is used to manage database schema and initial data.
    - The structure and data files are located in:
        - Initial Structure
        - Initial Data
    - Spring Boot will automatically apply these changes when the application starts.

    1.3 **Run the Application**:
    - Use Maven wrapper to start the application:
        ```shell
        ./mvnw spring-boot:run
        ```
2. **Start with docker-compose**
   1. Start DB and run APP:
        ```
        docker-compose up
        ```
    2. For this case the db configuration is:
        - database running in `myPostgresDb:5455`
        - app runnign `localhost:8080`
   
## API Documentation

### Endpoints [swagger-ui](http://localhost:8080/webjars/swagger-ui/index.html)
- **Products**:
  - `GET /api/v1/products`: Retrieve all products categorized by category.
- **Orders**:
  - `POST /api/v1/orders`: Create a new order.
  - `PUT /api/v1/orders`: Update an existing order. (Status need to be OPENED)
  - `POST /api/v1/orders/{id}/purchase`: Purchase an order. (Status need to be OPENED)
  - `GET /api/v1/orders`: Retrieve all orders.
  - `DELETE /api/v1/orders/{id}`: Delete a order (Status need to be OPENED)

## Business Rules
- An order can be created without products but requires a seat position.
- Orders can be updated to add buyer email, products, and recalculate the price based on stock.
- Orders can be marked as canceled or finished with payment details.

## Examples
### Create Order
```http
POST http://localhost:8080/api/v1/orders
Content-Type: application/json

{
  "products": [
    {
      "productId": "a80d205f-c430-4f21-9344-e3be35316e9e",
      "quantity": 2,
      "price": 1
    }
  ],
  "userId": "dca582ad-1888-44b0-a868-38d85c533952",
  "seatNumber": "12",
  "seatLetter": "A"
}
```
### Purchase Order
```http
POST http://localhost:8080/api/v1/orders/{orderId}/purchase
Content-Type: application/json

{
  "products": [
    {
      "productId": "3fb9d15a-79ce-43ce-8cc0-be582ac84cb4",
      "quantity": 1,
      "price": 0.5
    }
  ],
  "userId": "465d2d38-50ab-4a1e-9674-71667cfb68b9",
  "seatNumber": "12",
  "seatLetter": "A"
}
```
### Update Order
```http
PUT http://localhost:8080/api/v1/orders/{orderId}
Content-Type: application/json

{
  "products": [
    {
      "productId": "a80d205f-c430-4f21-9344-e3be35316e9e",
      "quantity": 2,
      "price": 1
    }
  ],
  "userId": "dca582ad-1888-44b0-a868-38d85c533952",
  "seatNumber": "12",
  "seatLetter": "A"
}
```
### Delete Order
```http
DELETE http://localhost:8080/api/v1/orders/80db5eaf-5034-4584-aa96-17ecc4fc3225
```

### Get All Products
```http
GET http://localhost:8080/api/v1/products
```

### Get All Orders
```http
GET http://localhost:8080/api/v1/orders
```