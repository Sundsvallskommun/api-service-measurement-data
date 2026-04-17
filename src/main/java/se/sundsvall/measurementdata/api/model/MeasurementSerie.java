package se.sundsvall.measurementdata.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Schema(description = "Measurement from a single source")
public class MeasurementSerie {

	@Schema(description = "Facility ID for non-aggregated series, null for aggregated", accessMode = READ_ONLY, examples = "735999109151605013")
	private String facilityId;

	@Schema(description = "Unit of all measurement points", accessMode = READ_ONLY, examples = "m3")
	private String unit;

	@Schema(description = "Type of measurement", accessMode = READ_ONLY, examples = "volume")
	private String measurementType;

	@ArraySchema(schema = @Schema(name = "metaData", implementation = MetaData.class, accessMode = READ_ONLY))
	private List<MetaData> metaData;

	@ArraySchema(schema = @Schema(name = "measurementPoints", implementation = MeasurementPoint.class, accessMode = READ_ONLY))
	private List<MeasurementPoint> measurementPoints;

	public static MeasurementSerie create() {
		return new MeasurementSerie();
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public MeasurementSerie withFacilityId(String facilityId) {
		this.facilityId = facilityId;
		return this;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public MeasurementSerie withUnit(String unit) {
		this.unit = unit;
		return this;
	}

	public String getMeasurementType() {
		return measurementType;
	}

	public void setMeasurementType(String measurementType) {
		this.measurementType = measurementType;
	}

	public MeasurementSerie withMeasurementType(String measurementType) {
		this.measurementType = measurementType;
		return this;
	}

	public List<MetaData> getMetaData() {
		return metaData;
	}

	public void setMetaData(List<MetaData> metaData) {
		this.metaData = metaData;
	}

	public MeasurementSerie withMetaData(List<MetaData> metaData) {
		this.metaData = metaData;
		return this;
	}

	public List<MeasurementPoint> getMeasurementPoints() {
		return measurementPoints;
	}

	public void setMeasurementPoints(List<MeasurementPoint> measurementPoints) {
		this.measurementPoints = measurementPoints;
	}

	public MeasurementSerie withMeasurementPoints(List<MeasurementPoint> measurementPoints) {
		this.measurementPoints = measurementPoints;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(facilityId, measurementPoints, measurementType, metaData, unit);
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
		MeasurementSerie other = (MeasurementSerie) obj;
		return Objects.equals(facilityId, other.facilityId) && Objects.equals(measurementPoints, other.measurementPoints) && Objects.equals(measurementType, other.measurementType) && Objects.equals(metaData, other.metaData) && Objects.equals(unit,
			other.unit);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MeasurementSerie [facilityId=").append(facilityId).append(", unit=").append(unit).append(", measurementType=").append(measurementType).append(", metaData=").append(metaData).append(", measurementPoints=").append(measurementPoints)
			.append("]");
		return builder.toString();
	}
}
