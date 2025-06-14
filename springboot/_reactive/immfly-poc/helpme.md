## Api to manage user purchase

### APP definition
 - Rest API with Spring boot + PostgreSQL DB and Java 21
 - API first approach to development
 - User of Webflux to provide reactive programming (For such simple API maybe it's an over engineering)
 - Liquibase for data base integrity
 - Docker compose to quickly provide DB and run application


### API definition
- basic entity definition
  - Products definition:
    - Unique id;
    - name;
    - price;
    - image; (one improvement is remove this image form db and add to some image hosting/file storage and provide only the link)
    - category;
  - Category definition:
    - Unique id;
    - name;
    - parent category;
  - Order definition:
    - list of product
    - payment details (total price, card token, status[paid/declined/opened],dateTime, paymentGateway)
    - order total
    - status [Opened/Dropped/Finished]
    - user
    - seat letter
    - seat number
  - ~~User
    - email
    - password~~
- Added Liquibase to manage db creation and base data insertion
- Provide the products (GET) and create orders (POST)
  - ~~_if has time_ add user authentication API~~  
  - For product provide one API to get all products categorized by category
    - GET /api/v1/products
    
  - For order provide one endpoint to create an order, one to update an existing order and one to purchase 
    - POST /api/v1/orders
    - PATCH /api/v1/orders
    - POST /api/v1/orders/{id}/purchase
  - ~~Additional points _if has time_:
    - CRUD for category
    - CRUD for product
    - Integration tests~~

### basic business rules:
- ☑️ An Order can be opened without any products associated with it, but needs a seat position.
- ☑️ If customer cancels the request, the order should mark as canceled (DROPPED)
- ☑️ When updating (adding a buyer email, products) an Order, we need to check if the product has enough stock and recalculate the price
- ☑️ When Finishing an order, a payment status should be passed Paid, PaymentFailed, OfflinePayment status, a card token, the payment gateway used
