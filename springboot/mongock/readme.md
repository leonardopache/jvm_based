##### #mongock

![mongock-img](https://docs.mongock.io/images/mongock-logo-with-title.jpg)

#### _Knowledge sharing_
From [Mongock page](https://docs.mongock.io/): "Is a Java based migration tool. It allows developers to execute safer migrations during the Application deployment process as code and data changes are shipped together. It was originally designed for MongoDB data migrations and is evolving to provide a wider suite of database compatibility."
As the creator description a couple of points to take in consideration:
- It is a migration tool, it's more to migrate and manage on runtime;
- Was designed for MongoDB, so has higher compatibility with NoSQL db;
- Execute the migration during the deployment, for my experience it could during the application startup;
- It is Java based;
  When using this tool I really felt like this tool is for implement migration data from A to B and not manage the DB changes, what is complete acceptable as it's for NoSQL and the db structure is complete different then SQL, document aren't tables. It's complete fine to use only for seed some relevant data. Als it has a great support working with Spring. The negative point for mongoDB reactive the configuration is a little more complex.

![tech-overview](https://docs.mongock.io/images/technical-overview-diagram-User%20HLD.jpg)

#### PoC SpringBoot + MongoDB (no reactive)
Springboot with `spring-boot-starter-data-mongodb` dependency.
##### Configuration:
_Add the library to the pom.xml_
```xml
<dependency>  
   <groupId>io.mongock</groupId>  
   <artifactId>mongock-bom</artifactId>  
   <version>5.2.4</version>  
   <type>pom</type>  
</dependency>  
<dependency>  
   <groupId>io.mongock</groupId>  
   <artifactId>mongock-springboot-v3</artifactId>  
   <version>5.2.4</version>  
</dependency>  
<!-- MONGODB DRIVER -->  
<dependency>  
   <groupId>io.mongock</groupId>  
   <artifactId>mongodb-springdata-v4-driver</artifactId>  
   <version>5.2.4</version>  
</dependency>
```

_Add the property into application.properties_
```
mongock.migration-scan-package=com.example.mongodb.mongock.migration
```

_Application implementation_
With Spring we can use annotations to access the mongock and define the implementation
- Add the annotation `@EnableMongock` in the main class
- in the package defined on application.properties `mongock.migration-scan-package`  add the execution class, responsible for implement the db migration
```java
/* Execution class sample to add a new registry into db. */
@Log4j2  
@ChangeUnit(id="customer-initializer", order = "001", author = "myself")  
public class CustomerInitialization {  
    @BeforeExecution  
    public void beforeExecution(MongoTemplate mongoTemplate) {  
        log.info("######### BeforeExecution!!!");  
    }  
    @RollbackBeforeExecution  
    public void rollbackBeforeExecution(MongoTemplate mongoTemplate) {  
        log.info("######### Rollback - BeforeExecution!!!");  
    }  
    @Execution  
    public void execution(CustomerRepository repository) {  
        log.info("######### Initialize data!!!");  
        List<Customer> customerFlux = Arrays.asList("Madhura", "Josh", "Olga").stream()  
                .map(Customer::new)  
                .map(repository::save)  
                .toList();  
        log.info(customerFlux);  
    }
    // This isn't a good rollback :o
    @RollbackExecution  
    public void rollbackExecution(CustomerRepository repository) {  
        log.info("######### Rollback Execution!!!");  
        repository.deleteAll();  
    }  
}
```

_Console log at startup_
```
: Starting service [Tomcat]
: LiveReload server is running on port 35729
: Mongock runner COMMUNITY version[5.2.4]
: Property transaction-enabled not provided and is unknown if driver is transactionable. BY DEFAULT MONGOCK WILL RUN IN NO-TRANSACTION MODE.
: Started MongodbApplication in 1.159 seconds (process running for 1.418)
: Mongock trying to acquire the lock
: Mongock acquired the lock until: Mon Jan 22 16:34:59 CET 2024
: Starting mongock lock daemon...
: Mongock starting the system update execution id[2024-01-22T16:33:59.483954-451]...
: method[io.mongock.runner.core.executor.system.changes.SystemChangeUnit00001] with arguments: []
: method[beforeExecution] with arguments: [io.mongock.driver.mongodb.springdata.v4.SpringDataMongoV4ChangeEntryRepository]
: APPLIED - {"id"="system-change-00001_before", "type"="before-execution", "author"="mongock", "class"="SystemChangeUnit00001", "method"="beforeExecution"}
: method[execution] with arguments: [io.mongock.driver.mongodb.springdata.v4.SpringDataMongoV4ChangeEntryRepository]
: APPLIED - {"id"="system-change-00001", "type"="execution", "author"="mongock", "class"="SystemChangeUnit00001", "method"="execution"}
: Mongock waiting to release the lock
: Mongock releasing the lock
: Mongock released the lock
: Mongock has finished the system update execution
: Mongock trying to acquire the lock
: Mongock acquired the lock until: Mon Jan 22 16:34:59 CET 2024
: Starting mongock lock daemon...
: Mongock starting the data migration sequence id[2024-01-22T16:33:59.483954-451]...
: method[com.example.mongodb.mongock.migration.CustomerInitialization] with arguments: []
: method[beforeExecution] with arguments: [org.springframework.data.mongodb.core.MongoTemplate]
: ######### BeforeExecution!!!
: APPLIED - {"id"="customer-initializer_before", "type"="before-execution", "author"="myself", "class"="CustomerInitialization", "method"="beforeExecution"}
: method[execution] with arguments: [jdk.proxy3.$Proxy72]
: ######### Initialize data!!!
: [Customer(id=65ae8ae702e8fc7cb47a8e06, name=Madhura), Customer(id=65ae8ae702e8fc7cb47a8e07, name=Josh), Customer(id=65ae8ae702e8fc7cb47a8e08, name=Olga)]
: APPLIED - {"id"="customer-initializer", "type"="execution", "author"="myself", "class"="CustomerInitialization", "method"="execution"}
: Mongock waiting to release the lock
: Mongock releasing the lock
: Mongock released the lock
: Mongock has finished
: Cancelled mongock lock daemon
: Cancelled mongock lock daemon
```
From the log is possible to see that Mongock work in two steps
1 -  MONGOCK lock management documents `mongockChangeLog` and `mongockLock` to verify is the changes was executed and save this change log
2 - MONGOCK lock documents to apply changes
##### [code-repo](https://github.com/leonardopache/jvm_based/tree/master/springboot/mongock)
Examples to install / run / call:
- docker-mongodb
  `docker run --name some-mongo -d mongo:tag`

- maven build and run
  `mvn clean install`
  `mvn spring-boot:run -Dspring-boot.run.jvmArguments='-Dserver.port=8081'`

_____