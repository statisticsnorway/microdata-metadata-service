package no.microdata.datastore.services;

import com.google.common.base.Stopwatch;
import no.microdata.datastore.DataService;
import no.microdata.datastore.MetadataService;
import no.microdata.datastore.adapters.api.dto.DataStoreVersionQuery;
import no.microdata.datastore.model.EventQuery;
import no.microdata.datastore.model.FixedQuery;
import no.microdata.datastore.model.MetadataQuery;
import no.microdata.datastore.model.StatusQuery;
import no.microdata.datastore.repository.DataServiceRepository;
import no.microdata.datastore.transformations.DataMappingFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
class DataServiceImpl implements DataService {

    private final static Logger log = LoggerFactory.getLogger(DataServiceImpl.class);

    @Autowired
    DataServiceRepository repository;

    @Autowired
    MetadataService metadataService;

    @Override
    public Map getEvent(MetadataQuery metadataQuery, EventQuery eventQuery){
        log.debug("Entering getEvent() with metadata query = $metadataQuery and eventQuery query = $eventQuery");

        final Stopwatch firstTimer = Stopwatch.createStarted();
        final Stopwatch secondTimer = Stopwatch.createStarted();
        final Stopwatch thirdTimer = Stopwatch.createStarted();

        Map dataStructures = metadataService.getDataStructure(metadataQuery);
        log.info("Call to metadataAdapter.getDataStructure consumed {} miliseconds.",
                    secondTimer.stop().elapsed(TimeUnit.MILLISECONDS));

        Map<String, String> resultSetIdentifier = repository.findByTimePeriod(eventQuery);
        log.info("Call to repository.findByTimePeriod consumed {} miliseconds.",
                    thirdTimer.stop().elapsed(TimeUnit.MILLISECONDS));

        Map dataStructure = DataMappingFunctions.addDatumsToDataStructure(dataStructures, resultSetIdentifier,
                eventQuery.getIncludeAttributes());

        log.debug("Found datastructure with name = {} and URL {}", resultSetIdentifier.get("name"),
                                                                    resultSetIdentifier.get("dataUrl"));

        log.info("Leaving getEvent with total elapsed time : {} seconds.", firstTimer.stop().elapsed(TimeUnit.SECONDS));
        return dataStructure;
    }

    @Override
    public Map getStatus(MetadataQuery metadataQuery, StatusQuery statusQuery) {
        log.debug("Entering getStatus() with metadata query = {} and time query = {}", metadataQuery, statusQuery);

        final Stopwatch firstTimer = Stopwatch.createStarted();
        final Stopwatch secondTimer = Stopwatch.createStarted();
        final Stopwatch thirdTimer = Stopwatch.createStarted();

        Map dataStructures = metadataService.getDataStructure(metadataQuery);
        log.info("Call to metadataAdapter.getDataStructure consumed {} miliseconds.",
                secondTimer.stop().elapsed(TimeUnit.MILLISECONDS));

        Map<String, String> resultSetIdentifier = repository.findByTime(statusQuery);
        log.info("Call to repository.findByTime consumed {} miliseconds.",
                thirdTimer.stop().elapsed(TimeUnit.MILLISECONDS));

        Map dataStructure = DataMappingFunctions.addDatumsToDataStructure(dataStructures, resultSetIdentifier,
                statusQuery.getIncludeAttributes());

        log.debug("Found datastructure with name = {} and URL {}", resultSetIdentifier.get("name"),
                resultSetIdentifier.get("dataUrl"));

        log.info("Leaving getStatus with total elapsed time : {} seconds.", firstTimer.stop().elapsed(TimeUnit.SECONDS));
        return dataStructure;
    }

    @Override
    public Map getFixed(MetadataQuery metadataQuery, FixedQuery fixedQuery) {
        log.debug("Entering getFixed() with metadata query = {} and data query = {}", metadataQuery, fixedQuery);

        final Stopwatch firstTimer = Stopwatch.createStarted();
        final Stopwatch secondTimer = Stopwatch.createStarted();
        final Stopwatch thirdTimer = Stopwatch.createStarted();

        Map dataStructures = metadataService.getDataStructure(metadataQuery);
        log.info("Call to metadataAdapter.getDataStructure consumed {} miliseconds.",
                secondTimer.stop().elapsed(TimeUnit.MILLISECONDS));

        Map<String, String> resultSetIdentifier = repository.findByFixed(fixedQuery);
        log.info("Call to repository.findByFixed consumed {} miliseconds.", thirdTimer.stop().elapsed(TimeUnit.MILLISECONDS));

        Map dataStructure = DataMappingFunctions.addDatumsToDataStructure(dataStructures, resultSetIdentifier,
                fixedQuery.getIncludeAttributes());

        log.debug("Found datastructure with name = {} and URL {}", resultSetIdentifier.get("name"),
                resultSetIdentifier.get("dataUrl"));

        log.info("Leaving getFixed with total elapsed time : {} seconds.", firstTimer.stop().elapsed(TimeUnit.SECONDS));
        return dataStructure;
    }

    @Override
    public Map getDataStructureVersion(DataStoreVersionQuery query) {
        return metadataService.getDataStructureVersion(query);
    }
}