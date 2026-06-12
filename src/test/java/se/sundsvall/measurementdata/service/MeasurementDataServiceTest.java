package se.sundsvall.measurementdata.service;

import generated.se.sundsvall.datawarehousereader.Aggregation;
import generated.se.sundsvall.datawarehousereader.Category;
import generated.se.sundsvall.datawarehousereader.Measurement;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.measurementdata.api.model.Display;
import se.sundsvall.measurementdata.api.model.MeasurementDataSearchParameters;
import se.sundsvall.measurementdata.integration.bfus.BfusIntegration;
import se.sundsvall.measurementdata.integration.bfus.configuration.BfusProperties;
import se.sundsvall.measurementdata.integration.datawarehousereader.DataWarehouseReaderClient;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Month.JANUARY;
import static java.time.Month.JULY;
import static java.time.Month.OCTOBER;
import static java.time.Month.SEPTEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.measurementdata.api.model.Aggregation.DAY;
import static se.sundsvall.measurementdata.api.model.Aggregation.HOUR;
import static se.sundsvall.measurementdata.api.model.Aggregation.MONTH;
import static se.sundsvall.measurementdata.api.model.Aggregation.QUARTER;
import static se.sundsvall.measurementdata.api.model.Category.DISTRICT_COOLING;
import static se.sundsvall.measurementdata.api.model.Category.DISTRICT_HEATING;
import static se.sundsvall.measurementdata.api.model.Category.ELECTRICITY;
import static se.sundsvall.measurementdata.api.model.Category.WASTE_MANAGEMENT;
import static se.sundsvall.measurementdata.api.model.Display.AGGREGATE;
import static se.sundsvall.measurementdata.api.model.Display.ONLYAGGREGATED;

@ExtendWith(MockitoExtension.class)
class MeasurementDataServiceTest {

	@Mock
	private DataWarehouseReaderClient dataWarehouseReaderClientMock;

	@Mock
	private BfusIntegration bfusIntegrationMock;

	@Mock
	private BfusProperties bfusPropertiesMock;

	@InjectMocks
	private MeasurementDataService service;

