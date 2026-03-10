package se.sundsvall.measurementdata.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Display mode for aggregated series", examples = "AGGREGATE")
public enum Display {
	AGGREGATE,
	ONLYAGGREGATED
}
