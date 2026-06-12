package se.sundsvall.measurementdata.integration.bfus.model;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.measurementdata.integration.bfus.model.ConsumptionResponse.Content;

import static org.assertj.core.api.Assertions.assertThat;

class ConsumptionResponseTest {

	private static final MeterReadingPart PART = new MeterReadingPart("kWh", List.of(
		new PeriodicValue("2025-09-01T00:00:00", BigDecimal.ONE, "2")));

	@Test
	void accessors() {
		final var content = new Content(List.of(PART));
		final var response = new ConsumptionResponse(content);

		assertThat(response.content()).isEqualTo(content);
		assertThat(response.content().meterReadingParts()).containsExactly(PART);
	}

	@Test
	void equalsHashCodeAndToString() {
		final var response = new ConsumptionResponse(new Content(List.of(PART)));
		final var equal = new ConsumptionResponse(new Content(List.of(PART)));

		assertThat(response)
			.isEqualTo(response)
			.isEqualTo(equal)
			.hasSameHashCodeAs(equal)
			.isNotEqualTo(null)
			.isNotEqualTo("not-a-response")
			.isNotEqualTo(new ConsumptionResponse(null));
		assertThat(response.toString()).contains("Content");
	}

	@Test
	void contentEqualsAndToString() {
		final var content = new Content(List.of(PART));

		assertThat(content)
			.isEqualTo(new Content(List.of(PART)))
			.isNotEqualTo(new Content(List.of()))
			.isNotEqualTo(null);
		assertThat(content.toString()).contains("MeterReadingPart");
	}
}
