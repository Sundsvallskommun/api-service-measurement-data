package se.sundsvall.measurementdata.service;

import static java.net.URLEncoder.encode;
import static java.nio.charset.Charset.defaultCharset;
import static se.sundsvall.measurementdata.service.mapper.DataWarehouseReaderMapper.toAggregation;
import static se.sundsvall.measurementdata.service.mapper.DataWarehouseReaderMapper.toCategory;
import static se.sundsvall.measurementdata.service.mapper.DataWarehouseReaderMapper.toData;

import generated.se.sundsvall.datawarehousereader.MeasurementResponse;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;
import se.sundsvall.measurementdata.api.model.Data;
import se.sundsvall.measurementdata.api.model.MeasurementDataSearchParameters;
import se.sundsvall.measurementdata.integration.datawarehousereader.DataWarehouseReaderClient;

@Service
public class MeasurementDataService {

	private static final int MEASUREMENT_DATA_RESPONSE_LIMIT = 1000;

	private final DataWarehouseReaderClient dataWarehouseReaderClient;

	public MeasurementDataService(DataWarehouseReaderClient dataWarehouseReaderClient) {
		this.dataWarehouseReaderClient = dataWarehouseReaderClient;
	}

	public Data fetchMeasurementData(final String municipalityId, final MeasurementDataSearchParameters parameters) {

		final MeasurementResponse response = dataWarehouseReaderClient.getMeasurementData(
			municipalityId,
			toCategory(parameters.getCategory()),
			toAggregation(parameters.getAggregateOn()),
			parameters.getPartyId(),
			parameters.getFacilityId(),
			asEncodedString(parameters.getFromDate()),
			asEncodedString(parameters.getToDate()),
			MEASUREMENT_DATA_RESPONSE_LIMIT);

		return toData(parameters, response);
	}

	private static String asEncodedString(final OffsetDateTime date) {
		return Optional.ofNullable(date)
			.map(d -> encode(d.toString(), defaultCharset()))
			.orElse(null);
	}
}
