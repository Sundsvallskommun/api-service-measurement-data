package se.sundsvall.measurementdata.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import se.sundsvall.measurementdata.Application;
import se.sundsvall.measurementdata.api.model.Aggregation;
import se.sundsvall.measurementdata.api.model.Category;
import se.sundsvall.measurementdata.api.model.Data;
import se.sundsvall.measurementdata.api.model.MeasurementDataSearchParameters;
import se.sundsvall.measurementdata.service.MeasurementDataService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class MeasurementDataResourceTest {

	private static final String PATH = "/{municipalityId}/measurement-data";

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private MeasurementDataService serviceMock;

	@Captor
	private ArgumentCaptor<MeasurementDataSearchParameters> parametersCaptor;

	@Test
	void getMeasurementData() {

		// Arrange
		final var municipalityId = "2281";
		final var aggregation = Aggregation.HOUR;
		final var category = Category.DISTRICT_HEATING;
		final var facilityId = "112233";
		final var fromDate = "2022-05-17T08:00:00.000Z";
		final var toDate = "2022-06-18T09:00:00.000Z";
		final var partyId = UUID.randomUUID().toString();

		when(serviceMock.fetchMeasurementData(any(), any())).thenReturn(Data.create());

		final MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
		parameters.add("partyId", partyId);
		parameters.add("category", category.name());
		parameters.add("facilityId", facilityId);
		parameters.add("aggregateOn", aggregation.name());
		parameters.add("fromDate", fromDate);
		parameters.add("toDate", toDate);

		// Act
		final var response = webTestClient.get().uri(uriBuilder -> uriBuilder.path(PATH)
			.queryParams(parameters)
			.build(municipalityId))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON_VALUE)
			.expectBody(Data.class)
			.returnResult()
			.getResponseBody();

		// Assert
		verify(serviceMock).fetchMeasurementData(eq(municipalityId), parametersCaptor.capture());

		final MeasurementDataSearchParameters parameterValues = parametersCaptor.getValue();
		assertThat(response).isNotNull().isEqualTo(Data.create());
		assertThat(parameterValues.getAggregateOn()).isEqualTo(aggregation);
		assertThat(parameterValues.getCategory()).isEqualTo(category);
		assertThat(parameterValues.getFacilityId()).isEqualTo(facilityId);
		assertThat(parameterValues.getFromDate()).isEqualTo(OffsetDateTime.parse(fromDate));
		assertThat(parameterValues.getPartyId()).isEqualTo(partyId);
		assertThat(parameterValues.getToDate()).isEqualTo(OffsetDateTime.parse(toDate));
	}
}
