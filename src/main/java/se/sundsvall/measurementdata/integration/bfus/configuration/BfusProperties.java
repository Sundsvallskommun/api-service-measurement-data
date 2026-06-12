package se.sundsvall.measurementdata.integration.bfus.configuration;

import java.time.LocalDate;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("integration.bfus")
public record BfusProperties(
	int connectTimeout,
	int readTimeout,
	String externalId,
	String identifierType,
	int unitType,
	String unit,
	int periodSize,
	int consumptionDataSource,
	LocalDate cutoffDate) {
}
