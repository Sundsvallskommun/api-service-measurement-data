package se.sundsvall.measurementdata.service.mapper;

import generated.se.sundsvall.datawarehousereader.Measurement;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.measurementdata.api.model.Aggregation;
import se.sundsvall.measurementdata.api.model.Category;
import se.sundsvall.measurementdata.api.model.MeasurementDataSearchParameters;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static se.sundsvall.measurementdata.service.mapper.DataWarehouseReaderMapper.toData;

class DataWarehouseReaderMapperTest {

	private static final Aggregation AGGREGATION = Aggregation.QUARTER;
	private static final Category CATEGORY = Category.DISTRICT_HEATING;
	private static final List<String> FACILITY_ID = List.of("facilityId");
	private static final OffsetDateTime FROM_DATE = now().with(firstDayOfYear());
	private static final String PARTY_ID = "partyId";
	private static final OffsetDateTime TO_DATE = now().with(firstDayOfYear()).plusMonths(1);

	private static MeasurementDataSearchParameters createParameters() {
		return MeasurementDataSearchParameters.create()
			.withAggregateOn(AGGREGATION)
			.withCategory(CATEGORY)
			.withFacilityIds(FACILITY_ID)
			.withFromDate(FROM_DATE)
			.withPartyId(PARTY_ID)
			.withToDate(TO_DATE);
	}

	@Test
	void toData_shouldMapParametersCorrectly() {
		final var parameters = createParameters();
		final var measurements = List.<Measurement>of();

		final var result = toData(parameters, measurements);

		assertThat(result.getAggregateOn()).isEqualTo(AGGREGATION);
		assertThat(result.getCategory()).isEqualTo(CATEGORY);
		assertThat(result.getFacilityIds()).isEqualTo(FACILITY_ID);
		assertThat(result.getFromDate()).isEqualTo(FROM_DATE);
		assertThat(result.getToDate()).isEqualTo(TO_DATE);
	}

	@Test
	void toData_withEmptyMeasurementsList_shouldReturnEmptyMeasurementSeries() {
		final var parameters = createParameters();
		final var measurements = Collections.<Measurement>emptyList();

		final var result = toData(parameters, measurements);

		assertThat(result.getMeasurementSeries()).isEmpty();
	}

	@Test
	void toData_withNullMeasurementsList_shouldReturnEmptyMeasurementSeries() {
		final var parameters = createParameters();

		final var result = toData(parameters, null);

		assertThat(result.getMeasurementSeries()).isEmpty();
	}

	@Test
	void toData_withSingleMeasurement_shouldReturnOneSeries() {
		final var parameters = createParameters();
		final var measurement = new Measurement()
			.feedType("Energy")
			.unit("kWh")
			.facilityId("facility1")
			.usage(BigDecimal.valueOf(100))
			.dateAndTime(now());

		final var result = toData(parameters, List.of(measurement));

		assertThat(result.getMeasurementSeries()).hasSize(1);
		assertThat(result.getMeasurementSeries().getFirst().getFacilityId()).isEqualTo("facility1");
	}

	@Test
	void toData_withSameFeedTypeAndUnitAndFacilityId_shouldGroupIntoOneSeries() {
		final var parameters = createParameters();
		final var measurement1 = new Measurement()
			.feedType("Energy")
			.unit("kWh")
			.facilityId("facility1")
			.usage(BigDecimal.valueOf(100))
			.dateAndTime(now());
		final var measurement2 = new Measurement()
			.feedType("Energy")
			.unit("kWh")
			.facilityId("facility1")
			.usage(BigDecimal.valueOf(200))
			.dateAndTime(now().plusHours(1));

		final var result = toData(parameters, List.of(measurement1, measurement2));

		assertThat(result.getMeasurementSeries()).hasSize(1);
		assertThat(result.getMeasurementSeries().getFirst().getMeasurementPoints()).hasSize(2);
		assertThat(result.getMeasurementSeries().getFirst().getFacilityId()).isEqualTo("facility1");
	}

	@Test
	void toData_withSameFeedTypeAndUnitButDifferentFacilityId_shouldCreateSeparateSeries() {
		final var parameters = createParameters();
		final var measurement1 = new Measurement()
			.feedType("Energy")
			.unit("kWh")
			.facilityId("facility1")
			.usage(BigDecimal.valueOf(100))
			.dateAndTime(now());
		final var measurement2 = new Measurement()
			.feedType("Energy")
			.unit("kWh")
			.facilityId("facility2")
			.usage(BigDecimal.valueOf(200))
			.dateAndTime(now());

		final var result = toData(parameters, List.of(measurement1, measurement2));

		assertThat(result.getMeasurementSeries()).hasSize(2);
		assertThat(result.getMeasurementSeries())
			.extracting("facilityId")
			.containsExactlyInAnyOrder("facility1", "facility2");
	}

