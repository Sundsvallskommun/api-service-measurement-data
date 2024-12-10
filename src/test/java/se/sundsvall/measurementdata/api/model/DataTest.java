package se.sundsvall.measurementdata.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static se.sundsvall.measurementdata.api.model.Aggregation.HOUR;
import static se.sundsvall.measurementdata.api.model.Category.DISTRICT_HEATING;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DataTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(Data.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testCreatePattern() {
		final var category = DISTRICT_HEATING;
		final var installationId = "installationId";
		final var aggregateOn = HOUR;
		final var fromDate = OffsetDateTime.of(2000, 1, 2, 3, 4, 5, 6, now().getOffset());
		final var toDate = OffsetDateTime.of(2002, 6, 5, 4, 3, 2, 1, now().getOffset());
		final var measurementSerie = new MeasurementSerie();

		Data data = Data.create()
			.withCategory(category)
			.withFacilityId(installationId)
			.withAggregateOn(aggregateOn)
			.withFromDate(fromDate)
			.withToDate(toDate)
			.withMeasurementSeries(Arrays.asList(measurementSerie));

		assertThat(data.getCategory()).isSameAs(category);
		assertThat(data.getFacilityId()).isEqualTo(installationId);
		assertThat(data.getAggregateOn()).isSameAs(aggregateOn);
		assertThat(data.getFromDate()).isSameAs(fromDate);
		assertThat(data.getToDate()).isSameAs(toDate);
		assertThat(data.getMeasurementSeries()).containsExactly(measurementSerie);

	}

	@Test
	void testCreateWithNoValueSet() {
		assertThat(Data.create()).hasAllNullFieldsOrProperties();
	}
}
