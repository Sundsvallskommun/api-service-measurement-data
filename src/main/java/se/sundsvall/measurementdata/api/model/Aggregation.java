package se.sundsvall.measurementdata.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Aggregation granularity", example = "HOUR")
public enum Aggregation {
    HOUR,
    DAY,
    MONTH,
    YEAR
}
