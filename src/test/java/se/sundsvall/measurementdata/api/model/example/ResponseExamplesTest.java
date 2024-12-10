package se.sundsvall.measurementdata.api.model.example;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.measurementdata.api.model.example.ResponseExamples.COMMUNICATION_RESPONSE_EXAMPLE;
import static se.sundsvall.measurementdata.api.model.example.ResponseExamples.DISTRICT_HEATING_RESPONSE_EXAMPLE;
import static se.sundsvall.measurementdata.api.model.example.ResponseExamples.ELECTRICITY_RESPONSE_EXAMPLE;
import static se.sundsvall.measurementdata.api.model.example.ResponseExamples.WASTE_MANAGEMENT_RESPONSE_EXAMPLE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import se.sundsvall.measurementdata.api.model.Data;

class ResponseExamplesTest {

	private ObjectMapper mapper;

	@BeforeEach
	void config() {
		this.mapper = JsonMapper.builder()
			.findAndAddModules()
			.build();
	}

	@ParameterizedTest
	@ValueSource(strings = {
		DISTRICT_HEATING_RESPONSE_EXAMPLE,
		COMMUNICATION_RESPONSE_EXAMPLE,
		ELECTRICITY_RESPONSE_EXAMPLE,
		WASTE_MANAGEMENT_RESPONSE_EXAMPLE
	})
	void verifyDistrictHeatingExample(String responseExample) throws JsonProcessingException {
		assertThat(mapper.readValue(responseExample, Data.class))
			.isNotNull()
			.isInstanceOf(Data.class);
	}
}
