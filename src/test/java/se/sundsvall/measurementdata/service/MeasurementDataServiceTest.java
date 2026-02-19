package se.sundsvall.measurementdata.service;

import generated.se.sundsvall.datawarehousereader.Aggregation;
import generated.se.sundsvall.datawarehousereader.Category;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.measurementdata.api.model.MeasurementDataSearchParameters;
import se.sundsvall.measurementdata.integration.datawarehousereader.DataWarehouseReaderClient;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.sundsvall.measurementdata.api.model.Aggregation.DAY;
import static se.sundsvall.measurementdata.api.model.Aggregation.MONTH;
import static se.sundsvall.measurementdata.api.model.Aggregation.QUARTER;
import static se.sundsvall.measurementdata.api.model.Category.DISTRICT_HEATING;
import static se.sundsvall.measurementdata.api.model.Category.ELECTRICITY;
import static se.sundsvall.measurementdata.api.model.Category.WASTE_MANAGEMENT;

@ExtendWith(MockitoExtension.class)
class MeasurementDataServiceTest {

	@Mock
	private DataWarehouseReaderClient dataWarehouseReaderClientMock;

	@InjectMocks
	private MeasurementDataService service;

	@Test
	void testExistingCustomer() {
		final var municipalityId = "municipalityId";
		final var aggregation = MONTH;
		final var category = WASTE_MANAGEMENT;
		final var facilityId = "facilityId";
		final var fromDate = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var partyId = "partyId";
		final var toDate = OffsetDateTime.of(2024, 2, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var encodedFromDate = encode(fromDate.toString(), UTF_8);
		final var encodedToDate = encode(toDate.toString(), UTF_8);

		final var parameters = MeasurementDataSearchParameters.create()
			.withAggregateOn(aggregation)
			.withCategory(category)
			.withFacilityId(facilityId)
			.withFromDate(fromDate)
			.withPartyId(partyId)
			.withToDate(toDate);

		when(dataWarehouseReaderClientMock.getMeasurements(
			municipalityId,
			Category.WASTE_MANAGEMENT.name(),
			Aggregation.MONTH.name(),
			partyId,
			facilityId,
			encodedFromDate,
			encodedToDate)).thenReturn(List.of());

		final var response = service.fetchMeasurementData(municipalityId, parameters);

		verify(dataWarehouseReaderClientMock).getMeasurements(
			municipalityId,
			Category.WASTE_MANAGEMENT.name(),
			Aggregation.MONTH.name(),
			partyId,
			facilityId,
			encodedFromDate,
			encodedToDate);
		assertThat(response.getAggregateOn()).isEqualTo(aggregation);
		assertThat(response.getCategory()).isEqualTo(category);
		assertThat(response.getFacilityId()).isEqualTo(facilityId);
		assertThat(response.getFromDate()).isEqualTo(fromDate);
		assertThat(response.getMeasurementSeries()).isEmpty();
		assertThat(response.getToDate()).isEqualTo(toDate);
	}

	@Test
	void fetchMeasurementData_withNullFromDate_shouldPassNullToClient() {
		final var municipalityId = "municipalityId";
		final var facilityId = "facilityId";
		final var partyId = "partyId";
		final var toDate = OffsetDateTime.of(2024, 2, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var encodedToDate = encode(toDate.toString(), UTF_8);

		final var parameters = MeasurementDataSearchParameters.create()
			.withAggregateOn(MONTH)
			.withCategory(WASTE_MANAGEMENT)
			.withFacilityId(facilityId)
			.withFromDate(null)
			.withPartyId(partyId)
			.withToDate(toDate);

		when(dataWarehouseReaderClientMock.getMeasurements(
			municipalityId,
			Category.WASTE_MANAGEMENT.name(),
			Aggregation.MONTH.name(),
			partyId,
			facilityId,
			null,
			encodedToDate)).thenReturn(List.of());

		service.fetchMeasurementData(municipalityId, parameters);

		verify(dataWarehouseReaderClientMock).getMeasurements(
			municipalityId,
			Category.WASTE_MANAGEMENT.name(),
			Aggregation.MONTH.name(),
			partyId,
			facilityId,
			null,
			encodedToDate);
	}

	@Test
	void fetchMeasurementData_withNullToDate_shouldPassNullToClient() {
		final var municipalityId = "municipalityId";
		final var facilityId = "facilityId";
		final var partyId = "partyId";
		final var fromDate = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var encodedFromDate = encode(fromDate.toString(), UTF_8);

		final var parameters = MeasurementDataSearchParameters.create()
			.withAggregateOn(MONTH)
			.withCategory(WASTE_MANAGEMENT)
			.withFacilityId(facilityId)
			.withFromDate(fromDate)
			.withPartyId(partyId)
			.withToDate(null);

		when(dataWarehouseReaderClientMock.getMeasurements(
			municipalityId,
			Category.WASTE_MANAGEMENT.name(),
			Aggregation.MONTH.name(),
			partyId,
			facilityId,
			encodedFromDate,
			null)).thenReturn(List.of());

		service.fetchMeasurementData(municipalityId, parameters);

		verify(dataWarehouseReaderClientMock).getMeasurements(
			municipalityId,
			Category.WASTE_MANAGEMENT.name(),
			Aggregation.MONTH.name(),
			partyId,
			facilityId,
			encodedFromDate,
			null);
	}

	@Test
	void fetchMeasurementData_withDistrictHeating_shouldMapCorrectly() {
		final var municipalityId = "municipalityId";
		final var facilityId = "facilityId";
		final var partyId = "partyId";
		final var fromDate = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var toDate = OffsetDateTime.of(2024, 2, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var encodedFromDate = encode(fromDate.toString(), UTF_8);
		final var encodedToDate = encode(toDate.toString(), UTF_8);

		final var parameters = MeasurementDataSearchParameters.create()
			.withAggregateOn(MONTH)
			.withCategory(DISTRICT_HEATING)
			.withFacilityId(facilityId)
			.withFromDate(fromDate)
			.withPartyId(partyId)
			.withToDate(toDate);

		when(dataWarehouseReaderClientMock.getMeasurements(
			municipalityId,
			Category.DISTRICT_HEATING.name(),
			Aggregation.MONTH.name(),
			partyId,
			facilityId,
			encodedFromDate,
			encodedToDate)).thenReturn(List.of());

		final var response = service.fetchMeasurementData(municipalityId, parameters);

		verify(dataWarehouseReaderClientMock).getMeasurements(
			municipalityId,
			Category.DISTRICT_HEATING.name(),
			Aggregation.MONTH.name(),
			partyId,
			facilityId,
			encodedFromDate,
			encodedToDate);
		assertThat(response.getCategory()).isEqualTo(DISTRICT_HEATING);
	}

	@Test
	void fetchMeasurementData_withElectricity_shouldMapCorrectly() {
		final var municipalityId = "municipalityId";
		final var facilityId = "facilityId";
		final var partyId = "partyId";
		final var fromDate = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var toDate = OffsetDateTime.of(2024, 2, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var encodedFromDate = encode(fromDate.toString(), UTF_8);
		final var encodedToDate = encode(toDate.toString(), UTF_8);

		final var parameters = MeasurementDataSearchParameters.create()
			.withAggregateOn(MONTH)
			.withCategory(ELECTRICITY)
			.withFacilityId(facilityId)
			.withFromDate(fromDate)
			.withPartyId(partyId)
			.withToDate(toDate);

		when(dataWarehouseReaderClientMock.getMeasurements(
			municipalityId,
			Category.ELECTRICITY.name(),
			Aggregation.MONTH.name(),
			partyId,
			facilityId,
			encodedFromDate,
			encodedToDate)).thenReturn(List.of());

		final var response = service.fetchMeasurementData(municipalityId, parameters);

		verify(dataWarehouseReaderClientMock).getMeasurements(
			municipalityId,
			Category.ELECTRICITY.name(),
			Aggregation.MONTH.name(),
			partyId,
			facilityId,
			encodedFromDate,
			encodedToDate);
		assertThat(response.getCategory()).isEqualTo(ELECTRICITY);
	}

	@Test
	void fetchMeasurementData_withQuarterAggregation_shouldMapCorrectly() {
		final var municipalityId = "municipalityId";
		final var facilityId = "facilityId";
		final var partyId = "partyId";
		final var fromDate = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var toDate = OffsetDateTime.of(2024, 4, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var encodedFromDate = encode(fromDate.toString(), UTF_8);
		final var encodedToDate = encode(toDate.toString(), UTF_8);

		final var parameters = MeasurementDataSearchParameters.create()
			.withAggregateOn(QUARTER)
			.withCategory(WASTE_MANAGEMENT)
			.withFacilityId(facilityId)
			.withFromDate(fromDate)
			.withPartyId(partyId)
			.withToDate(toDate);

		when(dataWarehouseReaderClientMock.getMeasurements(
			municipalityId,
			Category.WASTE_MANAGEMENT.name(),
			Aggregation.QUARTER.name(),
			partyId,
			facilityId,
			encodedFromDate,
			encodedToDate)).thenReturn(List.of());

		final var response = service.fetchMeasurementData(municipalityId, parameters);

		verify(dataWarehouseReaderClientMock).getMeasurements(
			municipalityId,
			Category.WASTE_MANAGEMENT.name(),
			Aggregation.QUARTER.name(),
			partyId,
			facilityId,
			encodedFromDate,
			encodedToDate);
		assertThat(response.getAggregateOn()).isEqualTo(QUARTER);
	}

	@Test
	void fetchMeasurementData_withDayAggregation_shouldMapCorrectly() {
		final var municipalityId = "municipalityId";
		final var facilityId = "facilityId";
		final var partyId = "partyId";
		final var fromDate = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var toDate = OffsetDateTime.of(2024, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC);
		final var encodedFromDate = encode(fromDate.toString(), UTF_8);
		final var encodedToDate = encode(toDate.toString(), UTF_8);

		final var parameters = MeasurementDataSearchParameters.create()
			.withAggregateOn(DAY)
			.withCategory(WASTE_MANAGEMENT)
			.withFacilityId(facilityId)
			.withFromDate(fromDate)
			.withPartyId(partyId)
			.withToDate(toDate);

		when(dataWarehouseReaderClientMock.getMeasurements(
			municipalityId,
			Category.WASTE_MANAGEMENT.name(),
			Aggregation.DAY.name(),
			partyId,
			facilityId,
			encodedFromDate,
			encodedToDate)).thenReturn(List.of());

		final var response = service.fetchMeasurementData(municipalityId, parameters);

		verify(dataWarehouseReaderClientMock).getMeasurements(
			municipalityId,
			Category.WASTE_MANAGEMENT.name(),
			Aggregation.DAY.name(),
			partyId,
			facilityId,
			encodedFromDate,
			encodedToDate);
		assertThat(response.getAggregateOn()).isEqualTo(DAY);
	}
}
