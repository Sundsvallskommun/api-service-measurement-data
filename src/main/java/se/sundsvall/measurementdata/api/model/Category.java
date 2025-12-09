package se.sundsvall.measurementdata.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Category", examples = "DISTRICT_HEATING")
public enum Category {
	DISTRICT_HEATING,
	ELECTRICITY,
	COMMUNICATION,
	WASTE_MANAGEMENT
}
