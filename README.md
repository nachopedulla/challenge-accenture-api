# Challenge Accenture

Challenge API with an ABM of Credit Cards.

### Security
The API has basic authentication, with username and password as secrets that needs to be configured:
```
       SPRING_SECURITY_USERNAME
       SPRING_SECURITY_PASSWORD
```  
  
For local testing purposes a properties file exists on resources is configured, set the SPRING_PROFILES_ACTIVE to local.

### Documentation

OpenApi is included, the swagger is exposed on path ```/swagger-ui/index.html#/ ```

### Logging 

The API has a basic logging filter that intercepts all request and:

- Adds a unique trace-id to the MVC context to keep track of the request cycle.
- Logs incoming method, path and params
- Logs the response status

### Dockerfile

A Dockerfile is added using Java 21 image.

### Data

On local env a data.sql script is executed to add some initial data to facilitate the tests:

| ID                                   | Customer |   Card number    |  Status  |   Brand    |
|:-------------------------------------|:--------:|:----------------:|:--------:|:----------:|
| a0d281a5-4de5-4f81-bc27-78eaefd28d36 | 12345678 | 4501000100010001 |  ACTIVE  |    VISA    |
| 27cbe3da-ced2-4b36-a56e-565f26252c68 | 12345678 | 4501000100010002 | INACTIVE |    VISA    |
| 420d971b-054d-4f0e-8c56-873acf87ad22 | 12345678 | 5501000100010003 |  ACTIVE  | MASTERCARD |
| 0f9bee11-327d-4685-acf6-87afff21b272 | 87654321 | 4502000100010001 |  ACTIVE  |    VISA    |
| 8110ed08-b5f9-4ec2-8326-69089bf7f161 | 87654321 | 5502000100010002 |  ACTIVE  | MASTERCARD |

