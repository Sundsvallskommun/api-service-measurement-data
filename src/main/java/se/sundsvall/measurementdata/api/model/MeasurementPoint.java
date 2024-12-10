package se.sundsvall.measurementdata.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.format.annotation.DateTimeFormat;

@Schema(description = "A single measurement data point")
public class MeasurementPoint {

	@Schema(description = "Value of the point", accessMode = READ_ONLY, example = "22.321")
	private BigDecimal value;

	@Schema(description = "Timestamp of the datapoint", accessMode = READ_ONLY)
	@DateTimeFormat(iso = DATE_TIME)
	private OffsetDateTime timestamp;

	@ArraySchema(schema = @Schema(name = "metaData", implementation = MetaData.class, accessMode = READ_ONLY))
	private List<MetaData> metaData;

	public static MeasurementPoint create() {
		return new MeasurementPoint();
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public MeasurementPoint withValue(BigDecimal value) {
		this.value = value;
		return this;
	}

	public OffsetDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(OffsetDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public MeasurementPoint withTimestamp(OffsetDateTime timestamp) {
		this.timestamp = timestamp;
		return this;
	}

	public List<MetaData> getMetaData() {
		return metaData;
	}

	public void setMetaData(List<MetaData> metaData) {
		this.metaData = metaData;
	}

	public MeasurementPoint withMetaData(List<MetaData> metaData) {
		this.metaData = metaData;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(metaData, timestamp, value);
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
		MeasurementPoint other = (MeasurementPoint) obj;
		return Objects.equals(metaData, other.metaData) && Objects.equals(timestamp, other.timestamp) && Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MeasurementPoint [value=").append(value).append(", timestamp=").append(timestamp).append(", metaData=").append(metaData).append("]");
		return builder.toString();
	}
}
