package se.sundsvall.measurementdata.integration.datawarehousereader.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("integration.datawarehousereader")
public record DataWarehouseReaderProperties(int connectTimeout, int readTimeout) {
}
