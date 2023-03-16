# Bank Fraud Detection Service

This is a simple credit card fraud detection service, provide a REST API.

## Functional Requirement
    Service has 1 endpoint ( "/analyzeTransaction") takes in serveral parameters as inputs
    Call another service to get information regarding the transaction
    Apply some business logic.
    Responed a JSON object

## Non Functional Requirement
    Monitoring -endpoint to show basic stats
    Packaging- build a docker image
    Test - unit test(integration test), coverage report, load-test.
    Service should be able to handle and respond to input errors.
    Service should log the following underlined transaction information for debugging
    There should have a time limit for connect to external api.

### Obfuscate Card Number
    Achieved by override toString function in model
    Drawback: can accidently call other function to print out the cardNum.
    Only mask the carNumber when the length is over 13.

    Alternatives:  
        Custom log mask pattern,  drawback: might impact performance, rules can be hard to define.

### Unit Test
    coverage: 
        Class: 100%
        Method: 100%
        Line: 93%

    The 7% untested line is from mapstruct auto generated lines.
    Since it's a lot line about test if the input is null, so used null object to test against part of the mapstruct object.

### Load Test
    Jmeter or Gatling

### SwaggerUI
    http://localhost:8080/swagger-ui/index.html

### Observability
    http://localhost:8081/

    Here enabled expose all information on the port in the dev profile, can limit the exposure in test or prod profile.

    Ideally would have a system to collect customed metrics and expose to port, 
    use promethus to scrap the metrics and Grafana for visualization.

### Docker Image
    docker build -t mastercard-fraud-server .
    docker run -p 8080:8080 -p 8081:8081 mastercard-fraud-server
