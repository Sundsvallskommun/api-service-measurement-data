package se.sundsvall.measurementdata.integration.bfus.model;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MeterReadingPartTest {

	private static final PeriodicValue VALUE = new PeriodicValue("2025-09-01T00:00:00", BigDecimal.ONE, "2");

	@Test
	void accessors() {
		final var part = new MeterReadingPart("kWh", List.of(VALUE));

		assertThat(part.unit()).isEqualTo("kWh");
		assertThat(part.periodicValues()).containsExactly(VALUE);
	}

	@Test
	void equalsHashCodeAndToString() {
		final var part = new MeterReadingPart("kWh", List.of(VALUE));
		final var equal = new MeterReadingPart("kWh", List.of(VALUE));

		assertThat(part)
			.isEqualTo(part)
			.isEqualTo(equal)
			.hasSameHashCodeAs(equal)
			.isNotEqualTo(null)
			.isNotEqualTo("not-a-part")
			.isNotEqualTo(new MeterReadingPart("MWh", List.of(VALUE)))
			.isNotEqualTo(new MeterReadingPart("kWh", List.of()));
		assertThat(part.toString()).contains("kWh");
	}
}
