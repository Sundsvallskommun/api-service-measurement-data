package se.sundsvall.measurementdata.api.model.example;

public class ResponseExamples {

	private ResponseExamples() {}

	public static final String DISTRICT_HEATING_RESPONSE_EXAMPLE = """
		{
		"category": "DISTRICT_HEATING",
		"facilityId": "1112233",
		"aggregateOn": "HOUR",
		"fromDate": "2022-05-17T08:00:00.000Z",
		"toDate": "2022-05-17T09:00:00.000Z",
		"measurementSeries": [
			{
			"unit": "MWh",
			"measurementType": "energy",
			"metaData": [],
			"measurementPoints": [
				{
				"value": 99.33,
				"timestamp": "2022-05-17T08:55:07.184Z",
				"metaData": []
				}
			]
			},
			{
			"unit": "m3",
			"measurementType": "volume",
			"metaData": [],
			"measurementPoints": [
				{
				"value": 22.312,
				"timestamp": "2022-05-17T08:55:07.184Z",
				"metaData": []
				}
			]
			},
			{
			"unit": "C",
			"measurementType": "flowTemperature",
			"metaData": [],
			"measurementPoints": [
				{
				"value": 63.33,
				"timestamp": "2022-05-17T08:55:07.184Z",
				"metaData": []
				}
			]
			},
			{
			"unit": "C",
			"measurementType": "returnTemperature",
			"metaData": [],
			"measurementPoints": [
				{
				"value": 57.13,
				"timestamp": "2022-05-17T08:55:07.184Z",
				"metaData": []
				}
			]
			}
		]
		}""";
	public static final String COMMUNICATION_RESPONSE_EXAMPLE = """
		{
		"category": "COMMUNICATION",
		"facilityId": "1112233",
		"aggregateOn": "HOUR",
		"fromDate": "2022-05-17T08:00:00.000Z",
		"toDate": "2022-05-17T09:00:00.000Z",
		"measurementSeries": [
			{
			"unit": "GB",
			"measurementType": "broadBandReceived",
			"metaData": [],
			"measurementPoints": [
				{
				"value": 139.76,
				"timestamp": "2022-05-17T08:55:07.184Z",
				"metaData": []
				}
			]
			},
			{
			"unit": "GB",
			"measurementType": "broadBandSent",
			"metaData": [],
			"measurementPoints": [
				{
				"value": 20.43,
				"timestamp": "2022-05-17T08:55:07.184Z",
				"metaData": []
				}
			]
			}
		]
		}""";

	public static final String ELECTRICITY_RESPONSE_EXAMPLE = """
		{
		"category": "ELECTRICITY",
		"facilityId": "1112233",
		"aggregateOn": "HOUR",
		"fromDate": "2022-05-17T08:00:00.000Z",
		"toDate": "2022-05-17T09:00:00.000Z",
		"measurementSeries": [
			{
			"unit": "kWh",
			"measurementType": "usage",
			"metaData": [],
			"measurementPoints": [
				{
				"value": 340.76,
				"timestamp": "2022-05-17T08:55:07.184Z",
				"metaData": [
					{
					"key": "readingType",
					"value": "Aktiv"
					}
				]
				}
			]
			}
		]
		}""";

	public static final String WASTE_MANAGEMENT_RESPONSE_EXAMPLE = """
		{
		"category": "WASTE_MANAGEMENT",
		"facilityId": "1112233",
		"aggregateOn": "HOUR",
		"fromDate": "2022-05-17T08:00:00.000Z",
		"toDate": "2022-05-17T09:00:00.000Z",
		"measurementSeries": [
			{
			"unit": "ton",
			"measurementType": "weight",
			"metaData": [],
			"measurementPoints": [
				{
				"value": 6.1,
				"timestamp": "2022-05-17T08:55:07.184Z",
				"metaData": [
					{
					"key": "productId",
					"value": "1063-1380-0-0-0"
					},
					{
					"key": "location",
					"value": "Blåberget"
					},
					{
					"key": "externalId",
					"value": "1302069"
					},
					{
					"key": "reference",
					"value": "ABC123"
					}
				]
				}
			]
			}
		]
		}""";
}
