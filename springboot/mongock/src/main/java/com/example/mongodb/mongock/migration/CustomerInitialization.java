package com.example.mongodb.mongock.migration;

import com.example.mongodb.mongock.customer.Customer;
import com.example.mongodb.mongock.customer.CustomerRepository;
import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Arrays;
import java.util.List;

@Log4j2
@ChangeUnit(id="customer-initializer", order = "001", author = "myself")
public class CustomerInitialization {

    @BeforeExecution
    public void beforeExecution(MongoTemplate mongoTemplate) {
        log.info("######### BeforeExecution!!!");
    }

    @RollbackBeforeExecution
    public void rollbackBeforeExecution(MongoTemplate mongoTemplate) {
        log.info("######### RollbackBeforeExecution!!!");
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

    @RollbackExecution
    public void rollbackExecution(CustomerRepository repository) {
        log.info("######### BEFORE RollbackExecution!!!");
        repository.deleteAll();
    }

}
