package no.microdata.datastore.services;

import no.microdata.datastore.MetadataService;
import no.microdata.datastore.model.*;
import no.microdata.datastore.repository.DataServiceRepository;
import no.microdata.datastore.services.fixture.DataServiceTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class DataServiceTest {

    DataServiceImpl dataService = new DataServiceImpl();

    DataServiceRepository dataServiceRepository = Mockito.mock(DataServiceRepository.class);

    MetadataService metadataService = Mockito.mock(MetadataService.class);

    String version;
    DatasetRevision datasetRevision;

    @BeforeEach
    void setup(){
        dataService.repository = dataServiceRepository;
        dataService.metadataService = metadataService;
        version = "1.0.0.0";
        Map datasetRevisionParms =
                Map.of("datasetName", "KJONN",
                        "version", version);
        datasetRevision = new DatasetRevision(datasetRevisionParms);
    }

    @DisplayName("should get event data")
    @Test
    void  getEventData() throws Exception {
        Map eventQueryParms =
                Map.of("datasetRevision", datasetRevision,
                        "startDate", (long) 11688,
                        "endDate", (long) 13149,
                        "includeAttributes", true);
        EventQuery dataQuery = new EventQuery(eventQueryParms);

        Map metadataQueryParms =
                Map.of("names", dataQuery.getDatasetRevision().getDatasetName(),
                        "languages", "no",
                        "requestId", "56",
                        "version", version,
                        "includeAttributes", true);
        MetadataQuery metadataQuery = new MetadataQuery(metadataQueryParms);

        when(metadataService.getDataStructure(metadataQuery)).thenReturn(DataServiceTestFixture.datastructure());
        when(dataServiceRepository.findByTimePeriod(dataQuery)).thenReturn(DataServiceTestFixture.RESPONSE_FROM_DATASERVICE());

        Map actualDataStructure = dataService.getEvent(metadataQuery, dataQuery);
        assertEquals(actualDataStructure, DataServiceTestFixture.EXPECTED_RESPONSE());
    }

    @DisplayName("should get status data")
    @Test
    void  getStatusData() throws Exception {
        Map statusQueryParms =
                Map.of("datasetRevision", datasetRevision,
                        "date", (long) 11688,
                        "includeAttributes", true);
        StatusQuery dataQuery = new StatusQuery(statusQueryParms);

        Map metadataQueryParms =
                Map.of("names", dataQuery.getDatasetRevision().getDatasetName(),
                        "languages", "no",
                        "requestId", "56",
                        "version", version,
                        "includeAttributes", true);
        MetadataQuery metadataQuery = new MetadataQuery(metadataQueryParms);

        when(metadataService.getDataStructure(metadataQuery)).thenReturn(DataServiceTestFixture.datastructure());
        when(dataServiceRepository.findByTime(dataQuery)).thenReturn(DataServiceTestFixture.RESPONSE_FROM_DATASERVICE());

        Map actualDataStructure = dataService.getStatus(metadataQuery, dataQuery);
        assertEquals(actualDataStructure, DataServiceTestFixture.EXPECTED_RESPONSE());
    }

    @DisplayName("should get fixed data")
    @Test
    void  getFixedData() throws Exception {
        Map fixedQueryParms =
                Map.of("datasetRevision", datasetRevision,
                        "includeAttributes", true);
        FixedQuery dataQuery = new FixedQuery(fixedQueryParms);

        Map metadataQueryParms =
                Map.of("names", dataQuery.getDatasetRevision().getDatasetName(),
                        "languages", "no",
                        "requestId", "56",
                        "version", version,
                        "includeAttributes", true);
        MetadataQuery metadataQuery = new MetadataQuery(metadataQueryParms);

        when(metadataService.getDataStructure(metadataQuery)).thenReturn(DataServiceTestFixture.datastructure());
        when(dataServiceRepository.findByFixed(dataQuery)).thenReturn(DataServiceTestFixture.RESPONSE_FROM_DATASERVICE());

        Map actualDataStructure = dataService.getFixed(metadataQuery, dataQuery);

        assertEquals(actualDataStructure, DataServiceTestFixture.EXPECTED_RESPONSE());
    }

    @DisplayName("should get event data with no attributes when includeAttributes is not given (default behaviour)")
    @Test
    void  getEventDataNoAttrs() throws Exception {
        Map eventQueryParms =
                Map.of("datasetRevision", datasetRevision,
                        "startDate", (long) 11688,
                        "endDate", (long) 13149);
        EventQuery dataQuery = new EventQuery(eventQueryParms);

        Map metadataQueryParms =
                Map.of("names", dataQuery.getDatasetRevision().getDatasetName(),
                        "languages", "no",
                        "requestId", "56",
                        "version", version);
        MetadataQuery metadataQuery = new MetadataQuery(metadataQueryParms);

        when(metadataService.getDataStructure(metadataQuery)).thenReturn(DataServiceTestFixture.DATASTRUCTURE_NO_ATTRS());
        when(dataServiceRepository.findByTimePeriod(dataQuery)).thenReturn(DataServiceTestFixture.RESPONSE_FROM_DATASERVICE());

        Map actualDataStructure = dataService.getEvent(metadataQuery, dataQuery);
        assertEquals(actualDataStructure, DataServiceTestFixture.EXPECTED_RESPONSE_NO_ATTRS());
    }

    @DisplayName("should get event data with no attributes when includeAttributes = false")
    @Test
    void  getEventDataAttrsFalse() throws Exception {
        Map eventQueryParms =
                Map.of("datasetRevision", datasetRevision,
                        "startDate", (long) 11688,
                        "endDate", (long) 13149,
                        "includeAttributes", false);
        EventQuery dataQuery = new EventQuery(eventQueryParms);

        Map metadataQueryParms =
                Map.of("names", dataQuery.getDatasetRevision().getDatasetName(),
                        "languages", "no",
                        "requestId", "56",
                        "version", version,
                        "includeAttributes", false);
        MetadataQuery metadataQuery = new MetadataQuery(metadataQueryParms);

        when(metadataService.getDataStructure(metadataQuery)).thenReturn(DataServiceTestFixture.DATASTRUCTURE_NO_ATTRS());
        when(dataServiceRepository.findByTimePeriod(dataQuery)).thenReturn(DataServiceTestFixture.RESPONSE_FROM_DATASERVICE());

        Map actualDataStructure = dataService.getEvent(metadataQuery, dataQuery);
        assertEquals(actualDataStructure, DataServiceTestFixture.EXPECTED_RESPONSE_NO_ATTRS());
    }

    @DisplayName("should throw an exception if MetadataQuery argument is missing")
    @Test
    void  getEventDataMissingMetadataQuery() throws Exception {
        Map eventQueryParms =
                Map.of("datasetRevision", datasetRevision,
                        "startDate", (long) 11688,
                        "endDate", (long) 13149,
                        "includeAttributes", false);
        EventQuery dataQuery = new EventQuery(eventQueryParms);

        assertThrows(Exception.class, () -> {
            dataService.getEvent(null, dataQuery);
        });
    }

    @DisplayName("should throw an exception if EventQuery argument is missing")
    @Test
    void  getEventDataMissingEventQuery() throws Exception {
        Map metadataQueryParms =
                Map.of("names", "FNR",
                        "languages", "no",
                        "requestId", "56",
                        "version", version,
                        "includeAttributes", false);
        MetadataQuery metadataQuery = new MetadataQuery(metadataQueryParms);

        assertThrows(Exception.class, () -> {
            dataService.getEvent(metadataQuery, null);
        });
    }
}