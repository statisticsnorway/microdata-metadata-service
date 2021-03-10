package no.microdata.datastore.repository;

import no.microdata.datastore.model.EventQuery;
import no.microdata.datastore.model.FixedQuery;
import no.microdata.datastore.model.StatusQuery;

import java.util.Map;

public interface DataServiceRepository {

    Map<String, String> findByTime(StatusQuery query);

    Map<String, String> findByFixed(FixedQuery query);

    Map<String, String> findByTimePeriod(EventQuery query);
}
