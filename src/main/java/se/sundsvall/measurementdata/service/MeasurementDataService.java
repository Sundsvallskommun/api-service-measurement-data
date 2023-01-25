package se.sundsvall.measurementdata.service;

import static java.net.URLEncoder.encode;
import static java.nio.charset.Charset.defaultCharset;
import static java.util.Objects.isNull;
import static se.sundsvall.measurementdata.service.mapper.DataWarehouseReaderMapper.toAggregation;
import static se.sundsvall.measurementdata.service.mapper.DataWarehouseReaderMapper.toCategory;
import static se.sundsvall.measurementdata.service.mapper.DataWarehouseReaderMapper.toData;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import generated.se.sundsvall.datawarehousereader.MeasurementResponse;
import se.sundsvall.measurementdata.api.model.Data;
import se.sundsvall.measurementdata.api.model.MeasurementDataSearchParameters;
import se.sundsvall.measurementdata.integration.datawarehousereader.DataWarehouseReaderClient;

@Service
public class MeasurementDataService {
	private static final int MEASUREMENT_DATA_RESPONSE_LIMIT = 1000;

	@Autowired
	private DataWarehouseReaderClient dataWarehouseReaderClient;

	public Data fetchMeasurementData(MeasurementDataSearchParameters parameters) {
		MeasurementResponse response = dataWarehouseReaderClient.getMeasurementData(
			toCategory(parameters.getCategory()),
			toAggregation(parameters.getAggregateOn()),
			parameters.getPartyId(),
			parameters.getFacilityId(),
			asEncodedString(parameters.getFromDate()),
			asEncodedString(parameters.getToDate()),
			MEASUREMENT_DATA_RESPONSE_LIMIT);

		return toData(parameters, response);
	}

	private static String asEncodedString(OffsetDateTime date) {
		return isNull(date) ? null : encode(date.toString(), defaultCharset());
	}
}
