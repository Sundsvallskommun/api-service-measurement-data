package se.sundsvall.measurementdata.service.mapper;

import static java.util.Collections.emptyList;
import static java.util.List.copyOf;
import static java.util.Objects.isNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import generated.se.sundsvall.datawarehousereader.Aggregation;
import generated.se.sundsvall.datawarehousereader.Category;
import generated.se.sundsvall.datawarehousereader.Measurement;
import generated.se.sundsvall.datawarehousereader.MeasurementMetaData;
import generated.se.sundsvall.datawarehousereader.MeasurementResponse;
import se.sundsvall.measurementdata.api.model.Data;
import se.sundsvall.measurementdata.api.model.MeasurementDataSearchParameters;
import se.sundsvall.measurementdata.api.model.MeasurementPoint;
import se.sundsvall.measurementdata.api.model.MeasurementSerie;
import se.sundsvall.measurementdata.api.model.MetaData;

public class DataWarehouseReaderMapper {
	private static final String KEY_INTERPOLATION = "interpolation";

	private DataWarehouseReaderMapper() {}

	public static Aggregation toAggregation(se.sundsvall.measurementdata.api.model.Aggregation aggregation) {
		if (isNull(aggregation)) {
			return null;
		}

		return switch (aggregation) {
			case HOUR -> Aggregation.HOUR;
			case DAY -> Aggregation.DAY;
			case MONTH -> Aggregation.MONTH;
			case YEAR -> Aggregation.YEAR;
		};
	}

	public static Category toCategory(se.sundsvall.measurementdata.api.model.Category category) {
		if (isNull(category)) {
			return null;
		}

		return switch (category) {
			case DISTRICT_HEATING -> Category.DISTRICT_HEATING;
			case ELECTRICITY -> Category.ELECTRICITY;
			case COMMUNICATION -> Category.COMMUNICATION;
			case WASTE_MANAGEMENT -> Category.WASTE_MANAGEMENT;
		};
	}

	public static Data toData(MeasurementDataSearchParameters parameters, MeasurementResponse response) {
		return Data.create()
			.withAggregateOn(parameters.getAggregateOn())
			.withCategory(parameters.getCategory())
			.withFacilityId(parameters.getFacilityId())
			.withFromDate(parameters.getFromDate())
			.withToDate(parameters.getToDate())
			.withMeasurementSeries(toMeasurementSeries(response));
	}

	private static List<MeasurementSerie> toMeasurementSeries(MeasurementResponse response) {
		if (Objects.equals(INTEGER_ZERO, response.getMeta().getCount())) {
			return emptyList();
		}

		final var series = new HashMap<String, MeasurementSerie>();
		Optional.ofNullable(response.getMeasurements()).orElse(emptyList()).stream().forEach(measurement -> handleMeasurement(series, measurement));

		return copyOf(series.values());
	}

	private static void handleMeasurement(Map<String, MeasurementSerie> series, Measurement measurement) {
		String key = measurement.getMeasurementType() + measurement.getUnit();

		if (!series.containsKey(key)) {
			series.put(key, MeasurementSerie.create()
				.withMeasurementType(measurement.getMeasurementType())
				.withMeasurementPoints(new ArrayList<>())
				.withUnit(measurement.getUnit()));
		}

		addMeasurementPoint(series.get(key), measurement);
	}

	private static void addMeasurementPoint(MeasurementSerie serie, Measurement measurement) {
		serie.getMeasurementPoints().add(MeasurementPoint.create()
			.withMetaData(toMetaDataList(measurement))
			.withTimestamp(measurement.getTimestamp())
			.withValue(measurement.getValue()));
	}

	private static List<MetaData> toMetaDataList(Measurement measurement) {
		List<MetaData> metaData = new ArrayList<>();

		metaData.addAll(ofNullable(measurement.getMetaData()).orElse(emptyList()).stream()
			.map(DataWarehouseReaderMapper::toMetaData)
			.toList());

		of(measurement.getInterpolation())
			.filter(value -> value > 0)
			.ifPresent(value -> metaData.add(toMetaData(KEY_INTERPOLATION, String.valueOf(value))));

		return metaData.isEmpty() ? null : metaData;
	}

	private static MetaData toMetaData(MeasurementMetaData metaData) {
		return toMetaData(metaData.getKey(), metaData.getValue());
	}

	private static MetaData toMetaData(String key, String value) {
		return MetaData.create()
			.withKey(key)
			.withValue(value);
	}
}
