package se.sundsvall.measurementdata.integration.bfus.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MeterReadingPart(
	@JsonProperty("Unit") String unit,
	@JsonProperty("PeriodicValues") List<PeriodicValue> periodicValues) {
}
