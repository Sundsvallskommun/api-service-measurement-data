package se.sundsvall.measurementdata.apptest.electricity;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

import org.junit.jupiter.api.Test;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.measurementdata.Application;

/**
 * Historical electricity quarter values (before the BFUS cut-off date 2025-10-21) are fetched from BFUS instead of
 * DataWarehouseReader. Verifies the BFUS-only path, in-app cross-facility aggregation, and the split/merge when the
 * requested period spans the cut-off date.
 */
@WireMockAppTestSuite(files = "classpath:/GetElectricityHistorical/", classes = Application.class)
class GetElectricityHistoricalIT extends AbstractAppTest {

	private static final String PATH = "/2281/measurement-data";
	private static final String RESPONSE_FILE = "response.json";

	@Test
	void test01_getHistoricalQuarterElectricitySingleFacility() {
		setupCall()
			.withServicePath(PATH +
				"?partyId=98BF599D-8A4D-4040-9A3C-128834D845F6" +
				"&category=ELECTRICITY" +
				"&facilityId=735999109171206078" +
				"&fromDate=2025-01-10T12:00:00.000Z" +
				"&toDate=2025-01-20T12:00:00.000Z" +
				"&aggregateOn=QUARTER")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_getHistoricalQuarterElectricityAggregated() {
		setupCall()
			.withServicePath(PATH +
				"?partyId=98BF599D-8A4D-4040-9A3C-128834D845F6" +
				"&category=ELECTRICITY" +
				"&facilityId=735999109171206078" +
				"&facilityId=735999109171206079" +
				"&fromDate=2025-01-10T12:00:00.000Z" +
				"&toDate=2025-01-20T12:00:00.000Z" +
				"&aggregateOn=QUARTER" +
				"&display=AGGREGATE")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_getHistoricalQuarterElectricitySpanningCutoff() {
		setupCall()
			.withServicePath(PATH +
				"?partyId=98BF599D-8A4D-4040-9A3C-128834D845F6" +
				"&category=ELECTRICITY" +
				"&facilityId=735999109171206078" +
				"&fromDate=2025-10-10T12:00:00.000Z" +
				"&toDate=2025-11-10T12:00:00.000Z" +
				"&aggregateOn=QUARTER")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}
}
