package se.sundsvall.measurementdata.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import se.sundsvall.measurementdata.Application;
import se.sundsvall.measurementdata.api.model.Aggregation;
import se.sundsvall.measurementdata.api.model.Category;
import se.sundsvall.measurementdata.api.model.Data;
import se.sundsvall.measurementdata.api.model.MeasurementDataSearchParameters;
import se.sundsvall.measurementdata.service.MeasurementDataService;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("junit")
class MeasurementDataResourceTest {

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private MeasurementDataService serviceMock;
	
	@Captor
	private ArgumentCaptor<MeasurementDataSearchParameters> parametersCaptor;
	
	@Test
	void getMeasurementData() {
		final var aggregation = Aggregation.HOUR;
		final var category = Category.DISTRICT_HEATING;
		final var facilityId = "112233";
		final var fromDate = "2022-05-17T08:00:00.000Z";
		final var toDate = "2022-06-18T09:00:00.000Z";
		final var partyId = UUID.randomUUID().toString();
		
		when(serviceMock.fetchMeasurementData(any())).thenReturn(Data.create());
		
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
		parameters.add("partyId", partyId);
		parameters.add("category", category.name());
		parameters.add("facilityId", facilityId);
		parameters.add("aggregateOn", aggregation.name());
		parameters.add("fromDate", fromDate);
		parameters.add("toDate", toDate);

		final var response = webTestClient.get().uri(uriBuilder -> uriBuilder.path("/measurement-data")
			.queryParams(parameters)
			.build())
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON_VALUE)
			.expectBody(Data.class)
			.returnResult()
			.getResponseBody();
		
		verify(serviceMock).fetchMeasurementData(parametersCaptor.capture());

		MeasurementDataSearchParameters parameterValues = parametersCaptor.getValue();
		assertThat(response).isNotNull().isEqualTo(Data.create());
		assertThat(parameterValues.getAggregateOn()).isEqualTo(aggregation);
		assertThat(parameterValues.getCategory()).isEqualTo(category);
		assertThat(parameterValues.getFacilityId()).isEqualTo(facilityId);
		assertThat(parameterValues.getFromDate()).isEqualTo(OffsetDateTime.parse(fromDate));
		assertThat(parameterValues.getPartyId()).isEqualTo(partyId);
		assertThat(parameterValues.getToDate()).isEqualTo(OffsetDateTime.parse(toDate));
		
	}

	@Test
	void getMeasurementDataMissingParameters() {
		webTestClient.get().uri("/measurement-data")
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON_VALUE)
			.expectBody()
			.jsonPath("$.title").isEqualTo("Constraint Violation")
			.jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
			.jsonPath("$.violations").value(hasSize(6))
			.jsonPath("$.violations").value(hasItem(allOf(hasEntry("field", "partyId"), hasEntry("message", "not a valid UUID"))))
			.jsonPath("$.violations").value(hasItem(allOf(hasEntry("field", "aggregateOn"), hasEntry("message", "must not be null"))))
			.jsonPath("$.violations").value(hasItem(allOf(hasEntry("field", "category"), hasEntry("message", "must not be null"))))
			.jsonPath("$.violations").value(hasItem(allOf(hasEntry("field", "facilityId"), hasEntry("message", "must not be blank"))))
			.jsonPath("$.violations").value(hasItem(allOf(hasEntry("field", "fromDate"), hasEntry("message", "must not be null"))))
			.jsonPath("$.violations").value(hasItem(allOf(hasEntry("field", "toDate"), hasEntry("message", "must not be null"))));
	}
}