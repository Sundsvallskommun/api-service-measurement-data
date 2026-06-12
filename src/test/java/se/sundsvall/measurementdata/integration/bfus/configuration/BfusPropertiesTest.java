package se.sundsvall.measurementdata.integration.bfus.configuration;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.measurementdata.Application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class BfusPropertiesTest {

	@Autowired
	private BfusProperties properties;

	@Test
	void testProperties() {
		assertThat(properties.connectTimeout()).isEqualTo(5);
		assertThat(properties.readTimeout()).isEqualTo(30);
		assertThat(properties.externalId()).isEqualTo("the-external-id");
		assertThat(properties.identifierType()).isEqualTo("serviceidentifier");
		assertThat(properties.unitType()).isEqualTo(101);
		assertThat(properties.unit()).isEqualTo("kWh");
		assertThat(properties.periodSize()).isEqualTo(15);
		assertThat(properties.consumptionDataSource()).isEqualTo(2);
		assertThat(properties.cutoffDate()).isEqualTo(LocalDate.of(2025, 10, 21));
	}
}
