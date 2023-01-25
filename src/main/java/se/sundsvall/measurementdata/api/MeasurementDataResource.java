package se.sundsvall.measurementdata.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static se.sundsvall.measurementdata.api.model.example.ResponseExamples.COMMUNICATION_RESPONSE_EXAMPLE;
import static se.sundsvall.measurementdata.api.model.example.ResponseExamples.DISTRICT_HEATING_RESPONSE_EXAMPLE;
import static se.sundsvall.measurementdata.api.model.example.ResponseExamples.ELECTRICITY_RESPONSE_EXAMPLE;
import static se.sundsvall.measurementdata.api.model.example.ResponseExamples.WASTE_MANAGEMENT_RESPONSE_EXAMPLE;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import se.sundsvall.measurementdata.api.model.Data;
import se.sundsvall.measurementdata.api.model.MeasurementDataSearchParameters;
import se.sundsvall.measurementdata.service.MeasurementDataService;

@RestController
@Validated
@Tag(name = "Measurement", description = "Measurement operations")
public class MeasurementDataResource {

	@Autowired
	private MeasurementDataService service;

	@GetMapping(path = "measurement-data", produces = { APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
	@Operation(summary = "Get a persons measurement data points for different categories")
	@ApiResponse(responseCode = "200", description = "Successful Operation", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = Data.class), examples = {
		@ExampleObject(name = "DISTRICT_HEATING", value = DISTRICT_HEATING_RESPONSE_EXAMPLE),
		@ExampleObject(name = "BROADBAND", value = COMMUNICATION_RESPONSE_EXAMPLE),
		@ExampleObject(name = "ELECTRICITY", value = ELECTRICITY_RESPONSE_EXAMPLE),
		@ExampleObject(name = "WASTE_MANAGEMENT", value = WASTE_MANAGEMENT_RESPONSE_EXAMPLE) }))
	@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = { Problem.class, ConstraintViolationProblem.class })))
	@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	@ApiResponse(responseCode = "502", description = "Bad Gateway", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<Data> getMeasurementData(@Valid MeasurementDataSearchParameters parameters) {
		return ResponseEntity.ok(service.fetchMeasurementData(parameters));
	}
}
