package no.microdata.datastore.services;

import no.microdata.datastore.MetadataService;
import no.microdata.datastore.adapters.api.dto.DataStoreVersionQuery;
import no.microdata.datastore.model.MetadataQuery;
import no.microdata.datastore.repository.MetadataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class MetadataServiceImpl implements MetadataService {

    private final static Logger log = LoggerFactory.getLogger(MetadataServiceImpl.class);

    @Autowired
    @Qualifier("bucketMetadataRepository")
    MetadataRepository metadataRepository;

    @Override
    public Map getDataStructure(MetadataQuery metadataQuery) {
        Map metadataAll = metadataRepository.getMetadataAll();

        List<Map> datasets = (List<Map>) metadataAll.get("dataStructures");
        for (Map dataset: datasets) {
            if (Objects.equals(metadataQuery.getNames().get(0), dataset.get("name"))) {
                return dataset;
            }
        }
        String msg = "Dataset with name " + metadataQuery.getNames().get(0) + " not found";
        log.error(msg);
        throw new RuntimeException(msg);
    }

    @Override
    public Map getDataStructureVersion(DataStoreVersionQuery query) {
        log.warn("MVP1 implementation - hardcoded!");
        return Map.of(
                "datastructureName", query.dataStructureName(),
                "datastructureVersion", "1.0.0.0"
        );
    }
}