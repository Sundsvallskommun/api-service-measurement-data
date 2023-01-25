package se.sundsvall.measurementdata.api.model;

import java.time.OffsetDateTime;
import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

@Schema(description = "Measurement date request parameters")
public class MeasurementDataSearchParameters {

	@ValidUuid
	@Schema(description = "Party ID, either private or enterprise uuid", required = true, example = "81471222-5798-11e9-ae24-57fa13b361e1")
	private String partyId;

	@Schema(required = true)
	@NotNull
	private Category category;

	@Schema(description = "Facility ID", required = true, example = "112233")
	@NotBlank
	private String facilityId;

	@Schema(description = "From date", required = true, example = "2021-01-31")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@NotNull
	private OffsetDateTime fromDate;

	@Schema(description = "To date", required = true, example = "2022-02-28")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@NotNull
	private OffsetDateTime toDate;

	@Schema(description = "Data point aggregation granularity", required = true)
	@NotNull
	private Aggregation aggregateOn;

	public static MeasurementDataSearchParameters create() {
		return new MeasurementDataSearchParameters();
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public MeasurementDataSearchParameters withPartyId(String partyId) {
		this.partyId = partyId;
		return this;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public MeasurementDataSearchParameters withCategory(Category category) {
		this.category = category;
		return this;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public MeasurementDataSearchParameters withFacilityId(String facilityId) {
		this.facilityId = facilityId;
		return this;
	}

	public OffsetDateTime getFromDate() {
		return fromDate;
	}

	public void setFromDate(OffsetDateTime fromDate) {
		this.fromDate = fromDate;
	}

	public MeasurementDataSearchParameters withFromDate(OffsetDateTime fromDate) {
		this.fromDate = fromDate;
		return this;
	}

	public OffsetDateTime getToDate() {
		return toDate;
	}

	public void setToDate(OffsetDateTime toDate) {
		this.toDate = toDate;
	}

	public MeasurementDataSearchParameters withToDate(OffsetDateTime toDate) {
		this.toDate = toDate;
		return this;
	}

	public Aggregation getAggregateOn() {
		return aggregateOn;
	}

	public void setAggregateOn(Aggregation aggregation) {
		this.aggregateOn = aggregation;
	}

	public MeasurementDataSearchParameters withAggregateOn(Aggregation aggregateOn) {
		this.aggregateOn = aggregateOn;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(aggregateOn, category, facilityId, fromDate, partyId, toDate);
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
		MeasurementDataSearchParameters other = (MeasurementDataSearchParameters) obj;
		return aggregateOn == other.aggregateOn && category == other.category && Objects.equals(facilityId, other.facilityId) && Objects.equals(fromDate, other.fromDate) && Objects.equals(partyId, other.partyId) && Objects.equals(toDate,
			other.toDate);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MeasurementDataSearchParameters [partyId=").append(partyId).append(", category=").append(category).append(", facilityId=").append(facilityId).append(", fromDate=").append(fromDate).append(", toDate=").append(toDate).append(
			", aggregateOn=").append(aggregateOn).append("]");
		return builder.toString();
	}
}
