package se.sundsvall.measurementdata.service;

import generated.se.sundsvall.datawarehousereader.Measurement;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import se.sundsvall.measurementdata.api.model.Data;
import se.sundsvall.measurementdata.api.model.MeasurementDataSearchParameters;
import se.sundsvall.measurementdata.integration.bfus.BfusIntegration;
import se.sundsvall.measurementdata.integration.bfus.configuration.BfusProperties;
import se.sundsvall.measurementdata.integration.datawarehousereader.DataWarehouseReaderClient;

import static java.net.URLEncoder.encode;
import static java.nio.charset.Charset.defaultCharset;
import static se.sundsvall.measurementdata.api.model.Aggregation.QUARTER;
import static se.sundsvall.measurementdata.api.model.Category.ELECTRICITY;
import static se.sundsvall.measurementdata.service.mapper.DataWarehouseReaderMapper.toData;
import static se.sundsvall.measurementdata.service.mapper.ElectricityAggregator.applyAggregation;

@Service
public class MeasurementDataService {

	private static final ZoneId ZONE = ZoneId.of("Europe/Stockholm");

	private final DataWarehouseReaderClient dataWarehouseReaderClient;
	private final BfusIntegration bfusIntegration;
	private final BfusProperties bfusProperties;

	public MeasurementDataService(final DataWarehouseReaderClient dataWarehouseReaderClient, final BfusIntegration bfusIntegration, final BfusProperties bfusProperties) {
		this.dataWarehouseReaderClient = dataWarehouseReaderClient;
		this.bfusIntegration = bfusIntegration;
		this.bfusProperties = bfusProperties;
	}

	public Data fetchMeasurementData(final String municipalityId, final MeasurementDataSearchParameters parameters) {
		if (usesBfus(parameters)) {
			return fetchHistoricalQuarterElectricity(municipalityId, parameters);
		}
		return fetchFromDataWarehouseReader(municipalityId, parameters);
	}

	private Data fetchFromDataWarehouseReader(final String municipalityId, final MeasurementDataSearchParameters parameters) {
		final var measurements = dataWarehouseReaderClient.getMeasurements(
			municipalityId,
			parameters.getCategory().name(),
			parameters.getAggregateOn().name(),
			parameters.getPartyId(),
			parameters.getFacilityIds(),
			asEncodedString(parameters.getFromDate()),
			asEncodedString(parameters.getToDate()),
			asEnumName(parameters.getDisplay()));

		return toData(parameters, measurements);
	}

	/**
	 * Electricity quarter values before the BFUS cut-off date are no longer available in DataWarehouseReader. They are
	 * fetched from BFUS (one call per facility) for the pre-cut-off window; if the requested period also extends into
	 * the post-cut-off window, the remainder is fetched from DataWarehouseReader and merged. Cross-facility aggregation
	 * is reconstructed in-app since BFUS cannot aggregate over facilities.
	 */
	private Data fetchHistoricalQuarterElectricity(final String municipalityId, final MeasurementDataSearchParameters parameters) {
		final var cutoffDate = bfusProperties.cutoffDate();
		final var measurements = new ArrayList<Measurement>();

		// BFUS dateFrom is inclusive and dateTo is exclusive. Query from the requested start through the day after the
		// requested end, capped at the cut-off date (also exclusive) so BFUS yields everything up to and including the
		// day before the cut-off.
		final var bfusDateFrom = toLocalDate(parameters.getFromDate());
		final var bfusDateToExclusive = earliest(exclusiveEndDate(parameters.getToDate()), cutoffDate);
		measurements.addAll(bfusIntegration.getElectricityConsumption(parameters.getFacilityIds(), bfusDateFrom, bfusDateToExclusive));

		// DataWarehouseReader owns everything from the cut-off date onwards (only when the period spans the cut-off).
		if (spansCutoff(parameters)) {
			measurements.addAll(fetchPostCutoffFromDataWarehouseReader(municipalityId, parameters));
		}

		return toData(parameters, applyAggregation(measurements, parameters.getDisplay()));
	}

	private List<Measurement> fetchPostCutoffFromDataWarehouseReader(final String municipalityId, final MeasurementDataSearchParameters parameters) {
		// Fetch raw per-facility values (display = null); aggregation across the full period is reconstructed in-app.
		return dataWarehouseReaderClient.getMeasurements(
			municipalityId,
			parameters.getCategory().name(),
			parameters.getAggregateOn().name(),
			parameters.getPartyId(),
			parameters.getFacilityIds(),
			asEncodedString(cutoffInstant()),
			asEncodedString(parameters.getToDate()),
			null);
	}

	private boolean usesBfus(final MeasurementDataSearchParameters parameters) {
		return (parameters.getCategory() == ELECTRICITY)
			&& (parameters.getAggregateOn() == QUARTER)
			&& (parameters.getFromDate() != null)
			&& parameters.getFromDate().isBefore(cutoffInstant());
	}

	private boolean spansCutoff(final MeasurementDataSearchParameters parameters) {
		return Optional.ofNullable(parameters.getToDate())
			.map(toDate -> !toDate.isBefore(cutoffInstant()))
			.orElse(true);
	}

	private OffsetDateTime cutoffInstant() {
		return bfusProperties.cutoffDate().atStartOfDay(ZONE).toOffsetDateTime();
	}

	private static LocalDate toLocalDate(final OffsetDateTime dateTime) {
		return Optional.ofNullable(dateTime)
			.map(value -> value.atZoneSameInstant(ZONE).toLocalDate())
			.orElse(null);
	}

	// BFUS dateTo is exclusive, so the requested (inclusive) end day is covered by querying the following day.
	private static LocalDate exclusiveEndDate(final OffsetDateTime toDate) {
		return Optional.ofNullable(toLocalDate(toDate))
			.map(date -> date.plusDays(1))
			.orElse(null);
	}

	private static LocalDate earliest(final LocalDate candidate, final LocalDate fallback) {
		if ((candidate != null) && candidate.isBefore(fallback)) {
			return candidate;
		}
		return fallback;
	}

	private static String asEncodedString(final OffsetDateTime offsetDateTime) {
		return Optional.ofNullable(offsetDateTime)
			.map(dateTime -> encode(dateTime.toString(), defaultCharset()))
			.orElse(null);
	}

	private static String asEnumName(final Enum<?> value) {
		return Optional.ofNullable(value)
			.map(Enum::name)
			.orElse(null);
	}
}