	@Test
	void testExistingCustomer() {
		final var municipalityId = "municipalityId";
		final var aggregation = MONTH;
		final var category = WASTE_MANAGEMENT;
		final var facilityId = List.of("facilityId");
		final var fromDate = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var partyId = "partyId";
		final var toDate = OffsetDateTime.of(2024, 2, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var encodedFromDate = encode(fromDate.toString(), UTF_8);
		final var encodedToDate = encode(toDate.toString(), UTF_8);

		final var parameters = MeasurementDataSearchParameters.create()
			.withAggregateOn(aggregation)
			.withCategory(category)
			.withFacilityIds(facilityId)
			.withFromDate(fromDate)
			.withPartyId(partyId)
			.withToDate(toDate)
			.withDisplay(AGGREGATE);

		when(dataWarehouseReaderClientMock.getMeasurements(
			municipalityId,
			Category.WASTE_MANAGEMENT.name(),
			Aggregation.MONTH.name(),
			partyId,
			facilityId,
			encodedFromDate,
			encodedToDate,
			AGGREGATE.name())).thenReturn(List.of());

		final var response = service.fetchMeasurementData(municipalityId, parameters);

		verify(dataWarehouseReaderClientMock).getMeasurements(
			municipalityId,
			Category.WASTE_MANAGEMENT.name(),
			Aggregation.MONTH.name(),
			partyId,
			facilityId,
			encodedFromDate,
			encodedToDate,
			AGGREGATE.name());
		assertThat(response.getAggregateOn()).isEqualTo(aggregation);
		assertThat(response.getCategory()).isEqualTo(category);
		assertThat(response.getFacilityIds()).isEqualTo(facilityId);
		assertThat(response.getFromDate()).isEqualTo(fromDate);
		assertThat(response.getMeasurementSeries()).isEmpty();
		assertThat(response.getToDate()).isEqualTo(toDate);
	}

	@Test
	void fetchMeasurementData_withNullFromDate_shouldPassNullToClient() {
		final var municipalityId = "municipalityId";
		final var facilityId = List.of("facilityId");
		final var partyId = "partyId";
		final var toDate = OffsetDateTime.of(2024, 2, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var encodedToDate = encode(toDate.toString(), UTF_8);

		final var parameters = MeasurementDataSearchParameters.create()
			.withAggregateOn(MONTH)
			.withCategory(WASTE_MANAGEMENT)
			.withFacilityIds(facilityId)
			.withFromDate(null)
			.withPartyId(partyId)
			.withToDate(toDate)
			.withDisplay(AGGREGATE);

		when(dataWarehouseReaderClientMock.getMeasurements(
			municipalityId,
			Category.WASTE_MANAGEMENT.name(),
			Aggregation.MONTH.name(),
			partyId,
			facilityId,
			null,
			encodedToDate,
			AGGREGATE.name())).thenReturn(List.of());

		service.fetchMeasurementData(municipalityId, parameters);

		verify(dataWarehouseReaderClientMock).getMeasurements(
			municipalityId,
			Category.WASTE_MANAGEMENT.name(),
			Aggregation.MONTH.name(),
			partyId,
			facilityId,
			null,
			encodedToDate,
			AGGREGATE.name());
	}

	@Test
	void fetchMeasurementData_withNullToDate_shouldPassNullToClient() {
		final var municipalityId = "municipalityId";
		final var facilityId = List.of("facilityId");
		final var partyId = "partyId";
		final var fromDate = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var encodedFromDate = encode(fromDate.toString(), UTF_8);

		final var parameters = MeasurementDataSearchParameters.create()
			.withAggregateOn(MONTH)
			.withCategory(WASTE_MANAGEMENT)
			.withFacilityIds(facilityId)
			.withFromDate(fromDate)
			.withPartyId(partyId)
			.withToDate(null)
			.withDisplay(Display.ONLYAGGREGATED);

		when(dataWarehouseReaderClientMock.getMeasurements(
			municipalityId,
			Category.WASTE_MANAGEMENT.name(),
			Aggregation.MONTH.name(),
			partyId,
			facilityId,
			encodedFromDate,
			null,
			ONLYAGGREGATED.name())).thenReturn(List.of());

		service.fetchMeasurementData(municipalityId, parameters);

		verify(dataWarehouseReaderClientMock).getMeasurements(
			municipalityId,
			Category.WASTE_MANAGEMENT.name(),
			Aggregation.MONTH.name(),
			partyId,
			facilityId,
			encodedFromDate,
			null,
			ONLYAGGREGATED.name());
	}

	@Test
	void fetchMeasurementData_withDistrictCooling_shouldMapCorrectly() {
		final var municipalityId = "municipalityId";
		final var facilityId = List.of("facilityId");
		final var partyId = "partyId";
		final var fromDate = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var toDate = OffsetDateTime.of(2024, 2, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var encodedFromDate = encode(fromDate.toString(), UTF_8);
		final var encodedToDate = encode(toDate.toString(), UTF_8);

		final var parameters = MeasurementDataSearchParameters.create()
			.withAggregateOn(MONTH)
			.withCategory(DISTRICT_COOLING)
			.withFacilityIds(facilityId)
			.withFromDate(fromDate)
			.withPartyId(partyId)
			.withToDate(toDate)
			.withDisplay(ONLYAGGREGATED);

		when(dataWarehouseReaderClientMock.getMeasurements(
			municipalityId,
			Category.DISTRICT_COOLING.name(),
			Aggregation.MONTH.name(),
			partyId,
			facilityId,
			encodedFromDate,
			encodedToDate,
			ONLYAGGREGATED.name())).thenReturn(List.of());

		final var response = service.fetchMeasurementData(municipalityId, parameters);

		verify(dataWarehouseReaderClientMock).getMeasurements(
			municipalityId,
			Category.DISTRICT_COOLING.name(),
			Aggregation.MONTH.name(),
			partyId,
			facilityId,
			encodedFromDate,
			encodedToDate,
			ONLYAGGREGATED.name());
		assertThat(response.getCategory()).isEqualTo(DISTRICT_COOLING);
	}

	@Test
	void fetchMeasurementData_withDistrictHeating_shouldMapCorrectly() {
		final var municipalityId = "municipalityId";
		final var facilityId = List.of("facilityId");
		final var partyId = "partyId";
		final var fromDate = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var toDate = OffsetDateTime.of(2024, 2, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var encodedFromDate = encode(fromDate.toString(), UTF_8);
		final var encodedToDate = encode(toDate.toString(), UTF_8);

		final var parameters = MeasurementDataSearchParameters.create()
			.withAggregateOn(MONTH)
			.withCategory(DISTRICT_HEATING)
			.withFacilityIds(facilityId)
			.withFromDate(fromDate)
			.withPartyId(partyId)
			.withToDate(toDate)
			.withDisplay(ONLYAGGREGATED);

		when(dataWarehouseReaderClientMock.getMeasurements(
			municipalityId,
			Category.DISTRICT_HEATING.name(),
			Aggregation.MONTH.name(),
			partyId,
			facilityId,
			encodedFromDate,
			encodedToDate,
			ONLYAGGREGATED.name())).thenReturn(List.of());

		final var response = service.fetchMeasurementData(municipalityId, parameters);

		verify(dataWarehouseReaderClientMock).getMeasurements(
			municipalityId,
			Category.DISTRICT_HEATING.name(),
			Aggregation.MONTH.name(),
			partyId,
			facilityId,
			encodedFromDate,
			encodedToDate,
			ONLYAGGREGATED.name());
		assertThat(response.getCategory()).isEqualTo(DISTRICT_HEATING);
	}

	@Test
	void fetchMeasurementData_withElectricity_shouldMapCorrectly() {
		final var municipalityId = "municipalityId";
		final var facilityId = List.of("facilityId");
		final var partyId = "partyId";
		final var fromDate = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var toDate = OffsetDateTime.of(2024, 2, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var encodedFromDate = encode(fromDate.toString(), UTF_8);
		final var encodedToDate = encode(toDate.toString(), UTF_8);

		final var parameters = MeasurementDataSearchParameters.create()
			.withAggregateOn(MONTH)
			.withCategory(ELECTRICITY)
			.withFacilityIds(facilityId)
			.withFromDate(fromDate)
			.withPartyId(partyId)
			.withToDate(toDate)
			.withDisplay(AGGREGATE);

		when(dataWarehouseReaderClientMock.getMeasurements(
			municipalityId,
			Category.ELECTRICITY.name(),
			Aggregation.MONTH.name(),
			partyId,
			facilityId,
			encodedFromDate,
			encodedToDate,
			AGGREGATE.name())).thenReturn(List.of());

		final var response = service.fetchMeasurementData(municipalityId, parameters);

		verify(dataWarehouseReaderClientMock).getMeasurements(
			municipalityId,
			Category.ELECTRICITY.name(),
			Aggregation.MONTH.name(),
			partyId,
			facilityId,
			encodedFromDate,
			encodedToDate,
			AGGREGATE.name());
		assertThat(response.getCategory()).isEqualTo(ELECTRICITY);
	}

	@Test
	void fetchMeasurementData_withQuarterAggregation_shouldMapCorrectly() {
		final var municipalityId = "municipalityId";
		final var facilityId = List.of("facilityId");
		final var partyId = "partyId";
		final var fromDate = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var toDate = OffsetDateTime.of(2024, 4, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var encodedFromDate = encode(fromDate.toString(), UTF_8);
		final var encodedToDate = encode(toDate.toString(), UTF_8);

		final var parameters = MeasurementDataSearchParameters.create()
			.withAggregateOn(QUARTER)
			.withCategory(WASTE_MANAGEMENT)
			.withFacilityIds(facilityId)
			.withFromDate(fromDate)
			.withPartyId(partyId)
			.withToDate(toDate)
			.withDisplay(AGGREGATE);

		when(dataWarehouseReaderClientMock.getMeasurements(
			municipalityId,
			Category.WASTE_MANAGEMENT.name(),
			Aggregation.QUARTER.name(),
			partyId,
			facilityId,
			encodedFromDate,
			encodedToDate,
			AGGREGATE.name())).thenReturn(List.of());

		final var response = service.fetchMeasurementData(municipalityId, parameters);

		verify(dataWarehouseReaderClientMock).getMeasurements(
			municipalityId,
			Category.WASTE_MANAGEMENT.name(),
			Aggregation.QUARTER.name(),
			partyId,
			facilityId,
			encodedFromDate,
			encodedToDate,
			AGGREGATE.name());
		assertThat(response.getAggregateOn()).isEqualTo(QUARTER);
	}

	@Test
	void fetchMeasurementData_withDayAggregation_shouldMapCorrectly() {
		final var municipalityId = "municipalityId";
		final var facilityId = List.of("facilityId");
		final var partyId = "partyId";
		final var fromDate = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var toDate = OffsetDateTime.of(2024, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC);
		final var encodedFromDate = encode(fromDate.toString(), UTF_8);
		final var encodedToDate = encode(toDate.toString(), UTF_8);

		final var parameters = MeasurementDataSearchParameters.create()
			.withAggregateOn(DAY)
			.withCategory(WASTE_MANAGEMENT)
			.withFacilityIds(facilityId)
			.withFromDate(fromDate)
			.withPartyId(partyId)
			.withToDate(toDate)
			.withDisplay(AGGREGATE);

		when(dataWarehouseReaderClientMock.getMeasurements(
			municipalityId,
			Category.WASTE_MANAGEMENT.name(),
			Aggregation.DAY.name(),
			partyId,
			facilityId,
			encodedFromDate,
			encodedToDate,
			AGGREGATE.name())).thenReturn(List.of());

		final var response = service.fetchMeasurementData(municipalityId, parameters);

		verify(dataWarehouseReaderClientMock).getMeasurements(
			municipalityId,
			Category.WASTE_MANAGEMENT.name(),
			Aggregation.DAY.name(),
			partyId,
			facilityId,
			encodedFromDate,
			encodedToDate,
			AGGREGATE.name());
		assertThat(response.getAggregateOn()).isEqualTo(DAY);
	}

	@Test
	void fetchMeasurementData_withDisplay_shouldPassDisplayToClient() {
		final var municipalityId = "municipalityId";
		final var facilityId = List.of("facilityId");
		final var partyId = "partyId";
		final var fromDate = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var toDate = OffsetDateTime.of(2024, 2, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var encodedFromDate = encode(fromDate.toString(), UTF_8);
		final var encodedToDate = encode(toDate.toString(), UTF_8);

		final var parameters = MeasurementDataSearchParameters.create()
			.withAggregateOn(MONTH)
			.withCategory(WASTE_MANAGEMENT)
			.withFacilityIds(facilityId)
			.withFromDate(fromDate)
			.withPartyId(partyId)
			.withToDate(toDate)
			.withDisplay(AGGREGATE);

		when(dataWarehouseReaderClientMock.getMeasurements(
			municipalityId,
			Category.WASTE_MANAGEMENT.name(),
			Aggregation.MONTH.name(),
			partyId,
			facilityId,
			encodedFromDate,
			encodedToDate,
			AGGREGATE.name())).thenReturn(List.of());

		service.fetchMeasurementData(municipalityId, parameters);

		verify(dataWarehouseReaderClientMock).getMeasurements(
			municipalityId,
			Category.WASTE_MANAGEMENT.name(),
			Aggregation.MONTH.name(),
			partyId,
			facilityId,
			encodedFromDate,
			encodedToDate,
			AGGREGATE.name());
	}

	@Test
	void fetchMeasurementData_electricityQuarterBeforeCutoff_usesBfusOnly() {
		final var municipalityId = "2281";
		final var facilityIds = List.of("f1");
		final var partyId = "partyId";
		final var fromDate = OffsetDateTime.of(2025, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var toDate = OffsetDateTime.of(2025, 6, 30, 0, 0, 0, 0, ZoneOffset.UTC);

		when(bfusPropertiesMock.cutoffDate()).thenReturn(LocalDate.of(2025, OCTOBER, 21));
		when(bfusIntegrationMock.getElectricityConsumption(facilityIds, LocalDate.of(2025, JANUARY, 1), LocalDate.of(2025, JULY, 1)))
			.thenReturn(List.of(new Measurement().facilityId("f1").feedType("Energy").unit("kWh").usage(BigDecimal.ONE).dateAndTime(fromDate)));

		final var parameters = MeasurementDataSearchParameters.create()
			.withAggregateOn(QUARTER)
			.withCategory(ELECTRICITY)
			.withFacilityIds(facilityIds)
			.withFromDate(fromDate)
			.withToDate(toDate)
			.withPartyId(partyId);

		final var response = service.fetchMeasurementData(municipalityId, parameters);

		assertThat(response.getCategory()).isEqualTo(ELECTRICITY);
		assertThat(response.getMeasurementSeries()).hasSize(1);
		assertThat(response.getMeasurementSeries().getFirst().getFacilityId()).isEqualTo("f1");
		verify(bfusIntegrationMock).getElectricityConsumption(facilityIds, LocalDate.of(2025, JANUARY, 1), LocalDate.of(2025, JULY, 1));
		verifyNoInteractions(dataWarehouseReaderClientMock);
	}

	@Test
	void fetchMeasurementData_electricityQuarterSpanningCutoff_mergesBfusAndDataWarehouseReader() {
		final var municipalityId = "2281";
		final var facilityIds = List.of("f1");
		final var partyId = "partyId";
		final var fromDate = OffsetDateTime.of(2025, 9, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var toDate = OffsetDateTime.of(2025, 12, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var bfusPoint = OffsetDateTime.of(2025, 9, 1, 0, 0, 0, 0, ZoneOffset.ofHours(2));
		final var dwrPoint = OffsetDateTime.of(2025, 11, 1, 0, 0, 0, 0, ZoneOffset.ofHours(1));

		when(bfusPropertiesMock.cutoffDate()).thenReturn(LocalDate.of(2025, OCTOBER, 21));
		when(bfusIntegrationMock.getElectricityConsumption(facilityIds, LocalDate.of(2025, SEPTEMBER, 1), LocalDate.of(2025, OCTOBER, 21)))
			.thenReturn(List.of(new Measurement().facilityId("f1").feedType("Energy").unit("kWh").usage(BigDecimal.ONE).dateAndTime(bfusPoint)));
		when(dataWarehouseReaderClientMock.getMeasurements(eq(municipalityId), eq("ELECTRICITY"), eq("QUARTER"), eq(partyId), eq(facilityIds), anyString(), anyString(), isNull()))
			.thenReturn(List.of(new Measurement().facilityId("f1").feedType("Energy").unit("kWh").usage(BigDecimal.TEN).dateAndTime(dwrPoint)));

		final var parameters = MeasurementDataSearchParameters.create()
			.withAggregateOn(QUARTER)
			.withCategory(ELECTRICITY)
			.withFacilityIds(facilityIds)
			.withFromDate(fromDate)
			.withToDate(toDate)
			.withPartyId(partyId);

		final var response = service.fetchMeasurementData(municipalityId, parameters);

		assertThat(response.getMeasurementSeries()).hasSize(1);
		assertThat(response.getMeasurementSeries().getFirst().getFacilityId()).isEqualTo("f1");
		assertThat(response.getMeasurementSeries().getFirst().getMeasurementPoints()).hasSize(2);
		verify(bfusIntegrationMock).getElectricityConsumption(facilityIds, LocalDate.of(2025, SEPTEMBER, 1), LocalDate.of(2025, OCTOBER, 21));
		verify(dataWarehouseReaderClientMock).getMeasurements(eq(municipalityId), eq("ELECTRICITY"), eq("QUARTER"), eq(partyId), eq(facilityIds), anyString(), anyString(), isNull());
	}

	@Test
	void fetchMeasurementData_electricityQuarterAfterCutoff_usesDataWarehouseReader() {
		final var municipalityId = "2281";
		final var facilityIds = List.of("f1");
		final var partyId = "partyId";
		final var fromDate = OffsetDateTime.of(2025, 11, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var toDate = OffsetDateTime.of(2025, 12, 1, 0, 0, 0, 0, ZoneOffset.UTC);

		when(bfusPropertiesMock.cutoffDate()).thenReturn(LocalDate.of(2025, OCTOBER, 21));
		when(dataWarehouseReaderClientMock.getMeasurements(eq(municipalityId), eq("ELECTRICITY"), eq("QUARTER"), eq(partyId), eq(facilityIds), anyString(), anyString(), isNull()))
			.thenReturn(List.of());

		final var parameters = MeasurementDataSearchParameters.create()
			.withAggregateOn(QUARTER)
			.withCategory(ELECTRICITY)
			.withFacilityIds(facilityIds)
			.withFromDate(fromDate)
			.withToDate(toDate)
			.withPartyId(partyId);

		service.fetchMeasurementData(municipalityId, parameters);

		verify(dataWarehouseReaderClientMock).getMeasurements(eq(municipalityId), eq("ELECTRICITY"), eq("QUARTER"), eq(partyId), eq(facilityIds), anyString(), anyString(), isNull());
		verifyNoInteractions(bfusIntegrationMock);
	}

	@Test
	void fetchMeasurementData_electricityHourBeforeCutoff_usesDataWarehouseReader() {
		final var municipalityId = "2281";
		final var facilityIds = List.of("f1");
		final var partyId = "partyId";
		final var fromDate = OffsetDateTime.of(2025, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		final var toDate = OffsetDateTime.of(2025, 6, 30, 0, 0, 0, 0, ZoneOffset.UTC);

		when(dataWarehouseReaderClientMock.getMeasurements(eq(municipalityId), eq("ELECTRICITY"), eq("HOUR"), eq(partyId), eq(facilityIds), anyString(), anyString(), isNull()))
			.thenReturn(List.of());

		final var parameters = MeasurementDataSearchParameters.create()
			.withAggregateOn(HOUR)
			.withCategory(ELECTRICITY)
			.withFacilityIds(facilityIds)
			.withFromDate(fromDate)
			.withToDate(toDate)
			.withPartyId(partyId);

		service.fetchMeasurementData(municipalityId, parameters);

		verify(dataWarehouseReaderClientMock).getMeasurements(eq(municipalityId), eq("ELECTRICITY"), eq("HOUR"), eq(partyId), eq(facilityIds), anyString(), anyString(), isNull());
		verifyNoInteractions(bfusIntegrationMock);
	}
}
