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

    @Value("${bucket.projectId}")
    private String projectId;

    @Value("${bucket.bucketName}")
    private String bucketName;

    @Value("${bucket.files.metadata-all.downloadFile}")
    private String metadataAllFile;

    @Value("${bucket.files.metadata-all.destFile}")
    private String metadataAllDestFile;

    @Value("${bucket.files.data-store.downloadFile}")
    private String dataStoreFile;

    @Value("${bucket.files.data-store.destFile}")
    private String dataStoreDestFile;

    @Value("${bucket.files.versions.downloadFile}")
    private String versionsFile;

    @Value("${bucket.files.versions.destFile}")
    private String versionsDestFile;

    @Override
    public Map getMetadataAllFile() {
        return getFileAsMap(metadataAllFile, metadataAllDestFile);
    }

    @Override
    public Map getDataStoreFile() {
        return getFileAsMap(dataStoreFile, dataStoreDestFile);
    }

    @Override
    public Map getVersionsFile() {
        return getFileAsMap(versionsFile, versionsDestFile);
    }

    private Map getFileAsMap(String file, String destFile) {
        downloadFromBucket(file, destFile);
        Map result;
        try {
            result = new ObjectMapper().readValue(Files.readString(Paths.get(destFile)), Map.class);
        } catch (IOException e) {
            log.error("Unable to read {}", destFile);
            throw new RuntimeException(e);
        }
        return result;
    }

    private void downloadFromBucket(String file, String destFile) {
        try {
            Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
            Blob blob = storage.get(BlobId.of(bucketName, file));
            blob.downloadTo(Paths.get(destFile));
        }catch (Exception e){
            log.error("Unable to download {} from bucket {} to {}", file, bucketName, destFile);
            throw e;
        }
        log.info("Downloaded {} from bucket {} to {}", file, bucketName, destFile);
    }
}
