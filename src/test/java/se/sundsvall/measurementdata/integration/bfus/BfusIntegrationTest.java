package se.sundsvall.measurementdata.integration.bfus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.measurementdata.integration.bfus.configuration.BfusProperties;
import se.sundsvall.measurementdata.integration.bfus.model.ConsumptionResponse;
import se.sundsvall.measurementdata.integration.bfus.model.ConsumptionResponse.Content;
import se.sundsvall.measurementdata.integration.bfus.model.MeterReadingPart;
import se.sundsvall.measurementdata.integration.bfus.model.PeriodicValue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BfusIntegrationTest {

	@Mock
	private BfusClient bfusClientMock;

	@Mock
	private BfusProperties bfusPropertiesMock;

	@InjectMocks
	private BfusIntegration integration;

	private void stubProperties() {
		when(bfusPropertiesMock.externalId()).thenReturn("ext");
		when(bfusPropertiesMock.unitType()).thenReturn(101);
		when(bfusPropertiesMock.unit()).thenReturn("kWh");
		when(bfusPropertiesMock.periodSize()).thenReturn(15);
		when(bfusPropertiesMock.consumptionDataSource()).thenReturn(2);
		when(bfusPropertiesMock.identifierType()).thenReturn("serviceidentifier");
	}

	private static ConsumptionResponse response(final BigDecimal consumption) {
		return new ConsumptionResponse(new Content(List.of(
			new MeterReadingPart("kWh", List.of(
				new PeriodicValue("2025-09-01T00:00:00", consumption, "2"))))));
	}

	@Test
	void getElectricityConsumption_callsClientPerFacilityAndMaps() {
		stubProperties();
		when(bfusClientMock.getConsumption("ext", "facility1", 101, "kWh", 15, 2, "2025-01-01", "2025-10-20", true, "serviceidentifier"))
			.thenReturn(response(BigDecimal.valueOf(0.21)));

		final var result = integration.getElectricityConsumption(List.of("facility1"), LocalDate.of(2025, 1, 1), LocalDate.of(2025, 10, 20));

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getFacilityId()).isEqualTo("facility1");
		assertThat(result.getFirst().getFeedType()).isEqualTo("Energy");
		assertThat(result.getFirst().getUnit()).isEqualTo("kWh");
		assertThat(result.getFirst().getUsage()).isEqualByComparingTo("0.21");

		verify(bfusClientMock).getConsumption("ext", "facility1", 101, "kWh", 15, 2, "2025-01-01", "2025-10-20", true, "serviceidentifier");
		verifyNoMoreInteractions(bfusClientMock);
	}

	@Test
	void getElectricityConsumption_callsClientForEachFacility() {
		stubProperties();
		when(bfusClientMock.getConsumption("ext", "facility1", 101, "kWh", 15, 2, "2025-01-01", "2025-10-20", true, "serviceidentifier"))
			.thenReturn(response(BigDecimal.valueOf(1)));
		when(bfusClientMock.getConsumption("ext", "facility2", 101, "kWh", 15, 2, "2025-01-01", "2025-10-20", true, "serviceidentifier"))
			.thenReturn(response(BigDecimal.valueOf(2)));

		final var result = integration.getElectricityConsumption(List.of("facility1", "facility2"), LocalDate.of(2025, 1, 1), LocalDate.of(2025, 10, 20));

		assertThat(result).hasSize(2)
			.extracting("facilityId")
			.containsExactly("facility1", "facility2");

		verify(bfusClientMock).getConsumption("ext", "facility1", 101, "kWh", 15, 2, "2025-01-01", "2025-10-20", true, "serviceidentifier");
		verify(bfusClientMock).getConsumption("ext", "facility2", 101, "kWh", 15, 2, "2025-01-01", "2025-10-20", true, "serviceidentifier");
		verifyNoMoreInteractions(bfusClientMock);
	}

	@Test
	void getElectricityConsumption_withEmptyFacilityList_doesNotCallClient() {
		final var result = integration.getElectricityConsumption(List.of(), LocalDate.of(2025, 1, 1), LocalDate.of(2025, 10, 20));

		assertThat(result).isEmpty();
		verifyNoMoreInteractions(bfusClientMock);
	}
}
