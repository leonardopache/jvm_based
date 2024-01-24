package com.example.liquibase.mongodb.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("customers")
public class Customer {
    @Id
    private String id;
    private String name;

    public Customer(String name) {
        this.name = name;
    }
}
