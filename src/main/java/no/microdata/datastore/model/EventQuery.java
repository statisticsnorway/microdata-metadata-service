package no.microdata.datastore.model;

import java.time.LocalDate;
import java.util.Map;
import java.util.StringJoiner;

public class EventQuery {

    long startDate;
    long endDate;
    String requestId;
    DatasetRevision datasetRevision;
    UnitIdFilter unitIdFilter;
    IntervalFilter intervalFilter;
    ValueFilter valueFilter;
    Boolean includeAttributes;

    EventQuery(long startDate){
        this.startDate = startDate;
    }

    public EventQuery(Map inputFields) {

        if(!hasRequiredFields(
                (DatasetRevision)inputFields.get("datasetRevision"),
                (long)inputFields.get("startDate"),
                (long)inputFields.get("endDate"))) {
            throw new AssertionError(
                    String.format(
                            "Missing required field. Fields datasetRevision = %s, startDate = %s and endDate = %s",
                            inputFields.get("datasetRevision"), inputFields.get("startDate"), inputFields.get("endDate")));
        }

        datasetRevision = (DatasetRevision)inputFields.get("datasetRevision");
        startDate = (long)inputFields.get("startDate");
        endDate = (long)inputFields.get("endDate");
        requestId = (String)inputFields.get("requestId");
        intervalFilter = (IntervalFilter)inputFields.getOrDefault("intervalFilter", IntervalFilter.fullIntervalInstance());
        unitIdFilter = (UnitIdFilter)inputFields.getOrDefault("unitIdFilter", UnitIdFilter.noFilterInstance());
        valueFilter = (ValueFilter)inputFields.getOrDefault("valueFilter", ValueFilter.noFilterInstance());
        includeAttributes = (Boolean)inputFields.getOrDefault("includeAttributes", false);
    }

    private boolean hasRequiredFields(DatasetRevision datasetRevision, Long startDate, Long endDate) {
        return datasetRevision != null && startDate != null && endDate != null;
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

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public DatasetRevision getDatasetRevision() {
        return datasetRevision;
    }

    public IntervalFilter getIntervalFilter() {
        return intervalFilter;
    }

    public ValueFilter getValueFilter() {
        return valueFilter;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", EventQuery.class.getSimpleName() + "[", "]")
                .add("startDate=" + startDate)
                .add("endDate=" + endDate)
                .add("requestId='" + requestId + "'")
                .add("datasetRevision=" + datasetRevision)
                .add("unitIdFilter=" + unitIdFilter)
                .add("intervalFilter=" + intervalFilter)
                .add("valueFilter=" + valueFilter)
                .add("includeAttributes=" + includeAttributes)
                .toString();
    }

}
