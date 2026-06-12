package se.sundsvall.measurementdata.service.mapper;

import generated.se.sundsvall.datawarehousereader.Measurement;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.measurementdata.api.model.Display.AGGREGATE;
import static se.sundsvall.measurementdata.api.model.Display.ONLYAGGREGATED;
import static se.sundsvall.measurementdata.service.mapper.ElectricityAggregator.applyAggregation;

class ElectricityAggregatorTest {

	private static final OffsetDateTime TS1 = OffsetDateTime.of(2025, 9, 1, 0, 0, 0, 0, ZoneOffset.ofHours(2));
	private static final OffsetDateTime TS2 = OffsetDateTime.of(2025, 9, 1, 0, 15, 0, 0, ZoneOffset.ofHours(2));

	private static Measurement measurement(final String facilityId, final String feedType, final String unit, final OffsetDateTime dateAndTime, final BigDecimal usage) {
		return new Measurement()
			.facilityId(facilityId)
			.feedType(feedType)
			.unit(unit)
			.dateAndTime(dateAndTime)
			.usage(usage);
	}

	@Test
	void applyAggregation_withNullDisplay_returnsInputUnchanged() {
		final var input = List.of(
			measurement("f1", "Energy", "kWh", TS1, BigDecimal.TEN),
			measurement("f2", "Energy", "kWh", TS1, BigDecimal.valueOf(20)));

		assertThat(applyAggregation(input, null)).isSameAs(input);
	}

	@Test
	void applyAggregation_withAggregate_keepsPerFacilityAndAddsAggregatedSeries() {
		final var input = List.of(
			measurement("f1", "Energy", "kWh", TS1, BigDecimal.TEN),
			measurement("f2", "Energy", "kWh", TS1, BigDecimal.valueOf(20)),
			measurement("f1", "Energy", "kWh", TS2, BigDecimal.valueOf(5)),
			measurement("f1", "outdoor_temperature", "C", TS1, BigDecimal.valueOf(15)));

		final var result = applyAggregation(input, AGGREGATE);

		// 4 originals (incl. temperature) + 2 aggregated points
		assertThat(result).hasSize(6);

		final var aggregated = result.stream().filter(m -> "Energy_aggregated".equals(m.getFeedType())).toList();
		assertThat(aggregated).hasSize(2);
		assertThat(aggregated).extracting(Measurement::getDateAndTime).containsExactly(TS1, TS2);
		assertThat(aggregated.getFirst().getUnit()).isEqualTo("kWh");
		assertThat(aggregated.getFirst().getUsage()).isEqualByComparingTo("30");
		assertThat(aggregated.getFirst().getFacilityId()).isEqualTo("f1,f2");
		assertThat(aggregated.getFirst().getInterpolation()).isEqualTo(-1);
		assertThat(aggregated.getLast().getUsage()).isEqualByComparingTo("5");
	}

	@Test
	void applyAggregation_withOnlyAggregated_returnsOnlyAggregatedSeries() {
		final var input = List.of(
			measurement("f1", "Energy", "kWh", TS1, BigDecimal.TEN),
			measurement("f2", "Energy", "kWh", TS1, BigDecimal.valueOf(20)),
			measurement("f1", "outdoor_temperature", "C", TS1, BigDecimal.valueOf(15)));

		final var result = applyAggregation(input, ONLYAGGREGATED);

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getFeedType()).isEqualTo("Energy_aggregated");
		assertThat(result.getFirst().getUsage()).isEqualByComparingTo("30");
	}

	@Test
	void applyAggregation_doesNotAggregateNonEnergySeries() {
		final var input = List.of(
			measurement("f1", "outdoor_temperature", "C", TS1, BigDecimal.valueOf(15)),
			measurement("f2", "outdoor_temperature", "C", TS1, BigDecimal.valueOf(17)));

		assertThat(applyAggregation(input, ONLYAGGREGATED)).isEmpty();
	}

	@Test
	void applyAggregation_treatsNullUsageAsZero() {
		final var input = List.of(
			measurement("f1", "Energy", "kWh", TS1, null),
			measurement("f2", "Energy", "kWh", TS1, BigDecimal.valueOf(20)));

		final var result = applyAggregation(input, ONLYAGGREGATED);

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getUsage()).isEqualByComparingTo("20");
	}
}
