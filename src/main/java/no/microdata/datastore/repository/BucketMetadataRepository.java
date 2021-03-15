package no.microdata.datastore.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Repository
public class BucketMetadataRepository implements MetadataRepository{

    private final static Logger log = LoggerFactory.getLogger(BucketMetadataRepository.class);

    @Value("${bucket.downloadFile}")
    private String metadataAllFile;

    @Value("${bucket.projectId}")
    private String projectId;

    @Value("${bucket.bucketName}")
    private String bucketName;

    @Value("${bucket.destFile}")
    private String destFile;

    @Override
    public Map getMetadataAll() {

        downloadFromBucket();
        Map metadataAll;
        try {
            metadataAll = new ObjectMapper().readValue(
                    Files.readString(Paths.get(destFile)), Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return metadataAll;
    }

    private void downloadFromBucket() {
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

        Blob blob = storage.get(BlobId.of(bucketName, metadataAllFile));
        blob.downloadTo(Paths.get(destFile));

        log.info("Downloaded object {} from bucket name {} to {}", metadataAllFile, bucketName, destFile);
    }
}
