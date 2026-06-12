package se.sundsvall.measurementdata.integration.bfus.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Response from the BFUS {@code /EP/Reading/Consumptions} endpoint. Only the fields consumed by the service are
 * modelled; the {@code Header} object and any other fields are intentionally ignored.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ConsumptionResponse(
	@JsonProperty("Content") Content content) {

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Content(
		@JsonProperty("MeterReadingParts") List<MeterReadingPart> meterReadingParts) {
	}
}
