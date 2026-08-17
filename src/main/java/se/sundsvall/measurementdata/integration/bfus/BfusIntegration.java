package se.sundsvall.measurementdata.integration.bfus;

import generated.se.sundsvall.datawarehousereader.Measurement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Component;
import se.sundsvall.measurementdata.integration.bfus.configuration.BfusProperties;
import se.sundsvall.measurementdata.service.mapper.BfusMapper;

@Component
public class BfusIntegration {

	private static final boolean USE_LOCAL_TIME = true;
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

	private final BfusClient bfusClient;
	private final BfusProperties bfusProperties;

	public BfusIntegration(final BfusClient bfusClient, final BfusProperties bfusProperties) {
		this.bfusClient = bfusClient;
		this.bfusProperties = bfusProperties;
	}

	/**
	 * Fetches electricity consumption for every facility from BFUS — one call per facility, since the BFUS endpoint
	 * accepts a single identifier and cannot aggregate across facilities — and normalises the responses into
	 * DataWarehouseReader {@link Measurement} objects.
	 */
	public List<Measurement> getElectricityConsumption(final List<String> facilityIds, final LocalDate dateFrom, final LocalDate dateTo) {
		return facilityIds.stream()
			.flatMap(facilityId -> getElectricityConsumption(facilityId, dateFrom, dateTo).stream())
			.toList();
	}

	private List<Measurement> getElectricityConsumption(final String facilityId, final LocalDate dateFrom, final LocalDate dateTo) {
		final var response = bfusClient.getConsumption(
			bfusProperties.externalId(),
			facilityId,
			bfusProperties.unitType(),
			bfusProperties.unit(),
			bfusProperties.periodSize(),
			bfusProperties.consumptionDataSource(),
			dateFrom.format(DATE_FORMAT),
			dateTo.format(DATE_FORMAT),
			USE_LOCAL_TIME,
			bfusProperties.identifierType());

		return BfusMapper.toMeasurements(facilityId, response);
	}
}
