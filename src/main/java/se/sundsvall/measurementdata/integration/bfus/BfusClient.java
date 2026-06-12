package se.sundsvall.measurementdata.integration.bfus;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import se.sundsvall.measurementdata.integration.bfus.configuration.BfusConfiguration;
import se.sundsvall.measurementdata.integration.bfus.model.ConsumptionResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.measurementdata.integration.bfus.configuration.BfusConfiguration.CLIENT_ID;

@FeignClient(name = CLIENT_ID, url = "${integration.bfus.url}", configuration = BfusConfiguration.class)
@CircuitBreaker(name = CLIENT_ID)
public interface BfusClient {

	/**
	 * Fetches periodic consumption values for a single identifier (facility). The path uses the period-size overload so
	 * that quarter-hour (15 minute) resolution can be requested.
	 *
	 * @see <a href="https://api-test.sundsvall.se/bfus/1.0.0">BFUS API (api-test)</a>
	 */
	@GetMapping(path = "/EP/Reading/Consumptions/{externalId}/{identifier}/{unitType}/{unit}/{periodSize}/{consumptionDataSource}/{dateFrom}/{dateTo}/{isLocalTime}/{identifierType}", produces = APPLICATION_JSON_VALUE)
	ConsumptionResponse getConsumption(
		@PathVariable final String externalId,
		@PathVariable final String identifier,
		@PathVariable final int unitType,
		@PathVariable final String unit,
		@PathVariable final int periodSize,
		@PathVariable final int consumptionDataSource,
		@PathVariable final String dateFrom,
		@PathVariable final String dateTo,
		@PathVariable final boolean isLocalTime,
		@PathVariable final String identifierType);
}
