package se.sundsvall.measurementdata.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Measurement date request parameters")
@ParameterObject
public class MeasurementDataSearchParameters {

	@ValidUuid
	@Schema(description = "Party ID, either private or enterprise uuid", examples = "81471222-5798-11e9-ae24-57fa13b361e1", requiredMode = REQUIRED)
	private String partyId;

	@Schema(requiredMode = REQUIRED)
	@NotNull
	private Category category;

	@Schema(description = "Facility ID (one or more)", examples = {
		"112233", "445566"
	}, requiredMode = REQUIRED)
	@NotEmpty
	@JsonProperty("facilityId")
	private List<@NotBlank String> facilityIds;

	@Schema(description = "From date", examples = "2021-01-31", requiredMode = REQUIRED)
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@NotNull
	private OffsetDateTime fromDate;

	@Schema(description = "To date", examples = "2022-02-28", requiredMode = REQUIRED)
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@NotNull
	private OffsetDateTime toDate;

	@Schema(description = "Data point aggregation granularity", requiredMode = REQUIRED)
	@NotNull
	private Aggregation aggregateOn;

	@Schema(description = "Display mode for aggregated series")
	private Display display;

	public static MeasurementDataSearchParameters create() {
		return new MeasurementDataSearchParameters();
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(final String partyId) {
		this.partyId = partyId;
	}

	public MeasurementDataSearchParameters withPartyId(final String partyId) {
		this.partyId = partyId;
		return this;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(final Category category) {
		this.category = category;
	}

	public MeasurementDataSearchParameters withCategory(final Category category) {
		this.category = category;
		return this;
	}

	public List<String> getFacilityIds() {
		return facilityIds;
	}

	public void setFacilityIds(final List<String> facilityIds) {
		this.facilityIds = facilityIds;
	}

	public void setFacilityId(final List<String> facilityIds) {
		this.facilityIds = facilityIds;
	}

	public MeasurementDataSearchParameters withFacilityIds(final List<String> facilityIds) {
		this.facilityIds = facilityIds;
		return this;
	}

	public OffsetDateTime getFromDate() {
		return fromDate;
	}

	public void setFromDate(final OffsetDateTime fromDate) {
		this.fromDate = fromDate;
	}

	public MeasurementDataSearchParameters withFromDate(final OffsetDateTime fromDate) {
		this.fromDate = fromDate;
		return this;
	}

	public OffsetDateTime getToDate() {
		return toDate;
	}

	public void setToDate(final OffsetDateTime toDate) {
		this.toDate = toDate;
	}

	public MeasurementDataSearchParameters withToDate(final OffsetDateTime toDate) {
		this.toDate = toDate;
		return this;
	}

	public Aggregation getAggregateOn() {
		return aggregateOn;
	}

	public void setAggregateOn(final Aggregation aggregation) {
		this.aggregateOn = aggregation;
	}

	public MeasurementDataSearchParameters withAggregateOn(final Aggregation aggregateOn) {
		this.aggregateOn = aggregateOn;
		return this;
	}

	public Display getDisplay() {
		return display;
	}

	public void setDisplay(final Display display) {
		this.display = display;
	}

	public MeasurementDataSearchParameters withDisplay(final Display display) {
		this.display = display;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(aggregateOn, category, display, facilityIds, fromDate, partyId, toDate);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final MeasurementDataSearchParameters other = (MeasurementDataSearchParameters) obj;
		return aggregateOn == other.aggregateOn && category == other.category && display == other.display && Objects.equals(facilityIds, other.facilityIds) && Objects.equals(fromDate, other.fromDate) && Objects.equals(partyId, other.partyId) && Objects
			.equals(toDate,
				other.toDate);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("MeasurementDataSearchParameters [partyId=").append(partyId).append(", category=").append(category).append(", facilityIds=").append(facilityIds).append(", fromDate=").append(fromDate).append(", toDate=").append(toDate).append(
			", aggregateOn=").append(aggregateOn).append(", display=").append(display).append("]");
		return builder.toString();
	}
}
