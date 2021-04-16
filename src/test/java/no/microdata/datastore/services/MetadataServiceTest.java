package no.microdata.datastore.services;

import no.microdata.datastore.adapters.api.dto.DataStoreVersionQuery;
import no.microdata.datastore.model.MetadataQuery;
import no.microdata.datastore.repository.MetadataRepository;
import no.microdata.datastore.services.fixture.MetadataServiceTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class MetadataServiceTest {

    MetadataServiceImpl metadataService = new MetadataServiceImpl();

    MetadataRepository metadataRepository = Mockito.mock(MetadataRepository.class);

    @BeforeEach
    void setup(){
        metadataService.metadataRepository = metadataRepository;
    }

    @DisplayName("should find two data structures with attributes")
    @Test
    void findTwoDataStructuresWithAttrs() throws Exception {
        Map metadataQueryParms =
                Map.of("names", Arrays.asList(new String[]{"TEST_PERSON_INCOME", "TEST_PERSON_PETS"}),
                        "languages", "no",
                        "requestId", "56",
                        "version", "1.0.0.0",
                        "includeAttributes", true);
        MetadataQuery metadataQuery = new MetadataQuery(metadataQueryParms);

        when(metadataRepository.getMetadataAllFile()).thenReturn(MetadataServiceTestFixture.metadataAll());

        List<Map<String, Object>> actualDataStructure = metadataService.findDataStructures(metadataQuery);
        assertEquals(actualDataStructure, MetadataServiceTestFixture.expectedTwoDatastructuresWithAttrs());
    }

    @DisplayName("should find two data structures no attributes")
    @Test
    void findTwoDataStructuresWithNoAttrs() throws Exception {
        Map metadataQueryParms =
                Map.of("names", Arrays.asList(new String[]{"TEST_PERSON_INCOME", "TEST_PERSON_PETS"}),
                        "languages", "no",
                        "requestId", "56",
                        "version", "1.0.0.0");
        MetadataQuery metadataQuery = new MetadataQuery(metadataQueryParms);

        when(metadataRepository.getMetadataAllFile()).thenReturn(MetadataServiceTestFixture.metadataAll());

        List<Map<String, Object>> actualDataStructure = metadataService.findDataStructures(metadataQuery);
        assertEquals(actualDataStructure, MetadataServiceTestFixture.expectedTwoDatastructuresNoAttrs());
    }

    @DisplayName("should find all data store versions")
    @Test
    void findAllDataStoreVersions() throws Exception {

        when(metadataRepository.getDataStoreFile()).thenReturn(MetadataServiceTestFixture.datastoreFile());
        when(metadataRepository.getVersionsFile()).thenReturn(MetadataServiceTestFixture.versionsFile());

        Map<String, Object> actualDataStructure = metadataService.findAllDataStoreVersions("req-id-1");
        assertEquals(actualDataStructure, MetadataServiceTestFixture.expectedDatastoreVersions());
    }


    @DisplayName("should find version for a datastructure")
    @Test
    void getDataStructureVersion() throws Exception {
        Map expected = Map.of(
                "datastructureName", "TEST_PERSON_INCOME",
                "datastructureVersion", "2.0.0.0"
        );
        DataStoreVersionQuery query =
                new DataStoreVersionQuery("TEST_PERSON_INCOME", "2.0.0.0", "req-id-2");

        Map actual = metadataService.getDataStructureVersion(query);
        assertEquals(actual, expected);
    }
}