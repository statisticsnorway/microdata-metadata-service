package no.microdata.datastore;

import no.microdata.datastore.adapters.api.dto.DataStoreVersionQuery;
import no.microdata.datastore.model.MetadataQuery;

import java.util.List;
import java.util.Map;

public interface MetadataService {

    Map getDataStructure(MetadataQuery metadataQuery);

    List<Map<String, Object>> findDataStructures(MetadataQuery query);

    Map getDataStructureVersion(DataStoreVersionQuery query);

    Map findAllDataStoreVersions(String requestId);

    Map findAllMetadata(MetadataQuery query);

}
