package se.sundsvall.measurementdata.integration.datawarehousereader;

import generated.se.sundsvall.datawarehousereader.Measurement;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import se.sundsvall.measurementdata.integration.datawarehousereader.configuration.DataWarehouseReaderConfiguration;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static se.sundsvall.measurementdata.integration.datawarehousereader.configuration.DataWarehouseReaderConfiguration.CLIENT_ID;

@FeignClient(name = CLIENT_ID, url = "${integration.datawarehousereader.url}", configuration = DataWarehouseReaderConfiguration.class)
@CircuitBreaker(name = CLIENT_ID)
public interface DataWarehouseReaderClient {

	@GetMapping(path = "/{municipalityId}/measurements/{category}/{aggregateOn}", produces = {
		APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE
	})
	List<Measurement> getMeasurements(
		@PathVariable final String municipalityId,
		@PathVariable final String category,
		@PathVariable final String aggregateOn,
		@RequestParam final String partyId,
		@RequestParam final List<String> facilityId,
		@RequestParam final String fromDateTime,
		@RequestParam final String toDateTime);
}
