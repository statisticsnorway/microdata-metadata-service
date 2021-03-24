package no.microdata.datastore.adapters.api.dto;

import no.microdata.datastore.exceptions.BadRequestException;
import no.microdata.datastore.exceptions.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static no.microdata.datastore.transformations.Utils.*;
import static no.microdata.datastore.adapters.api.ErrorMessage.*;

public class InputTimePeriodQuery extends InputQuery{

    final static Logger log = LoggerFactory.getLogger(InputTimePeriodQuery.class);
    Long startDate;
    Long stopDate;

    /**
     * Validates the input query.
     *
     * @return true if time query is valid, otherwise throws BadRequestException or UnauthorizedException
     * @throws BadRequestException if not valid
     * @throws UnauthorizedException if authentication fails
     */
    public boolean validate() {
        super.validate();
        if (isNullOrEmptyOrNegative(startDate))
            throw  new BadRequestException(requestValidationError(INPUT_FIELD_START_DATE));
        if (isNullOrEmptyOrNegative(stopDate))
            throw  new BadRequestException(requestValidationError(INPUT_FIELD_STOP_DATE));
        return true;
    }

    @Override
    public String toString(){
        String string = String.format("{ dataStructureName: %1$s, startDate: %2$d, stopDate: %3$d, version: %4$s", dataStructureName, startDate, stopDate, version);

        if(hasValueFilter()){
            string.concat(String.format(", values.size(): %1$d", values.size()));
        }
        if(hasUnitIdFilter()){
            string.concat(String.format(", population filter size:  %1$d", populationFilter().size()));
        }
        if (hasIntervalFilter()){
            string.concat(String.format(", interval filter: %1$s", intervalFilter));
        }
        if (includeAttributes != null && includeAttributes){
            string.concat(String.format(", includeAttributes: %1$b", includeAttributes));
        }
        return string.concat(" }");
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getStopDate() {
        return stopDate;
    }

    public void setStopDate(Long stopDate) {
        this.stopDate = stopDate;
    }
}