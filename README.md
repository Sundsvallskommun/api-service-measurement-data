# MeasurementData

## Leverantör

Sundsvalls kommun

## Beskrivning
MeasurementData är en tjänst som tillhandahåller aktuell mätdata utifrån angiven kategori.

## Tekniska detaljer

### Integrationer
Tjänsten integrerar mot:

* Mikrotjänst DataWarehouseReader

### Starta tjänsten

|Konfigurationsnyckel|Beskrivning|
|---|---|
|`add.key`|Add description|


### Paketera och starta tjänsten
Applikationen kan paketeras genom:

```
./mvnw package
```
Kommandot skapar filen `api-service-measurement-data-<version>.jar` i katalogen `target`. Tjänsten kan nu köras genom kommandot `java -jar target/api-service-measurement-data-<version>.jar`.

### Bygga och starta med Docker
Exekvera följande kommando för att bygga en Docker-image:

```
docker build -f src/main/docker/Dockerfile -t api.sundsvall.se/ms-measurement-data:latest .
```

Exekvera följande kommando för att starta samma Docker-image i en container:

```
docker run -i --rm -p8080:8080 api.sundsvall.se/ms-measurement-data

```

#### Kör applikationen lokalt

Exekvera följande kommando för att bygga och starta en container i sandbox mode:  

```
docker-compose -f src/main/docker/docker-compose-sandbox.yaml build && docker-compose -f src/main/docker/docker-compose-sandbox.yaml up
```


## 
Copyright (c) 2022 Sundsvalls kommun