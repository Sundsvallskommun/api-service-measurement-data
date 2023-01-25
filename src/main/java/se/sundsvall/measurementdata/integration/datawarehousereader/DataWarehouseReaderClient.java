package se.sundsvall.measurementdata.integration.datawarehousereader;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static se.sundsvall.measurementdata.integration.datawarehousereader.configuration.DataWarehouseReaderConfiguration.CLIENT_REGISTRATION_ID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import generated.se.sundsvall.datawarehousereader.Aggregation;
import generated.se.sundsvall.datawarehousereader.Category;
import generated.se.sundsvall.datawarehousereader.MeasurementResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.measurementdata.integration.datawarehousereader.configuration.DataWarehouseReaderConfiguration;

@FeignClient(name = CLIENT_REGISTRATION_ID, url = "${integration.datawarehousereader.url}", configuration = DataWarehouseReaderConfiguration.class)
@CircuitBreaker(name = CLIENT_REGISTRATION_ID)
public interface DataWarehouseReaderClient {

	@GetMapping(path = "measurements/{category}/{aggregateOn}", produces = { APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
	MeasurementResponse getMeasurementData(
		@PathVariable("category") Category category,
		@PathVariable("aggregateOn") Aggregation aggregateOn,
		@RequestParam("partyId") String partyId,
		@RequestParam("facilityId") String facilityId,
		@RequestParam("fromDateTime") String fromDateTime,
		@RequestParam("toDateTime") String toDateTime,
		@RequestParam("limit") int limit);
}
