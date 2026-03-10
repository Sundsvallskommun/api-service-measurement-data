package se.sundsvall.measurementdata.service;

import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;
import se.sundsvall.measurementdata.api.model.Data;
import se.sundsvall.measurementdata.api.model.MeasurementDataSearchParameters;
import se.sundsvall.measurementdata.integration.datawarehousereader.DataWarehouseReaderClient;

import static java.net.URLEncoder.encode;
import static java.nio.charset.Charset.defaultCharset;
import static se.sundsvall.measurementdata.service.mapper.DataWarehouseReaderMapper.toData;

@Service
public class MeasurementDataService {

	private final DataWarehouseReaderClient dataWarehouseReaderClient;

	public MeasurementDataService(final DataWarehouseReaderClient dataWarehouseReaderClient) {
		this.dataWarehouseReaderClient = dataWarehouseReaderClient;
	}

	public Data fetchMeasurementData(final String municipalityId, final MeasurementDataSearchParameters parameters) {

		final var measurements = dataWarehouseReaderClient.getMeasurements(
			municipalityId,
			parameters.getCategory().name(),
			parameters.getAggregateOn().name(),
			parameters.getPartyId(),
			parameters.getFacilityIds(),
			asEncodedString(parameters.getFromDate()),
			asEncodedString(parameters.getToDate()));

		return toData(parameters, measurements);
	}

	private static String asEncodedString(final OffsetDateTime offsetDateTime) {
		return Optional.ofNullable(offsetDateTime)
			.map(dateTime -> encode(dateTime.toString(), defaultCharset()))
			.orElse(null);
	}
}
