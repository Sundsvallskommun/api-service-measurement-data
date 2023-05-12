package se.sundsvall.measurementdata.apptest.electricity;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

import org.junit.jupiter.api.Test;

import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.measurementdata.Application;

@WireMockAppTestSuite(files = "classpath:/GetElectricity/", classes = Application.class)
class GetElectricityIT extends AbstractAppTest {

	private static final String PATH = "/measurement-data";
	private static final String RESPONSE_FILE = "response.json";

	@Test
	void test01_getElectricityMonthPrivate() throws Exception {
		setupCall()
			.withServicePath(PATH +
				"?page=1" +
				"&limit=100" +
				"&partyId=98BF599D-8A4D-4040-9A3C-128834D845F6" +
				"&category=ELECTRICITY" +
				"&facilityId=735999109128517141" +
				"&fromDate=2019-01-01T14:39:22.817Z" +
				"&toDate=2019-06-30T14:39:22.817Z" +
				"&aggregateOn=MONTH")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_getElectricityDayEnterprise() throws Exception {
		setupCall()
			.withServicePath(PATH +
				"?page=1" +
				"&limit=100" +
				"&partyId=BF7967EC-261A-4FF8-934A-8BF31B299169" +
				"&category=ELECTRICITY" +
				"&facilityId=735999109250135015" +
				"&fromDate=2022-05-01T11:55:00.558Z" +
				"&toDate=2022-06-30T11:55:00.558Z" +
				"&aggregateOn=DAY")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}
}
