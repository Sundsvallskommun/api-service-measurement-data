package se.sundsvall.measurementdata.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import java.util.List;
import java.util.Objects;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Measurement from a single source")
public class MeasurementSerie {

	@Schema(description = "Unit of all measurement points", accessMode = READ_ONLY, example = "m3")
	private String unit;

	@Schema(description = "Type of measurement", accessMode = READ_ONLY, example = "volume")
	private String measurementType;

	@ArraySchema(schema = @Schema(name = "metaData", implementation = MetaData.class, accessMode = READ_ONLY))
	private List<MetaData> metaData;

	@ArraySchema(schema = @Schema(name = "measurementPoints", implementation = MeasurementPoint.class, accessMode = READ_ONLY))
	private List<MeasurementPoint> measurementPoints;

	public static MeasurementSerie create() {
		return new MeasurementSerie();
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
		return Objects.hash(measurementPoints, measurementType, metaData, unit);
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
		return Objects.equals(measurementPoints, other.measurementPoints) && Objects.equals(measurementType, other.measurementType) && Objects.equals(metaData, other.metaData) && Objects.equals(unit, other.unit);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MeasurementSerie [unit=").append(unit).append(", measurementType=").append(measurementType).append(", metaData=").append(metaData).append(", measurementPoints=").append(measurementPoints).append("]");
		return builder.toString();
	}
}
