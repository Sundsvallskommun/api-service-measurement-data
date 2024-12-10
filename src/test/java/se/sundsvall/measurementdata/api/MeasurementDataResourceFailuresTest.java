package se.sundsvall.measurementdata.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.zalando.problem.Status.BAD_REQUEST;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;
import se.sundsvall.measurementdata.Application;
import se.sundsvall.measurementdata.api.model.Aggregation;
import se.sundsvall.measurementdata.api.model.Category;
import se.sundsvall.measurementdata.service.MeasurementDataService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class MeasurementDataResourceFailuresTest {

	private static final String PATH = "/{municipalityId}/measurement-data";

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private MeasurementDataService serviceMock;

	@Test
	void getMeasurementDataMissingParameters() {

		// Arrange
		final var municipalityId = "2281";

		// Act
		final var response = webTestClient.get().uri(uriBuilder -> uriBuilder.path(PATH)
			.build(municipalityId))
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON_VALUE)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(
				tuple("aggregateOn", "must not be null"),
				tuple("category", "must not be null"),
				tuple("facilityId", "must not be blank"),
				tuple("fromDate", "must not be null"),
				tuple("partyId", "not a valid UUID"),
				tuple("toDate", "must not be null"));

		verifyNoInteractions(serviceMock);
	}

	@Test
	void getMeasurementDataInvalidMunicipalityId() {

		// Arrange
		final var municipalityId = "invalid";
		final var aggregation = Aggregation.HOUR;
		final var category = Category.DISTRICT_HEATING;
		final var facilityId = "112233";
		final var fromDate = "2022-05-17T08:00:00.000Z";
		final var toDate = "2022-06-18T09:00:00.000Z";
		final var partyId = UUID.randomUUID().toString();

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
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON_VALUE)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(
				tuple("getMeasurementData.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(serviceMock);
	}
}
