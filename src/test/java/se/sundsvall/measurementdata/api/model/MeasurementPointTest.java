package se.sundsvall.measurementdata.api.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Random;

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

class MeasurementPointTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(MeasurementPoint.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testCreatePattern() {
		final var value = BigDecimal.valueOf(10.1d);
		final var metaData = new MetaData().withValue("value").withKey("key");
		final var timestamp = OffsetDateTime.of(2000, 1, 2, 3, 4, 5, 6, now().getOffset());

		MeasurementPoint point = MeasurementPoint.create()
			.withValue(value)
			.withTimestamp(timestamp)
			.withMetaData(Arrays.asList(metaData));

		assertThat(point.getValue()).isEqualTo(value);
		assertThat(point.getTimestamp()).isSameAs(timestamp);
		assertThat(point.getMetaData()).containsExactly(metaData);
	}

	@Test
	void testCreateWithNoValueSet() {
		Assertions.assertThat(MeasurementPoint.create()).hasAllNullFieldsOrProperties();
	}
}
