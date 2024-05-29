### Welcome

This example of creating an Authorization Server

**Dependencies**
- spring-boot-starter-security
- spring-boot-starter-oauth2-authorization-server

**To run this sample**

1. Clone the current project.
2. Install the dependencies and start the server:
```sh
$ mvn clean install -U
$ mvn spring-boot:run 
```

4. Obtain the token with
```sh
curl http://localhost:9000/.well-known/openid-configuration
```
