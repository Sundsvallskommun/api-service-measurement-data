package se.sundsvall.measurementdata.service.mapper;

import generated.se.sundsvall.bfus.ConsumptionContent;
import generated.se.sundsvall.bfus.ConsumptionResponse;
import generated.se.sundsvall.bfus.MeterReadingPart;
import generated.se.sundsvall.bfus.PeriodicValue;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.measurementdata.service.mapper.BfusMapper.toMeasurements;

class BfusMapperTest {

	private static final String FACILITY_ID = "735999109171206078";

	private static ConsumptionResponse response(final MeterReadingPart... parts) {
		return new ConsumptionResponse().content(new ConsumptionContent().meterReadingParts(Arrays.asList(parts)));
	}

	private static MeterReadingPart part(final String unit, final PeriodicValue... values) {
		return new MeterReadingPart().unit(unit).periodicValues(Arrays.asList(values));
	}

	private static PeriodicValue value(final String fromDate, final BigDecimal consumption) {
		return new PeriodicValue().fromDate(fromDate).consumption(consumption).status("2");
	}

	@Test
	void toMeasurements_withNullResponse_returnsEmpty() {
		assertThat(toMeasurements(FACILITY_ID, null)).isEmpty();
	}

	@Test
	void toMeasurements_withNullContent_returnsEmpty() {
		assertThat(toMeasurements(FACILITY_ID, new ConsumptionResponse())).isEmpty();
	}

	@Test
	void toMeasurements_withNullMeterReadingParts_returnsEmpty() {
		assertThat(toMeasurements(FACILITY_ID, new ConsumptionResponse().content(new ConsumptionContent()))).isEmpty();
	}

	@Test
	void toMeasurements_filtersNullParts() {
		final var response = response(null, part("kWh", value("2025-09-01T00:00:00", BigDecimal.ONE)));
		assertThat(toMeasurements(FACILITY_ID, response)).hasSize(1);
	}

	@Test
	void toMeasurements_withNullPeriodicValues_returnsEmpty() {
		assertThat(toMeasurements(FACILITY_ID, response(new MeterReadingPart().unit("kWh")))).isEmpty();
	}

	@Test
	void toMeasurements_filtersNullPeriodicValues() {
		final var part = new MeterReadingPart().unit("kWh").periodicValues(Arrays.asList(null, value("2025-09-01T00:15:00", BigDecimal.TEN)));
		assertThat(toMeasurements(FACILITY_ID, response(part))).hasSize(1);
	}

	@Test
	void toMeasurements_mapsFieldsToDataWarehouseReaderRepresentation() {
		final var result = toMeasurements(FACILITY_ID, response(part("kWh", value("2025-09-01T00:15:00", BigDecimal.valueOf(0.28)))));

		assertThat(result).hasSize(1);
		final var measurement = result.getFirst();
		assertThat(measurement.getFacilityId()).isEqualTo(FACILITY_ID);
		assertThat(measurement.getFeedType()).isEqualTo("Energy");
		assertThat(measurement.getUnit()).isEqualTo("kWh");
		assertThat(measurement.getUsage()).isEqualByComparingTo("0.28");
		assertThat(measurement.getInterpolation()).isZero();
	}

	@Test
	void toMeasurements_withNullUnit_defaultsToKwh() {
		final var result = toMeasurements(FACILITY_ID, response(part(null, value("2025-09-01T00:00:00", BigDecimal.ONE))));
		assertThat(result.getFirst().getUnit()).isEqualTo("kWh");
	}

	@Test
	void toMeasurements_resolvesTimestampInSummerTimeAsCEST() {
		final var result = toMeasurements(FACILITY_ID, response(part("kWh", value("2025-07-15T12:00:00", BigDecimal.ONE))));
		assertThat(result.getFirst().getDateAndTime()).isEqualTo(OffsetDateTime.of(2025, 7, 15, 12, 0, 0, 0, ZoneOffset.ofHours(2)));
	}

	@Test
	void toMeasurements_resolvesTimestampInWinterTimeAsCET() {
		final var result = toMeasurements(FACILITY_ID, response(part("kWh", value("2025-01-15T12:00:00", BigDecimal.ONE))));
		assertThat(result.getFirst().getDateAndTime()).isEqualTo(OffsetDateTime.of(2025, 1, 15, 12, 0, 0, 0, ZoneOffset.ofHours(1)));
	}

	@Test
	void toMeasurements_withNullFromDate_mapsNullTimestamp() {
		final var result = toMeasurements(FACILITY_ID, response(part("kWh", value(null, BigDecimal.ONE))));
		assertThat(result.getFirst().getDateAndTime()).isNull();
	}
}
