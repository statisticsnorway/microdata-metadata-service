package no.microdata.datastore.model;

import java.time.LocalDate;
import java.util.Map;
import java.util.StringJoiner;

public class StatusQuery {

    DatasetRevision datasetRevision;
    long date;
    String requestId;
    UnitIdFilter unitIdFilter;
    IntervalFilter intervalFilter;
    ValueFilter valueFilter;
    Boolean includeAttributes;

    public StatusQuery(Map inputFields) {

        if(!hasRequiredFields(
                (DatasetRevision)inputFields.get("datasetRevision"),
                (long)inputFields.get("date"))) {
            throw new AssertionError(
                    String.format(
                            "Missing required field. Fields datasetRevision = %s, date = %s",
                            inputFields.get("datasetRevision"), inputFields.get("date")));
        }

        datasetRevision = (DatasetRevision)inputFields.get("datasetRevision");
        date = (long)inputFields.get("date");
        requestId = (String)inputFields.get("requestId");
        intervalFilter = (IntervalFilter)inputFields.getOrDefault("intervalFilter", IntervalFilter.fullIntervalInstance());
        unitIdFilter = (UnitIdFilter)inputFields.getOrDefault("unitIdFilter", UnitIdFilter.noFilterInstance());
        valueFilter = (ValueFilter)inputFields.getOrDefault("valueFilter", ValueFilter.noFilterInstance());
        includeAttributes = (Boolean)inputFields.getOrDefault("includeAttributes", false);
    }

    private boolean hasRequiredFields(DatasetRevision datasetRevision, Long date) {
        return datasetRevision != null && date != null;
    }

    boolean hasValueFilter(){
        return valueFilter.valueFilter().size() > 0;
    }

    boolean hasUnitIdFilter() {
        return unitIdFilter.unitIds().size() > 0;
    }

    public UnitIdFilter getUnitIdFilter() {
        return unitIdFilter;
    }

    public Boolean getIncludeAttributes() {
        return includeAttributes;
    }

    public DatasetRevision getDatasetRevision() {
        return datasetRevision;
    }

    public ValueFilter getValueFilter() {
        return valueFilter;
    }

    public long getDate() {
        return date;
    }

    public IntervalFilter getIntervalFilter() {
        return intervalFilter;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StatusQuery.class.getSimpleName() + "[", "]")
                .add("date=" + date)
                .add("requestId='" + requestId + "'")
                .add("datasetRevision=" + datasetRevision)
                .add("unitIdFilter=" + unitIdFilter)
                .add("intervalFilter=" + intervalFilter)
                .add("valueFilter=" + valueFilter)
                .add("includeAttributes=" + includeAttributes)
                .toString();
    }

}
