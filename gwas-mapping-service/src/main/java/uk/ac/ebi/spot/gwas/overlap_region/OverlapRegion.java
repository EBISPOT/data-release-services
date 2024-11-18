package uk.ac.ebi.spot.gwas.overlap_region;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@JsonPropertyOrder({
        "strand",
        "feature_type",
        "start",
        "assembly_name",
        "end",
        "id",
        "seq_region_name",
        "stain"
})

//@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OverlapRegion implements Serializable {


    private static final long serialVersionUID = -8954532137311559917L;

    @JsonProperty("strand")
    public Integer strand;

    @JsonProperty("feature_type")
    public String featureType;

    @JsonProperty("start")
    public Integer start;

    @JsonProperty("assembly_name")
    public String assemblyName;

    @JsonProperty("end")
    public Integer end;

    @JsonProperty("id")
    public String id;

    @JsonProperty("seq_region_name")
    public String seqRegionName;

    @JsonProperty("stain")
    public String stain;

    @JsonProperty("error")
    public String error;


    public OverlapRegion(@JsonProperty("strand") Integer strand,
                         @JsonProperty("feature_type") String featureType,
                         @JsonProperty("start") Integer start,
                         @JsonProperty("assembly_name") String assemblyName,
                         @JsonProperty("end") Integer end,
                         @JsonProperty("id") String id,
                         @JsonProperty("seq_region_name") String seqRegionName,
                         @JsonProperty("stain") String stain,
                         @JsonProperty("error") String error) {
        this.strand = strand;
        this.featureType = featureType;
        this.start = start;
        this.assemblyName = assemblyName;
        this.end = end;
        this.id = id;
        this.seqRegionName = seqRegionName;
        this.stain = stain;
        this.error = error;
    }

    public Integer getStrand() {
        return strand;
    }

    public void setStrand(Integer strand) {
        this.strand = strand;
    }

    public String getFeatureType() {
        return featureType;
    }

    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public String getAssemblyName() {
        return assemblyName;
    }

    public void setAssemblyName(String assemblyName) {
        this.assemblyName = assemblyName;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeqRegionName() {
        return seqRegionName;
    }

    public void setSeqRegionName(String seqRegionName) {
        this.seqRegionName = seqRegionName;
    }

    public String getStain() {
        return stain;
    }

    public void setStain(String stain) {
        this.stain = stain;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public OverlapRegion(@JsonProperty("error") String error) {
        this.error = error;
    }
}