	@Test
	void toData_withAggregatedFeedType_shouldNotSetFacilityId() {
		final var parameters = createParameters();
		final var measurement = new Measurement()
			.feedType("energy_aggregated")
			.unit("kWh")
			.facilityId("facility1,facility2")
			.usage(BigDecimal.valueOf(300))
			.dateAndTime(now());

		final var result = toData(parameters, List.of(measurement));

		assertThat(result.getMeasurementSeries()).hasSize(1);
		assertThat(result.getMeasurementSeries().getFirst().getFacilityId()).isNull();
		assertThat(result.getMeasurementSeries().getFirst().getMeasurementType()).isEqualTo("energy_aggregated");
	}

	@Test
	void toData_withMixedAggregatedAndNonAggregated_shouldHandleCorrectly() {
		final var parameters = createParameters();
		final var nonAggregated1 = new Measurement()
			.feedType("energy")
			.unit("kWh")
			.facilityId("facility1")
			.usage(BigDecimal.valueOf(100))
			.dateAndTime(now());
		final var nonAggregated2 = new Measurement()
			.feedType("energy")
			.unit("kWh")
			.facilityId("facility2")
			.usage(BigDecimal.valueOf(200))
			.dateAndTime(now());
		final var aggregated = new Measurement()
			.feedType("energy_aggregated")
			.unit("kWh")
			.facilityId("facility1,facility2")
			.usage(BigDecimal.valueOf(300))
			.dateAndTime(now());

		final var result = toData(parameters, List.of(nonAggregated1, nonAggregated2, aggregated));

		assertThat(result.getMeasurementSeries()).hasSize(3);
		assertThat(result.getMeasurementSeries())
			.filteredOn(s -> s.getFacilityId() != null)
			.extracting("facilityId")
			.containsExactlyInAnyOrder("facility1", "facility2");
		assertThat(result.getMeasurementSeries())
			.filteredOn(s -> s.getFacilityId() == null)
			.extracting("measurementType")
			.containsExactly("energy_aggregated");
	}

	@Test
	void toData_withDifferentFeedTypes_shouldCreateSeparateSeries() {
		final var parameters = createParameters();
		final var measurement1 = new Measurement()
			.feedType("Energy")
			.unit("kWh")
			.usage(BigDecimal.valueOf(100))
			.dateAndTime(now());
		final var measurement2 = new Measurement()
			.feedType("Power")
			.unit("kWh")
			.usage(BigDecimal.valueOf(200))
			.dateAndTime(now());

		final var result = toData(parameters, List.of(measurement1, measurement2));

		assertThat(result.getMeasurementSeries()).hasSize(2);
		assertThat(result.getMeasurementSeries())
			.extracting("measurementType")
			.containsExactlyInAnyOrder("Energy", "Power");
	}

	@Test
	void toData_withDifferentUnits_shouldCreateSeparateSeries() {
		final var parameters = createParameters();
		final var measurement1 = new Measurement()
			.feedType("Energy")
			.unit("kWh")
			.usage(BigDecimal.valueOf(100))
			.dateAndTime(now());
		final var measurement2 = new Measurement()
			.feedType("Energy")
			.unit("MWh")
			.usage(BigDecimal.valueOf(200))
			.dateAndTime(now());

		final var result = toData(parameters, List.of(measurement1, measurement2));

		assertThat(result.getMeasurementSeries()).hasSize(2);
		assertThat(result.getMeasurementSeries())
			.extracting("unit")
			.containsExactlyInAnyOrder("kWh", "MWh");
	}

	@Test
	void toData_withMixedFeedTypesAndUnits_shouldGroupCorrectly() {
		final var parameters = createParameters();
		final var energyKwh1 = new Measurement().feedType("Energy").unit("kWh").usage(BigDecimal.valueOf(100)).dateAndTime(now());
		final var energyKwh2 = new Measurement().feedType("Energy").unit("kWh").usage(BigDecimal.valueOf(150)).dateAndTime(now().plusHours(1));
		final var energyMwh = new Measurement().feedType("Energy").unit("MWh").usage(BigDecimal.valueOf(1)).dateAndTime(now());
		final var powerKw = new Measurement().feedType("Power").unit("kW").usage(BigDecimal.valueOf(50)).dateAndTime(now());

		final var result = toData(parameters, List.of(energyKwh1, energyKwh2, energyMwh, powerKw));

		assertThat(result.getMeasurementSeries()).hasSize(3);
		assertThat(result.getMeasurementSeries())
			.extracting("measurementType", "unit")
			.containsExactlyInAnyOrder(
				tuple("Energy", "kWh"),
				tuple("Energy", "MWh"),
				tuple("Power", "kW"));

		final var energyKwhSeries = result.getMeasurementSeries().stream()
			.filter(s -> "Energy".equals(s.getMeasurementType()) && "kWh".equals(s.getUnit()))
			.findFirst()
			.orElseThrow();
		assertThat(energyKwhSeries.getMeasurementPoints()).hasSize(2);
	}

