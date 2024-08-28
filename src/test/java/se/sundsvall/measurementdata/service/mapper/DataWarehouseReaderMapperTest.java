package se.sundsvall.measurementdata.service.mapper;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.dept44.test.annotation.resource.Load.ResourceType.JSON;
import static se.sundsvall.measurementdata.service.mapper.DataWarehouseReaderMapper.toAggregation;
import static se.sundsvall.measurementdata.service.mapper.DataWarehouseReaderMapper.toCategory;
import static se.sundsvall.measurementdata.service.mapper.DataWarehouseReaderMapper.toData;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import generated.se.sundsvall.datawarehousereader.Measurement;
import generated.se.sundsvall.datawarehousereader.MeasurementMetaData;
import generated.se.sundsvall.datawarehousereader.MeasurementResponse;
import generated.se.sundsvall.datawarehousereader.PagingAndSortingMetaData;
import se.sundsvall.dept44.test.annotation.resource.Load;
import se.sundsvall.dept44.test.extension.ResourceLoaderExtension;
import se.sundsvall.measurementdata.api.model.Aggregation;
import se.sundsvall.measurementdata.api.model.Category;
import se.sundsvall.measurementdata.api.model.Data;
import se.sundsvall.measurementdata.api.model.MeasurementDataSearchParameters;
import se.sundsvall.measurementdata.api.model.MeasurementPoint;
import se.sundsvall.measurementdata.api.model.MeasurementSerie;

@ExtendWith(ResourceLoaderExtension.class)
class DataWarehouseReaderMapperTest {

	private static final Aggregation AGGREGATION = Aggregation.YEAR;
	private static final Category CATEGORY = Category.DISTRICT_HEATING;
	private static final String FACILITY_ID = "facilityId";
	private static final OffsetDateTime FROM_DATE = now().with(firstDayOfYear());
	private static final String PARTY_ID = "partyId";
	private static final OffsetDateTime TO_DATE = now().with(firstDayOfYear()).plusMonths(1);
	private static final String MEASUREMENT_TYPE = "measurementType";
	private static final String UNIT = "unit";
	private static final String METADATA_KEY = "key";
	private static final String METADATA_VALUE = "value";
	private static final OffsetDateTime NOW = now();

	@ParameterizedTest
	@MethodSource("toAggregationsStreamArguments")
	void testToAggregation(Aggregation input, generated.se.sundsvall.datawarehousereader.Aggregation output) {
		assertThat(toAggregation(input)).isEqualTo(output);
	}

	@ParameterizedTest
	@MethodSource("toCategoriesStreamArguments")
	void testToCategory(Category input, generated.se.sundsvall.datawarehousereader.Category output) {
		assertThat(toCategory(input)).isEqualTo(output);
	}

	@Test
	void testToDataOnEmptyResponse() {
		final var data = toData(createParameters(), createDataWarehouseReaderResponse(0, 0, false));

		assertThat(data.getAggregateOn()).isEqualTo(AGGREGATION);
		assertThat(data.getCategory()).isEqualTo(CATEGORY);
		assertThat(data.getFacilityId()).isEqualTo(FACILITY_ID);
		assertThat(data.getFromDate()).isEqualTo(FROM_DATE);
		assertThat(data.getMeasurementSeries()).isEmpty();
		assertThat(data.getToDate()).isEqualTo(TO_DATE);
	}

	@Test
	void testToDataWithMetaData(@Load(value = "dataWarehouseReaderMapperTest/expectedJsonWithMetaData.json", as = JSON) Data expectedResult) {
		final var data = toData(createParameters(), createDataWarehouseReaderResponse(2, 5, true));

		assertThat(data).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedResult);
		assertThat(data.getFromDate()).isEqualTo(FROM_DATE);
		assertThat(data.getToDate()).isEqualTo(TO_DATE);
		assertThat(data.getMeasurementSeries())
			.flatExtracting(MeasurementSerie::getMeasurementPoints)
			.extracting(MeasurementPoint::getTimestamp)
			.allSatisfy(timeStamp -> assertThat(timeStamp).isEqualTo(NOW));
	}

	@Test
	void testToDataWithoutMetaData(@Load(value = "dataWarehouseReaderMapperTest/expectedJsonWithoutMetaData.json", as = JSON) Data expectedResult) {
		final var data = toData(createParameters(), createDataWarehouseReaderResponse(2, 4, false));

		assertThat(data).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedResult);
		assertThat(data.getFromDate()).isEqualTo(FROM_DATE);
		assertThat(data.getToDate()).isEqualTo(TO_DATE);
		assertThat(data.getMeasurementSeries())
			.flatExtracting(MeasurementSerie::getMeasurementPoints)
			.extracting(MeasurementPoint::getTimestamp)
			.allSatisfy(timeStamp -> assertThat(timeStamp).isEqualTo(NOW));
	}

	private static Stream<Arguments> toAggregationsStreamArguments() {
		return Stream.of(
			Arguments.of(null, null),
			Arguments.of(Aggregation.DAY, generated.se.sundsvall.datawarehousereader.Aggregation.DAY),
			Arguments.of(Aggregation.HOUR, generated.se.sundsvall.datawarehousereader.Aggregation.HOUR),
			Arguments.of(Aggregation.MONTH, generated.se.sundsvall.datawarehousereader.Aggregation.MONTH),
			Arguments.of(Aggregation.YEAR, generated.se.sundsvall.datawarehousereader.Aggregation.YEAR));
	}

	private static Stream<Arguments> toCategoriesStreamArguments() {
		return Stream.of(
			Arguments.of(null, null),
			Arguments.of(Category.COMMUNICATION, generated.se.sundsvall.datawarehousereader.Category.COMMUNICATION),
			Arguments.of(Category.DISTRICT_HEATING, generated.se.sundsvall.datawarehousereader.Category.DISTRICT_HEATING),
			Arguments.of(Category.ELECTRICITY, generated.se.sundsvall.datawarehousereader.Category.ELECTRICITY),
			Arguments.of(Category.WASTE_MANAGEMENT, generated.se.sundsvall.datawarehousereader.Category.WASTE_MANAGEMENT));
	}

	private static MeasurementDataSearchParameters createParameters() {
		return MeasurementDataSearchParameters.create()
			.withAggregateOn(AGGREGATION)
			.withCategory(CATEGORY)
			.withFacilityId(FACILITY_ID)
			.withFromDate(FROM_DATE)
			.withPartyId(PARTY_ID)
			.withToDate(TO_DATE);
	}

	private static MeasurementResponse createDataWarehouseReaderResponse(int series, int serieMembers, boolean hasMetadata) {
		final MeasurementResponse response = new MeasurementResponse().meta(new PagingAndSortingMetaData().count(series * serieMembers));
		for (int serie = 0; serie < series; serie++) {
			for (int serieMember = 0; serieMember < serieMembers; serieMember++) {
				response.addMeasurementsItem(createMeasurement(serie, serieMember, hasMetadata));
			}
		}
		return response;
	}

	private static Measurement createMeasurement(int serie, int serieMember, boolean hasMetadata) {
		return new Measurement()
			.interpolation((serie + 1) * serieMember)
			.measurementType(MEASUREMENT_TYPE + "_" + (serie + 1))
			.metaData(hasMetadata ? List.of(new MeasurementMetaData().key(METADATA_KEY).value(METADATA_VALUE + "_" + ((serie + 1) * (serieMember + 1)))) : null)
			.unit(UNIT)
			.timestamp(NOW)
			.value(BigDecimal.valueOf((serie + 1) * (serieMember + 1)));
	}
}
