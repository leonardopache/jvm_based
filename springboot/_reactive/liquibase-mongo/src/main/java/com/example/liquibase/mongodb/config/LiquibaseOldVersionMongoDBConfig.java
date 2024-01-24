package com.example.liquibase.mongodb.config;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.exception.LiquibaseException;
import liquibase.ext.mongodb.database.MongoLiquibaseDatabase;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@Slf4j
public class LiquibaseOldVersionMongoDBConfig {
    @Value("${spring.data.mongodb.uri}")
    private String connectionUrl;

    @Value("${changeLogFile}")
    private String changeLogFile;

    /**
     * Execute the update when start the application.
     */
    @Bean
    @Lazy
    public Liquibase oldLiquibase() {
        try (MongoLiquibaseDatabase db = (MongoLiquibaseDatabase) DatabaseFactory.getInstance()
                .openDatabase(connectionUrl, "", "", null, new ClassLoaderResourceAccessor());
             Liquibase liquibase = new Liquibase("db-json/master.json", new ClassLoaderResourceAccessor(), db)) {

            liquibase.update(new Contexts());
            return liquibase;
        } catch (LiquibaseException e) {
            log.error("error running liquibase {}", e.getMessage());
        }
        return null;
    }
}
