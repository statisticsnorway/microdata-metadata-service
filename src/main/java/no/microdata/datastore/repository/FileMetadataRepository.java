package no.microdata.datastore.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Repository
public class FileMetadataRepository implements MetadataRepository{

    @Value("${datastore.metadata.metadata-all}")
    private String metadataAllFile;

    @Override
    public Map getMetadataAll() {
        Map metadataAll;
        try {
            metadataAll = new ObjectMapper().readValue(
                    Files.readString(Paths.get(metadataAllFile)), Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return metadataAll;
    }

}