	@Test
	void toData_shouldMapTimestampCorrectly() {
		final var parameters = createParameters();
		final var timestamp = now();
		final var measurement = new Measurement()
			.feedType("Energy")
			.unit("kWh")
			.usage(BigDecimal.valueOf(100))
			.dateAndTime(timestamp);

		final var result = toData(parameters, List.of(measurement));

		assertThat(result.getMeasurementSeries().getFirst().getMeasurementPoints().getFirst().getTimestamp())
			.isEqualTo(timestamp);
	}

	@Test
	void toData_shouldMapValueCorrectly() {
		final var parameters = createParameters();
		final var usage = BigDecimal.valueOf(123.456);
		final var measurement = new Measurement()
			.feedType("Energy")
			.unit("kWh")
			.usage(usage)
			.dateAndTime(now());

		final var result = toData(parameters, List.of(measurement));

		assertThat(result.getMeasurementSeries().getFirst().getMeasurementPoints().getFirst().getValue())
			.isEqualTo(usage);
	}

	@Test
	void toData_shouldMapMeasurementTypeFromFeedType() {
		final var parameters = createParameters();
		final var feedType = "CustomFeedType";
		final var measurement = new Measurement()
			.feedType(feedType)
			.unit("kWh")
			.usage(BigDecimal.valueOf(100))
			.dateAndTime(now());

		final var result = toData(parameters, List.of(measurement));

		assertThat(result.getMeasurementSeries().getFirst().getMeasurementType()).isEqualTo(feedType);
	}

	@Test
	void toData_shouldMapUnitCorrectly() {
		final var parameters = createParameters();
		final var unit = "m3";
		final var measurement = new Measurement()
			.feedType("Volume")
			.unit(unit)
			.usage(BigDecimal.valueOf(100))
			.dateAndTime(now());

		final var result = toData(parameters, List.of(measurement));

		assertThat(result.getMeasurementSeries().getFirst().getUnit()).isEqualTo(unit);
	}

	@Test
	void toData_withPositiveInterpolation_shouldCreateMetaData() {
		final var parameters = createParameters();
		final var measurement = new Measurement()
			.feedType("Energy")
			.unit("kWh")
			.usage(BigDecimal.valueOf(100))
			.dateAndTime(now())
			.interpolation(5);

		final var result = toData(parameters, List.of(measurement));

		final var metaData = result.getMeasurementSeries().getFirst().getMeasurementPoints().getFirst().getMetaData();
		assertThat(metaData).isNotNull().hasSize(1);
		assertThat(metaData.getFirst().getKey()).isEqualTo("interpolation");
		assertThat(metaData.getFirst().getValue()).isEqualTo("5");
	}

	@Test
	void toData_withZeroInterpolation_shouldReturnNullMetaData() {
		final var parameters = createParameters();
		final var measurement = new Measurement()
			.feedType("Energy")
			.unit("kWh")
			.usage(BigDecimal.valueOf(100))
			.dateAndTime(now())
			.interpolation(0);

		final var result = toData(parameters, List.of(measurement));

		assertThat(result.getMeasurementSeries().getFirst().getMeasurementPoints().getFirst().getMetaData()).isNull();
	}

	@Test
	void toData_withNullInterpolation_shouldReturnNullMetaData() {
		final var parameters = createParameters();
		final var measurement = new Measurement()
			.feedType("Energy")
			.unit("kWh")
			.usage(BigDecimal.valueOf(100))
			.dateAndTime(now())
			.interpolation(null);

		final var result = toData(parameters, List.of(measurement));

		assertThat(result.getMeasurementSeries().getFirst().getMeasurementPoints().getFirst().getMetaData()).isNull();
	}

	@Test
	void toData_withNegativeInterpolation_shouldReturnNullMetaData() {
		final var parameters = createParameters();
		final var measurement = new Measurement()
			.feedType("Energy")
			.unit("kWh")
			.usage(BigDecimal.valueOf(100))
			.dateAndTime(now())
			.interpolation(-1);

		final var result = toData(parameters, List.of(measurement));

		assertThat(result.getMeasurementSeries().getFirst().getMeasurementPoints().getFirst().getMetaData()).isNull();
	}

	@Test
	void toData_withNullValues_shouldHandleGracefully() {
		final var parameters = createParameters();
		final var measurement = new Measurement()
			.feedType(null)
			.unit(null)
			.usage(null)
			.dateAndTime(null)
			.interpolation(null);

		final var result = toData(parameters, List.of(measurement));

		assertThat(result.getMeasurementSeries()).hasSize(1);
		final var point = result.getMeasurementSeries().getFirst().getMeasurementPoints().getFirst();
		assertThat(point.getTimestamp()).isNull();
		assertThat(point.getValue()).isNull();
		assertThat(point.getMetaData()).isNull();
	}

}
