###### Maven Plugin

- Start PostgreSQL on Docker:

```sh
docker run \
    --name myPostgresDb \
    -p 5455:5432 \
    -e POSTGRES_USER=postgresUser \
    -e POSTGRES_PASSWORD=postgresPW \
    -e POSTGRES_DB=postgresDB \
    -d postgres
```

- Create Java project

```sh
mvn archetype:generate -DgroupId=com.test.liquibase -DartifactId=mvn-liquibase-plugin -DarchetypeArtifactId=maven-archetype-quickstart
cd mvn-liquibase-plugin
mvn clean install -U
java -cp target/mvn-liquibase-plugin-1.0-SNAPSHOT.jar com.test.liquibase.App
# Hello World!
```

- Configuration of Maven dependencies

```xml
<properties>
    <liquibase-maven-plugin.version>4.25.1</liquibase-maven-plugin.version>
    <org.postgres.version>42.6.0</org.postgres.version>
</properties>

<!-- LIQUIBASE using manual maven command -->
<plugin>
<groupId>org.liquibase</groupId>
<artifactId>liquibase-maven-plugin</artifactId>
<version>${liquibase-maven-plugin.version}</version>
<configuration>
    <propertyFile>liquibase.properties</propertyFile>
</configuration>
<executions>
    <!-- Execute validation during the process-resources maven phase -->
    <execution>
        <phase>process-resources</phase>
        <goals>
            <goal>validate</goal>
        </goals>
    </execution>
</executions>
<dependencies>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>${org.postgres.version}</version>
    </dependency>
</dependencies>
</plugin>
```

- Create changeLog

```sh
# Create the xml files with the changeSet 
# Configure liquibase.properties into resource folder with:

#liquibase.properties
changeLogFile=db/master.xml  
url=jdbc:postgresql://localhost:5455/postgresDB  
username=  
password=
```

- Validate - Update

```sh
# validation during the maven install
mvn clean install

# update manual with maven
mvn liquibase:update
```