package no.microdata.datastore.adapters.api.dto;

import no.microdata.datastore.exceptions.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringJoiner;

import static no.microdata.datastore.transformations.Utils.*;
import static no.microdata.datastore.adapters.api.ErrorMessage.*;

public class InputTimeQuery extends InputQuery{

    final static Logger log = LoggerFactory.getLogger(InputTimeQuery.class);
    Long date;

    /**
     * Validates the input query.
     *
     * @return true if time query is valid, otherwise throws BadRequestException
     * @throws BadRequestException if not valid
     */
    public boolean validate() {
        super.validate();
        if (isNullOrEmptyOrNegative(date))
            throw  new BadRequestException(requestValidationError(INPUT_FIELD_START_DATE));
        return true;
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", InputTimeQuery.class.getSimpleName() + "[", "]")
                .add("date=" + date)
                .add("dataStructureName='" + dataStructureName + "'")
                .add("version='" + version + "'")
                .add("values=" + (values!=null ? values.size() : "null"))
                .add("population=" + populationFilter().size())
                .add("intervalFilter='" + intervalFilter + "'")
                .add("includeAttributes=" + includeAttributes)
                .toString();
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}