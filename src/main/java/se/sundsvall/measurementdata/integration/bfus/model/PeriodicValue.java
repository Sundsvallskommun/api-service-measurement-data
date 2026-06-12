package se.sundsvall.measurementdata.integration.bfus.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PeriodicValue(
	@JsonProperty("FromDate") String fromDate,
	@JsonProperty("Consumption") BigDecimal consumption,
	@JsonProperty("Status") String status) {
}
