# KeyStoreConnect

## How to build
mvn clean package

## How to start
Go to the module config-rest-api and run the following command:
```bash
java -jar target/config-rest-api-0.0.1-SNAPSHOT.jar
```
or via maven:
```bash
mvn spring-boot:run
```

Start in debug mode:
```bash
java -jar  -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=*:5005 target/config-rest-api-0.0.1-SNAPSHOT.jar
```
## OAS 3.0 API Documentation
http://localhost:8080/swagger-ui.html