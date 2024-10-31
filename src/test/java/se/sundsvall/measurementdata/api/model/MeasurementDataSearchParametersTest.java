package se.sundsvall.measurementdata.api.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Random;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.OffsetDateTime.now;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static se.sundsvall.measurementdata.api.model.Aggregation.HOUR;
import static se.sundsvall.measurementdata.api.model.Category.DISTRICT_HEATING;

class MeasurementDataSearchParametersTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(MeasurementDataSearchParameters.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testCreatePattern() {
		final var partyId = "partyId";
		final var category = DISTRICT_HEATING;
		final var facilityId = "facilityId";
		final var aggregateOn = HOUR;
		final var fromDate = OffsetDateTime.of(2000, 1, 2, 3, 4, 5, 6, now().getOffset());
		final var toDate = OffsetDateTime.of(2002, 6, 5, 4, 3, 2, 1, now().getOffset());

		MeasurementDataSearchParameters searchParameters = MeasurementDataSearchParameters.create()
			.withPartyId(partyId)
			.withCategory(category)
			.withFacilityId(facilityId)
			.withAggregateOn(aggregateOn)
			.withFromDate(fromDate)
			.withToDate(toDate);

		Assertions.assertThat(searchParameters.getPartyId()).isEqualTo(partyId);
		Assertions.assertThat(searchParameters.getCategory()).isSameAs(category);
		Assertions.assertThat(searchParameters.getFacilityId()).isEqualTo(facilityId);
		Assertions.assertThat(searchParameters.getAggregateOn()).isSameAs(aggregateOn);
		Assertions.assertThat(searchParameters.getFromDate()).isSameAs(fromDate);
		Assertions.assertThat(searchParameters.getToDate()).isSameAs(toDate);
	}

	@Test
	void testCreateWithNoValueSet() {
		Assertions.assertThat(MeasurementDataSearchParameters.create()).hasAllNullFieldsOrProperties();
	}
}
