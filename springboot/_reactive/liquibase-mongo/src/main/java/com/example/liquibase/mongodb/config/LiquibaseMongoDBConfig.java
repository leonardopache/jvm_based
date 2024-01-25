package com.example.liquibase.mongodb.config;

import liquibase.command.CommandResults;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionCommandStep;
import liquibase.exception.LiquibaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LiquibaseMongoDBConfig {
    @Value("${spring.data.mongodb.uri}")
    private String connectionUrl;

    @Value("${changeLogFile}")
    private String changeLogFile;

    /**
     * Execute the update when start the application.
     */
    @Bean
    public CommandResults liquibaseUpdate() throws LiquibaseException {
        return new CommandScope("update")
                .addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, changeLogFile)
                .addArgumentValue(DbUrlConnectionCommandStep.URL_ARG, connectionUrl)
                .execute();
    }
}
