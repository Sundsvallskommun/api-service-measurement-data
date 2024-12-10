package se.sundsvall.measurementdata.service;

import static java.net.URLDecoder.decode;
import static java.nio.charset.Charset.defaultCharset;
import static java.time.OffsetDateTime.now;
import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.firstDayOfNextMonth;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.sundsvall.measurementdata.api.model.Aggregation.MONTH;
import static se.sundsvall.measurementdata.api.model.Category.WASTE_MANAGEMENT;

import generated.se.sundsvall.datawarehousereader.Aggregation;
import generated.se.sundsvall.datawarehousereader.Category;
import generated.se.sundsvall.datawarehousereader.MeasurementResponse;
import generated.se.sundsvall.datawarehousereader.PagingAndSortingMetaData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.measurementdata.api.model.MeasurementDataSearchParameters;
import se.sundsvall.measurementdata.integration.datawarehousereader.DataWarehouseReaderClient;

@ExtendWith(MockitoExtension.class)
class MeasurementDataServiceTest {

	@Captor
	private ArgumentCaptor<String> fromDateCaptor;

	@Captor
	private ArgumentCaptor<String> toDateCaptor;

	@Mock
	private DataWarehouseReaderClient dataWarehouseReaderClientMock;

	@InjectMocks
	private MeasurementDataService service;

	@Test
	void testExistingCustomer() {

		// Arrange
		final var municipalityId = "municipalityId";
		final var aggregation = MONTH;
		final var category = WASTE_MANAGEMENT;
		final var facilityId = "facilityId";
		final var fromDate = now().with(firstDayOfMonth());
		final var partyId = "partyId";
		final var toDate = now().with(firstDayOfNextMonth());

		final var parameters = MeasurementDataSearchParameters.create()
			.withAggregateOn(aggregation)
			.withCategory(category)
			.withFacilityId(facilityId)
			.withFromDate(fromDate)
			.withPartyId(partyId)
			.withToDate(toDate);

		when(dataWarehouseReaderClientMock.getMeasurementData(any(), any(), any(), any(), any(), any(), any(), anyInt())).thenReturn(new MeasurementResponse().meta(new PagingAndSortingMetaData()));

		// Act
		final var response = service.fetchMeasurementData(municipalityId, parameters);

		// Assert
		verify(dataWarehouseReaderClientMock).getMeasurementData(eq(municipalityId), eq(Category.WASTE_MANAGEMENT), eq(Aggregation.MONTH), eq(partyId), eq(facilityId), fromDateCaptor.capture(), toDateCaptor.capture(), eq(1000));
		assertThat(decode(fromDateCaptor.getValue(), defaultCharset())).isEqualTo(fromDate.toString());
		assertThat(decode(toDateCaptor.getValue(), defaultCharset())).isEqualTo(toDate.toString());
		assertThat(response.getAggregateOn()).isEqualTo(aggregation);
		assertThat(response.getCategory()).isEqualTo(category);
		assertThat(response.getFacilityId()).isEqualTo(facilityId);
		assertThat(response.getFromDate()).isEqualTo(fromDate);
		assertThat(response.getMeasurementSeries()).isEmpty();
		assertThat(response.getToDate()).isEqualTo(toDate);
	}
}
