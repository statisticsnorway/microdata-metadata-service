package no.microdata.datastore.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Repository
@ConditionalOnProperty(name = "download.from.bucket", havingValue = "false")
public class LocalStorageMetadataRepository implements MetadataRepository{

    private final static Logger log = LoggerFactory.getLogger(LocalStorageMetadataRepository.class);

    @Value("${storage.metadata-all}")
    private String metadataAllFile;

    @Value("${storage.data-store}")
    private String dataStoreFile;

    @Value("${storage.versions}")
    private String versionsFile;

    @Override
    public Map getMetadataAllFile() {
        return getFileAsMap(metadataAllFile);
    }

    @Override
    public Map getDataStoreFile() {
        return getFileAsMap(dataStoreFile);
    }

    @Override
    public Map getVersionsFile() {
        return getFileAsMap(versionsFile);
    }

    private Map getFileAsMap(String file) {
        log.info("Attempts to read {}", file);
        Map result;
        try {
            result = new ObjectMapper().readValue(Files.readString(Paths.get(file)), Map.class);
        } catch (IOException e) {
            log.error("Unable to read {}", file);
            throw new RuntimeException(e);
        }
        return result;
    }
}