package no.microdata.datastore.services;

import no.microdata.datastore.DataStoreService;
import no.microdata.datastore.repository.MetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DataStoreServiceImpl implements DataStoreService {

    @Autowired
    @Qualifier("bucketMetadataRepository")
    MetadataRepository metadataRepository;

    @Override
    public Map<String, Object> findAllDataStoreVersions(String requestId) {
        Map<String, Object> datastore = metadataRepository.getDataStoreFile();
        Map<String, Object> versions = metadataRepository.getVersionsFile();
        datastore.putAll(versions);
        return datastore;
    }
}