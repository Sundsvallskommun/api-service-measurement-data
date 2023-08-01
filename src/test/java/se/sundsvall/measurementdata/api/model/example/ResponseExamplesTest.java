package se.sundsvall.measurementdata.api.model.example;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.measurementdata.api.model.example.ResponseExamples.COMMUNICATION_RESPONSE_EXAMPLE;
import static se.sundsvall.measurementdata.api.model.example.ResponseExamples.DISTRICT_HEATING_RESPONSE_EXAMPLE;
import static se.sundsvall.measurementdata.api.model.example.ResponseExamples.ELECTRICITY_RESPONSE_EXAMPLE;
import static se.sundsvall.measurementdata.api.model.example.ResponseExamples.WASTE_MANAGEMENT_RESPONSE_EXAMPLE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import se.sundsvall.measurementdata.api.model.Data;

class ResponseExamplesTest {

	private ObjectMapper mapper;

	@BeforeEach
	void config() {
		this.mapper = JsonMapper.builder()
			.findAndAddModules()
			.build();
	}

	@Test
	void verifyDistrictHeatingExample() throws JsonProcessingException {
		assertThat(mapper.readValue(DISTRICT_HEATING_RESPONSE_EXAMPLE, Data.class)).isInstanceOf(Data.class);
	}

	@Test
	void verifyCommunicationExample() throws JsonProcessingException {
		assertThat(mapper.readValue(COMMUNICATION_RESPONSE_EXAMPLE, Data.class)).isInstanceOf(Data.class);
	}

	@Test
	void verifyElectricityExample() throws JsonProcessingException {
		assertThat(mapper.readValue(ELECTRICITY_RESPONSE_EXAMPLE, Data.class)).isInstanceOf(Data.class);
	}

	@Test
	void verifyWasteExample() throws JsonProcessingException {
		assertThat(mapper.readValue(WASTE_MANAGEMENT_RESPONSE_EXAMPLE, Data.class)).isInstanceOf(Data.class);
	}
}
