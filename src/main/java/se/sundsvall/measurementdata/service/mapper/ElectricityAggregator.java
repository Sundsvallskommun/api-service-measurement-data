package se.sundsvall.measurementdata.service.mapper;

import generated.se.sundsvall.datawarehousereader.Measurement;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import se.sundsvall.measurementdata.api.model.Display;

import static java.util.Comparator.nullsLast;
import static se.sundsvall.measurementdata.service.mapper.BfusMapper.FEED_TYPE_ENERGY;

/**
 * Reconstructs the cross-facility aggregation that DataWarehouseReader normally performs server-side. Used for the
 * historical-quarter electricity path, where data originates from BFUS (one facility per call) and therefore cannot be
 * aggregated by DataWarehouseReader. Only the energy series is summed — temperature and other series are never
 * aggregated, matching DataWarehouseReader behaviour.
 */
public final class ElectricityAggregator {

	private static final String AGGREGATED_SUFFIX = "_aggregated";
	private static final int AGGREGATED_INTERPOLATION = -1;
	private static final String FACILITY_ID_DELIMITER = ",";

	private ElectricityAggregator() {}

	/**
	 * Applies the requested {@link Display} semantics:
	 * <ul>
	 * <li>{@code null} — per-facility series only (input unchanged)</li>
	 * <li>{@code AGGREGATE} — per-facility series plus a synthesised aggregated series</li>
	 * <li>{@code ONLYAGGREGATED} — only the aggregated series</li>
	 * </ul>
	 */
	public static List<Measurement> applyAggregation(final List<Measurement> measurements, final Display display) {
		if (display == null) {
			return measurements;
		}

		final var aggregated = aggregateEnergy(measurements);

		if (display == Display.ONLYAGGREGATED) {
			return aggregated;
		}

		final var result = new ArrayList<>(measurements);
		result.addAll(aggregated);
		return result;
	}

	private static List<Measurement> aggregateEnergy(final List<Measurement> measurements) {
		final var groups = new LinkedHashMap<Key, Accumulator>();

		measurements.stream()
			.filter(measurement -> FEED_TYPE_ENERGY.equals(measurement.getFeedType()))
			.forEach(measurement -> groups
				.computeIfAbsent(new Key(measurement.getUnit(), measurement.getDateAndTime()), _ -> new Accumulator())
				.add(measurement));

		return groups.entrySet().stream()
			.sorted(Comparator.comparing(entry -> entry.getKey().dateAndTime(), nullsLast(OffsetDateTime::compareTo)))
			.map(entry -> toAggregatedMeasurement(entry.getKey(), entry.getValue()))
			.toList();
	}

	private static Measurement toAggregatedMeasurement(final Key key, final Accumulator accumulator) {
		return new Measurement()
			.feedType(FEED_TYPE_ENERGY + AGGREGATED_SUFFIX)
			.unit(key.unit())
			.facilityId(String.join(FACILITY_ID_DELIMITER, accumulator.facilityIds()))
			.usage(accumulator.sum())
			.dateAndTime(key.dateAndTime())
			.interpolation(AGGREGATED_INTERPOLATION);
	}

	private record Key(String unit, OffsetDateTime dateAndTime) {}

	private static final class Accumulator {
		private final List<String> facilityIds = new ArrayList<>();
		private BigDecimal sum = BigDecimal.ZERO;

		private void add(final Measurement measurement) {
			sum = sum.add(Optional.ofNullable(measurement.getUsage()).orElse(BigDecimal.ZERO));
			Optional.ofNullable(measurement.getFacilityId())
				.filter(facilityId -> !facilityIds.contains(facilityId))
				.ifPresent(facilityIds::add);
		}

		private BigDecimal sum() {
			return sum;
		}

		private List<String> facilityIds() {
			return facilityIds;
		}
	}
}
