package se.sundsvall.measurementdata.service.mapper;

import generated.se.sundsvall.datawarehousereader.Measurement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import se.sundsvall.measurementdata.api.model.Data;
import se.sundsvall.measurementdata.api.model.MeasurementDataSearchParameters;
import se.sundsvall.measurementdata.api.model.MeasurementPoint;
import se.sundsvall.measurementdata.api.model.MeasurementSerie;
import se.sundsvall.measurementdata.api.model.MetaData;

import static java.util.Collections.emptyList;

public class DataWarehouseReaderMapper {
	private static final String KEY_INTERPOLATION = "interpolation";

	private DataWarehouseReaderMapper() {}

	public static Data toData(final MeasurementDataSearchParameters parameters, final List<Measurement> measurements) {
		return Data.create()
			.withAggregateOn(parameters.getAggregateOn())
			.withCategory(parameters.getCategory())
			.withFacilityIds(parameters.getFacilityIds())
			.withFromDate(parameters.getFromDate())
			.withToDate(parameters.getToDate())
			.withMeasurementSeries(toMeasurementSeries(measurements));
	}

	private static List<MeasurementSerie> toMeasurementSeries(final List<Measurement> measurements) {
		if (Objects.isNull(measurements) || measurements.isEmpty()) {
			return emptyList();
		}

		final var series = new HashMap<String, MeasurementSerie>();
		measurements.forEach(measurement -> handleMeasurement(series, measurement));

		return List.copyOf(series.values());
	}

	private static void handleMeasurement(final Map<String, MeasurementSerie> series, final Measurement measurement) {
		final var feedType = measurement.getFeedType();
		final var isAggregated = feedType != null && feedType.endsWith("_aggregated");
		var key = isAggregated
			? feedType + measurement.getUnit()
			: feedType + measurement.getUnit() + measurement.getFacilityId();

		if (!series.containsKey(key)) {
			final var serie = MeasurementSerie.create()
				.withMeasurementType(feedType)
				.withMeasurementPoints(new ArrayList<>())
				.withUnit(measurement.getUnit());

			if (!isAggregated) {
				serie.withFacilityId(measurement.getFacilityId());
			}

			series.put(key, serie);
		}

		addMeasurementPoint(series.get(key), measurement);
	}

	private static void addMeasurementPoint(final MeasurementSerie series, final Measurement measurement) {
		series.getMeasurementPoints().add(MeasurementPoint.create()
			.withMetaData(toMetaData(measurement))
			.withTimestamp(measurement.getDateAndTime())
			.withValue(measurement.getUsage()));
	}

	private static List<MetaData> toMetaData(final Measurement measurement) {
		if (measurement.getInterpolation() != null && measurement.getInterpolation() > 0) {
			return List.of(MetaData.create()
				.withKey(KEY_INTERPOLATION)
				.withValue(String.valueOf(measurement.getInterpolation())));
		}
		return null;
	}

}
