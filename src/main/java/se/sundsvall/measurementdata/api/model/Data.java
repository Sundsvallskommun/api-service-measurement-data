package se.sundsvall.measurementdata.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.format.annotation.DateTimeFormat;

@Schema(description = "Measurement data for a category")
public class Data {

	@Schema(accessMode = READ_ONLY, enumAsRef = true)
	private Category category;

	@Schema(accessMode = READ_ONLY, example = "1234567")
	private String facilityId;

	@Schema(accessMode = READ_ONLY, enumAsRef = true)
	private Aggregation aggregateOn;

	@Schema(description = "From date")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private OffsetDateTime fromDate;

	@Schema(description = "To date")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private OffsetDateTime toDate;

	@ArraySchema(schema = @Schema(implementation = MeasurementSerie.class, accessMode = READ_ONLY))
	private List<MeasurementSerie> measurementSeries;

	public static Data create() {
		return new Data();
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Data withCategory(Category category) {
		this.category = category;
		return this;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public Data withFacilityId(String facilityId) {
		this.facilityId = facilityId;
		return this;
	}

	public Aggregation getAggregateOn() {
		return aggregateOn;
	}

	public void setAggregateOn(Aggregation aggregateOn) {
		this.aggregateOn = aggregateOn;
	}

	public Data withAggregateOn(Aggregation aggregateOn) {
		this.aggregateOn = aggregateOn;
		return this;
	}

	public List<MeasurementSerie> getMeasurementSeries() {
		return measurementSeries;
	}

	public void setMeasurementSeries(List<MeasurementSerie> measurementSeries) {
		this.measurementSeries = measurementSeries;
	}

	public Data withMeasurementSeries(List<MeasurementSerie> measurementSeries) {
		this.measurementSeries = measurementSeries;
		return this;
	}

	public OffsetDateTime getFromDate() {
		return fromDate;
	}

	public void setFromDate(OffsetDateTime fromDate) {
		this.fromDate = fromDate;
	}

	public Data withFromDate(OffsetDateTime fromDate) {
		this.fromDate = fromDate;
		return this;
	}

	public OffsetDateTime getToDate() {
		return toDate;
	}

	public void setToDate(OffsetDateTime toDate) {
		this.toDate = toDate;
	}

	public Data withToDate(OffsetDateTime toDate) {
		this.toDate = toDate;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(aggregateOn, category, facilityId, fromDate, measurementSeries, toDate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Data other = (Data) obj;
		return aggregateOn == other.aggregateOn && category == other.category && Objects.equals(facilityId, other.facilityId) && Objects.equals(fromDate, other.fromDate) && Objects.equals(measurementSeries, other.measurementSeries) && Objects.equals(
			toDate, other.toDate);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Data [category=").append(category).append(", facilityId=").append(facilityId).append(", aggregateOn=").append(aggregateOn).append(", fromDate=").append(fromDate).append(", toDate=").append(toDate).append(
			", measurementSeries=").append(measurementSeries).append("]");
		return builder.toString();
	}
}
