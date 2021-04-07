package no.microdata.datastore.adapters.api.dto;

import no.microdata.datastore.exceptions.BadRequestException;
import no.microdata.datastore.exceptions.UnauthorizedException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static no.microdata.datastore.transformations.Utils.*;
import static no.microdata.datastore.adapters.api.ErrorMessage.*;

public abstract class InputQuery {

    String dataStructureName;
    String version;

    Collection<String> values;
    Map<String, Set<Long>> population;
    String intervalFilter;

    Credentials credentials;
    Boolean includeAttributes = false;

    boolean validate() {
        if (!credentials.isValid())
            throw new UnauthorizedException(Credentials.ERROR_MESSAGE);
        if (isNullOrEmptyOrNegative(dataStructureName))
            throw  new BadRequestException(requestValidationError(INPUT_FIELD_DATASTRUCTURE_NAME));
        if (isNullOrEmptyOrNegative(version))
            throw  new BadRequestException(requestValidationError(INPUT_FIELD_VERSION));
        if ( ! isSemanticVersion(version))
            throw new BadRequestException(versionValidationError(version));
        return true;
    }

    public boolean hasValueFilter(){
        return values != null && values.size() > 0;
    }

    boolean hasUnitIdFilter(){
        return populationFilter().size() > 0;
    }

    public boolean hasIntervalFilter(){
        return intervalFilter != null && intervalFilter.length() > 0;
    }

    public Set<Long> populationFilter(){
        return population == null ? new HashSet<Long>() : population.getOrDefault("unitIds", new HashSet<Long>());
    }

    private static final String SEMVER_REG_EXP = "^([0-9]+)\\.([0-9]+)\\.([0-9]+)\\.([0-9]+)$";
    private static final Pattern pattern = Pattern.compile(SEMVER_REG_EXP);

    static final boolean isSemanticVersion(String version) {
        Matcher matcher = pattern.matcher(version);
        return matcher.find();
    }

    public String getDataStructureName() {
        return dataStructureName;
    }

    public void setDataStructureName(String dataStructureName) {
        this.dataStructureName = dataStructureName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Collection<String> getValues() {
        return values;
    }

    public void setValues(Collection<String> values) {
        this.values = values;
    }

    public Map<String, Set<Long>> getPopulation() {
        return population;
    }

    public void setPopulation(Map<String, Set<Long>> population) {
        this.population = population;
    }

    public String getIntervalFilter() {
        return intervalFilter;
    }

    public void setIntervalFilter(String intervalFilter) {
        this.intervalFilter = intervalFilter;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public Boolean getIncludeAttributes() {
        return includeAttributes;
    }

    public void setIncludeAttributes(Boolean includeAttributes) {
        this.includeAttributes = includeAttributes;
    }
}