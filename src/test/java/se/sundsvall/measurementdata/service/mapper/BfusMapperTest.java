package se.sundsvall.measurementdata.service.mapper;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.measurementdata.integration.bfus.model.ConsumptionResponse;
import se.sundsvall.measurementdata.integration.bfus.model.ConsumptionResponse.Content;
import se.sundsvall.measurementdata.integration.bfus.model.MeterReadingPart;
import se.sundsvall.measurementdata.integration.bfus.model.PeriodicValue;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.measurementdata.service.mapper.BfusMapper.toMeasurements;

class BfusMapperTest {

	private static final String FACILITY_ID = "735999109171206078";

	@Test
	void toMeasurements_withNullResponse_returnsEmpty() {
		assertThat(toMeasurements(FACILITY_ID, null)).isEmpty();
	}

	@Test
	void toMeasurements_withNullContent_returnsEmpty() {
		assertThat(toMeasurements(FACILITY_ID, new ConsumptionResponse(null))).isEmpty();
	}

	@Test
	void toMeasurements_withNullMeterReadingParts_returnsEmpty() {
		assertThat(toMeasurements(FACILITY_ID, new ConsumptionResponse(new Content(null)))).isEmpty();
	}

	@Test
	void toMeasurements_filtersNullParts() {
		final var response = new ConsumptionResponse(new Content(Arrays.asList(
			(MeterReadingPart) null,
			new MeterReadingPart("kWh", List.of(new PeriodicValue("2025-09-01T00:00:00", BigDecimal.ONE, "2"))))));

		assertThat(toMeasurements(FACILITY_ID, response)).hasSize(1);
	}

	@Test
	void toMeasurements_withNullPeriodicValues_returnsEmpty() {
		final var response = new ConsumptionResponse(new Content(List.of(new MeterReadingPart("kWh", null))));

		assertThat(toMeasurements(FACILITY_ID, response)).isEmpty();
	}

	@Test
	void toMeasurements_filtersNullPeriodicValues() {
		final var response = new ConsumptionResponse(new Content(List.of(
			new MeterReadingPart("kWh", Arrays.asList(
				(PeriodicValue) null,
				new PeriodicValue("2025-09-01T00:00:00", BigDecimal.TEN, "2"))))));

		assertThat(toMeasurements(FACILITY_ID, response)).hasSize(1);
	}

	@Test
	void toMeasurements_mapsFieldsToDataWarehouseReaderRepresentation() {
		final var response = new ConsumptionResponse(new Content(List.of(
			new MeterReadingPart("kWh", List.of(
				new PeriodicValue("2025-09-01T00:15:00", BigDecimal.valueOf(0.28), "2"))))));

		final var result = toMeasurements(FACILITY_ID, response);

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
		final var response = new ConsumptionResponse(new Content(List.of(
			new MeterReadingPart(null, List.of(
				new PeriodicValue("2025-09-01T00:00:00", BigDecimal.ONE, "2"))))));

		assertThat(toMeasurements(FACILITY_ID, response).getFirst().getUnit()).isEqualTo("kWh");
	}

	@Test
	void toMeasurements_resolvesTimestampInSummerTimeAsCEST() {
		final var response = new ConsumptionResponse(new Content(List.of(
			new MeterReadingPart("kWh", List.of(
				new PeriodicValue("2025-07-15T12:00:00", BigDecimal.ONE, "2"))))));

		assertThat(toMeasurements(FACILITY_ID, response).getFirst().getDateAndTime())
			.isEqualTo(OffsetDateTime.of(2025, 7, 15, 12, 0, 0, 0, ZoneOffset.ofHours(2)));
	}

	@Test
	void toMeasurements_resolvesTimestampInWinterTimeAsCET() {
		final var response = new ConsumptionResponse(new Content(List.of(
			new MeterReadingPart("kWh", List.of(
				new PeriodicValue("2025-01-15T12:00:00", BigDecimal.ONE, "2"))))));

		assertThat(toMeasurements(FACILITY_ID, response).getFirst().getDateAndTime())
			.isEqualTo(OffsetDateTime.of(2025, 1, 15, 12, 0, 0, 0, ZoneOffset.ofHours(1)));
	}

	@Test
	void toMeasurements_withNullFromDate_mapsNullTimestamp() {
		final var response = new ConsumptionResponse(new Content(List.of(
			new MeterReadingPart("kWh", List.of(
				new PeriodicValue(null, BigDecimal.ONE, "2"))))));

		assertThat(toMeasurements(FACILITY_ID, response).getFirst().getDateAndTime()).isNull();
	}
}
