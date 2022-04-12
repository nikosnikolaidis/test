# smartclide-TD-Principal

The backend service that start new analysis for the calculation of the TD Principal and accesses the results.
This component a is REST API implemented in Spring Boot.
- port 8555
- Τhe URL of the sonarqube instance can be changed via the environment variable GR_NIKOS_SMARTCLIDE_SONARQUBE_URL (default: http://localhost:9000)
- OpenAPI with Swagger UI (/api and /api-ui)

### (Get) Project Measures
##### Mandatory PathVariable:
Project Name
##### Example Request:
```
/api/analysis/commons-io/measures
```

### (Get) Project Issues
##### Mandatory PathVariable:
Project Name
##### Example Request:
```
/api/analysis/commons-io/issues
```

### (Post) Start Analysis
##### Mandatory Header:
```
"Content-Type":  "application/json"
```
##### Example Request:
```
/api/analysis
```
##### Example Body:
```
{
    "gitURL":"https://github.com/apache/commons-io"
}
```