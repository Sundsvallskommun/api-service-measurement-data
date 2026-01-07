package se.sundsvall.measurementdata.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Aggregation granularity", examples = "HOUR")
public enum Aggregation {
	QUARTER,
	HOUR,
	DAY,
	MONTH
}
