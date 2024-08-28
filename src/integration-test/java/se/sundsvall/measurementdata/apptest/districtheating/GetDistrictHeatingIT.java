package se.sundsvall.measurementdata.apptest.districtheating;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import org.junit.jupiter.api.Test;

import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.measurementdata.Application;

@WireMockAppTestSuite(files = "classpath:/GetDistrictHeating/", classes = Application.class)
class GetDistrictHeatingIT extends AbstractAppTest {

	private static final String PATH = "/2281/measurement-data";
	private static final String RESPONSE_FILE = "response.json";

	@Test
	void test01_getDistrictHeatingMonthPrivate() {
		setupCall()
			.withServicePath(PATH +
				"?page=1" +
				"&limit=100" +
				"&partyId=B1EDEA3C-1083-4E1A-81FB-7D95E505E102" +
				"&category=DISTRICT_HEATING" +
				"&facilityId=735999109113202014" +
				"&fromDate=2018-01-01T14:39:22.817Z" +
				"&toDate=2018-12-31T14:39:22.817Z" +
				"&aggregateOn=MONTH")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_getDistrictHeatingNotImplementedAggregationLevel() {
		setupCall()
			.withServicePath(PATH +
				"?page=1" +
				"&limit=100" +
				"&partyId=B1EDEA3C-1083-4E1A-81FB-7D95E505E102" +
				"&category=DISTRICT_HEATING" +
				"&facilityId=735999109113202014" +
				"&fromDate=2018-01-01T14:39:22.817Z" +
				"&toDate=2018-12-31T14:39:22.817Z" +
				"&aggregateOn=HOUR")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(BAD_GATEWAY)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_customerNotFound() {
		setupCall()
			.withServicePath(PATH +
				"?page=1" +
				"&limit=100" +
				"&partyId=B1EDEA3C-1083-4E1A-81FB-7D95E505E103" +
				"&category=DISTRICT_HEATING" +
				"&facilityId=735999109113202014" +
				"&fromDate=2018-01-01T14:39:22.817Z" +
				"&toDate=2018-12-31T14:39:22.817Z" +
				"&aggregateOn=MONTH")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}
}
