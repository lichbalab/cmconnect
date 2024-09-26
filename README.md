
# Certificate Manager Connect (CMC)

# CMC Overview

CMC provides a comprehensive solution for **secure certificate and key management**, enabling seamless SSL integration and enhancing the security posture of modern applications. 
Designed with flexibility and ease of use in mind, it offers robust tools for managing HTTPS connections and ensuring that sensitive security assets are handled efficiently.

## Key Features:

- **Centralized Certificate & Key Management (CMC server):**
    - Store and manage SSL/TLS certificates and private keys in a centralized repository.
    - The centralization of certificates simplifies administration, enhances security by reducing the risk of key compromise, and facilitates easier auditing and compliance.

- **Developer-Friendly SDK for SSL Integration:**
    - The provided **SDK** enables quick and simple integration of SSL/TLS functionality into applications, supporting secure HTTPS communication without the need for deep security expertise.
    - The SDK includes ready-made utilities for configuring **HTTPS listeners** and clients, streamlining the process of adding SSL encryption to both servers and client connections.
    - **Hot Certificate Reloading**: A standout feature of the SDK is its ability to dynamically reload certificates and private keys **without restarting applications**, ensuring that key rotations, renewals, or updates occur seamlessly in production environments.

- **Broad Application Support:**
    - The SDK is designed to integrate with a wide range of modern application frameworks and environments, including but not limited to:
        - **Spring Boot applications**, offering out-of-the-box support for HTTPS configurations and enhancing security in microservice architectures.
        - **Other Java-based applications** or frameworks with built-in HTTP/HTTPS listeners.

- **RESTful API for Easy Integration:**
    - The library provides a well-documented **REST API** for automating the retrieval and management of certificates, allowing developers to easily integrate it into existing CI/CD pipelines or other automation tools.
    - This API simplifies communication with external systems, enabling developers to manage SSL certificates, update configurations, and monitor security status remotely.

## How to Use CMC

### Requirements
- Java JDK 21 or higher
- Docker (optional, for containerized deployment)

### Starting the CMC Server with REST API
```bash
docker-compose up -d
```
Once running, KeyStoreConnect allows you to...
http://localhost:8080

### CMC SDK Integration
Integrate the CMC Spring Boot SDK into your application by adding the following dependency to your `pom.xml` file:
```xml
<dependency>
    <groupId>com.lichbalab</groupId>
    <artifactId>cmc-spring-sdk</artifactId>
    <version>2024.1-SNAPSHOT</version>
</dependency>
```
#### Configuring the SDK
The following properties can be configured to customize the behavior of the CMC SDK. These properties are typically defined in the application's configuration file (`application.properties`, `application.yml`) or overridden by environment variables.
##### `cmc.sdk.base-url`

- **Default**: `http://localhost:8080`
- **Description**:
  Defines the base URL for connecting to the CMC API. The actual port can be overridden by setting the `TEST_CMC_API_PORT` environment variable.

  Example:
  ```properties
  cmc.sdk.base-url=http://localhost:8080

##### `cmc.sdk.ssl-bundle-key-alias`

- **Description**:
  Defines the alias of the key pair used in HTTPS listener.

  Example:
  ```properties
  cmc.sdk.ssl-bundle-key-alias=cmc-ssl-bundle

##### `cmc.sdk.synchronisation-scheduling-cron`

- **Default**: `0 0 0 * * *`
- **Description**:
  Defines the schedule for synchronizing certificates with the CMC server. The value is a cron expression that determines when the synchronization task should run.

  Example:
  ```properties
  cmc.sdk.synchronisation-scheduling-cron=0 0 0 * * *

##### `cmc.sdk.client-auth`

- **Default**: `NONE`
- **Description**:
  Client authentication type. Possible values are:
  - `NONE` Client authentication is not wanted., 
  - `NEED` Client authentication is wanted but not mandatory., 
  - `WANT`Client authentication is needed and mandatory.

  Applicable only for HTTPS listener connections.
  
  Example:
  ```properties
  cmc.sdk.client-auth=WANT

##### `cmc.sdk.disable-hostname-verification`

- **Default**: `false`
- **Description**:
  Whether to disable hostname verification when connecting to HTTPS server. This is useful for testing or when the server's certificate does not match the hostname. 
Applicable only for HTTPS client connections. 

  Example:
  ```properties
  cmc.sdk.disable-hostname-verification=true

##### `cmc.sdk.enabled`

- **Default**: `true`
- **Description**:
  Whether to enable CMC SDK.

  Example:
  ```properties
  cmc.sdk.enabled=false



## Building from Source

### Clone the Repository
```bash
git clone https://github.com/lichbalab/cmconnect.git
cd cmconnect
```

### Build the Project
To build the project, use Maven:
```bash
mvn clean package
```
This command cleans any compiled files in your project, ensuring a fresh build from the source.

### Build Docker Image
If you prefer Docker, you can build an image using:
```bash
docker build -t lichbalab:cmc-2024.1 .
```
This command builds a Docker image tagged `lichbalab:cmc-2024.1`.

### Run Docker Image
Run the Docker image with:
```bash
docker-compose up -d
```
This starts the Docker container in detached mode.

To follow the log output:
```bash
docker logs -f <conainer-id>
```
To get <container-id> use:
```bash
docker ps
```

#### Start CMC app
##### Directly via JAR:
```bash
java -jar target/ksc-rest-api-2024.1-SNAPSHOT.jar
```
#### Via Maven:
```bash
mvn spring-boot:run
```

#### Debug Mode:
Start in debug mode to enable breakpoints and other debugging features:
```bash
java -jar -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 target/ksc-rest-api-2024.1-SNAPSHOT.jar
```

## Configuration
Configure KeyStoreConnect by editing...

[TBD]
## Troubleshooting
Encountering issues? Check out our troubleshooting guide...

[TBD]

## Contributing
We welcome contributions! Please see our contribution guidelines for how to submit pull requests, report bugs, or request features.

## License
KeyStoreConnect is released under the [[TBD]] license. See the LICENSE file for more details.

## Contact
For support or questions, reach out to us at lichbalab@gmail.com.
