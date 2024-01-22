package com.example.mongodb.mongock;

import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableMongock
@SpringBootApplication
public class MongodbApplication {
	public static void main(String[] args) {
		SpringApplication.run(MongodbApplication.class, args);
	}
}