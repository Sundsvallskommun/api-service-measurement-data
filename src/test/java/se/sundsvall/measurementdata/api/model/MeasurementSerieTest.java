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

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Random;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MeasurementSerieTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(MeasurementSerie.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testCreatePattern() {
		final var unit = "unit";
		final var measurementType = "measurementType";
		final var metaData = new MetaData().withValue("value").withKey("key");
		final var measurementPoint = new MeasurementPoint();

		MeasurementSerie serie = MeasurementSerie.create()
			.withUnit(unit)
			.withMeasurementType(measurementType)
			.withMetaData(Arrays.asList(metaData))
			.withMeasurementPoints(Arrays.asList(measurementPoint));

		assertThat(serie.getUnit()).isEqualTo(unit);
		assertThat(serie.getMeasurementType()).isEqualTo(measurementType);
		assertThat(serie.getMetaData()).containsExactly(metaData);
		assertThat(serie.getMeasurementPoints()).containsExactly(measurementPoint);

	}

	@Test
	void testCreateWithNoValueSet() {
		Assertions.assertThat(MeasurementSerie.create()).hasAllNullFieldsOrProperties();
	}
}
