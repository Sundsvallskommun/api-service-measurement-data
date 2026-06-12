package se.sundsvall.measurementdata.service.mapper;

import generated.se.sundsvall.datawarehousereader.Measurement;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import se.sundsvall.measurementdata.integration.bfus.model.ConsumptionResponse;
import se.sundsvall.measurementdata.integration.bfus.model.MeterReadingPart;
import se.sundsvall.measurementdata.integration.bfus.model.PeriodicValue;

/**
 * Maps a BFUS {@link ConsumptionResponse} into the same internal {@link Measurement} representation that
 * DataWarehouseReader produces, so the existing {@link DataWarehouseReaderMapper} and aggregation can be reused
 * unchanged. BFUS electricity consumption is normalised to {@code feedType=Energy} / {@code unit=kWh}.
 */
public final class BfusMapper {

	static final String FEED_TYPE_ENERGY = "Energy";
	private static final String DEFAULT_UNIT = "kWh";
	private static final int INTERPOLATION_MEASURED = 0;
	private static final ZoneId STOCKHOLM = ZoneId.of("Europe/Stockholm");

	private BfusMapper() {}

	public static List<Measurement> toMeasurements(final String facilityId, final ConsumptionResponse response) {
		return Optional.ofNullable(response)
			.map(ConsumptionResponse::content)
			.map(ConsumptionResponse.Content::meterReadingParts)
			.orElseGet(List::of)
			.stream()
			.filter(Objects::nonNull)
			.flatMap(part -> toMeasurements(facilityId, part).stream())
			.toList();
	}

	private static List<Measurement> toMeasurements(final String facilityId, final MeterReadingPart part) {
		final var unit = Optional.ofNullable(part.unit()).orElse(DEFAULT_UNIT);
		return Optional.ofNullable(part.periodicValues())
			.orElseGet(List::of)
			.stream()
			.filter(Objects::nonNull)
			.map(value -> toMeasurement(facilityId, unit, value))
			.toList();
	}

	private static Measurement toMeasurement(final String facilityId, final String unit, final PeriodicValue value) {
		return new Measurement()
			.facilityId(facilityId)
			.feedType(FEED_TYPE_ENERGY)
			.unit(unit)
			.usage(value.consumption())
			.dateAndTime(toOffsetDateTime(value.fromDate()))
			.interpolation(INTERPOLATION_MEASURED);
	}

	/**
	 * BFUS is queried with {@code isLocalTime=true}, so {@code FromDate} is a local (Europe/Stockholm) timestamp without
	 * offset, which we resolve to the correct offset via the zone. Known limitation: on the autumn DST fall-back day the
	 * repeated local hour cannot be disambiguated (both readings resolve to the earlier offset); acceptable for these
	 * historical values. Switch the query to {@code isLocalTime=false} (UTC) if exact disambiguation is ever required.
	 */
	private static OffsetDateTime toOffsetDateTime(final String fromDate) {
		return Optional.ofNullable(fromDate)
			.map(LocalDateTime::parse)
			.map(localDateTime -> localDateTime.atZone(STOCKHOLM).toOffsetDateTime())
			.orElse(null);
	}
}
