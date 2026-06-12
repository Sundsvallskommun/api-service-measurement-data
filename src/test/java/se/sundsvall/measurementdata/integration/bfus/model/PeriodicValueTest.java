package se.sundsvall.measurementdata.integration.bfus.model;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PeriodicValueTest {

	@Test
	void accessors() {
		final var value = new PeriodicValue("2025-09-01T00:00:00", BigDecimal.valueOf(0.21), "2");

		assertThat(value.fromDate()).isEqualTo("2025-09-01T00:00:00");
		assertThat(value.consumption()).isEqualByComparingTo("0.21");
		assertThat(value.status()).isEqualTo("2");
	}

	@Test
	void equalsHashCodeAndToString() {
		final var value = new PeriodicValue("2025-09-01T00:00:00", BigDecimal.ONE, "2");
		final var equal = new PeriodicValue("2025-09-01T00:00:00", BigDecimal.ONE, "2");

		assertThat(value)
			.isEqualTo(value)
			.isEqualTo(equal)
			.hasSameHashCodeAs(equal)
			.isNotEqualTo(null)
			.isNotEqualTo("not-a-periodic-value")
			.isNotEqualTo(new PeriodicValue("other", BigDecimal.ONE, "2"))
			.isNotEqualTo(new PeriodicValue("2025-09-01T00:00:00", BigDecimal.TEN, "2"))
			.isNotEqualTo(new PeriodicValue("2025-09-01T00:00:00", BigDecimal.ONE, "9"));
		assertThat(value.toString()).contains("2025-09-01T00:00:00", "2");
	}
}
