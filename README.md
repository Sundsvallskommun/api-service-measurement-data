# MeasurementData

_The service provides resources for retrieving measurement data matching supplied parameters._

## Getting Started

### Prerequisites

- **Java 21 or higher**
- **Maven**
- **MariaDB**
- **Git**
- **[Dependent Microservices](#dependencies)**

### Installation

1. **Clone the repository:**

```bash
git clone https://github.com/Sundsvallskommun/api-service-measurement-data.git
cd api-service-measurement-data
```

2. **Configure the application:**

   Before running the application, you need to set up configuration settings.
   See [Configuration](#configuration)

   **Note:** Ensure all required configurations are set; otherwise, the application may fail to start.

3. **Ensure dependent services are running:**

   If this microservice depends on other services, make sure they are up and accessible. See [Dependencies](#dependencies) for more details.

4. **Build and run the application:**

   ```bash
   mvn spring-boot:run
   ```

## Dependencies

This microservice depends on the following services:

- **DataWarehouseReader**
  - **Purpose:** Used for reading measurement data.
  - **Repository:** [https://github.com/Sundsvallskommun/api-service-datawarehousereader](https://github.com/Sundsvallskommun/api-service-datawarehousereader)
  - **Setup Instructions:** See documentation in repository above for installation and configuration steps.

Ensure that these services are running and properly configured before starting this microservice.

## API Documentation

See `openapi.yaml` located in directory `src\integration-test\resources\openapi` or access the API documentation via Swagger UI:

- **Swagger UI:** [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

## Usage

### API Endpoints

See the [API Documentation](#api-documentation) for detailed information on available endpoints.

### Example Request

```bash
curl -X GET http://localhost:8080/2281/measurement-data?
  partyId=81471222-5798-11e9-ae24-57fa13b361e1&
  category=DISTRICT_HEATING&
  facilityId=123456&
  fromDate=2025-01-01T00:00:00.000+01:00&
  toDate=2025-01-31T23:59:59.999+01:00&
  aggregateOn=HOUR
```

## Configuration

Configuration is crucial for the application to run successfully. Ensure all necessary settings are configured in `application.yml`.

### Key Configuration Parameters

- **Server Port:**

```yaml
server:
  port: 8080
```

- **External Service URLs**

```yaml
config:
  integration:
    token-uri: <token-url>
    datawarehousereader:
      url: <service-url>
      client-id: <client-id>
      client-secret: <client-secret>
      connectTimeout: <connect timeout in seconds>
      readTimeout: <read timeout in seconds>
```

### Additional Notes

- **Application Profiles:**

  Use Spring profiles (`dev`, `prod`, etc.) to manage different configurations for different environments.

- **Logging Configuration:**

  Adjust logging levels if necessary.

## Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](https://github.com/Sundsvallskommun/.github/blob/main/.github/CONTRIBUTING.md) for guidelines.

## License

This project is licensed under the [MIT License](LICENSE).

## Status

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-measurement-data&metric=alert_status)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-measurement-data)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-measurement-data&metric=reliability_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-measurement-data)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-measurement-data&metric=security_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-measurement-data)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-measurement-data&metric=sqale_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-measurement-data)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-measurement-data&metric=vulnerabilities)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-measurement-data)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-measurement-data&metric=bugs)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-measurement-data)

## 

&copy; 2023 Sundsvalls kommun
