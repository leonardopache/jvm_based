package com.example.liquibase.mongodb.customer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public List<Customer> saveAll(String... names) {
        return Arrays.asList(names).stream()
                .map(Customer::new)
                .map(customerRepository::save)
                .toList();
    }

    public List<Customer> getAll() {
        return customerRepository.findAll();
    }
}
