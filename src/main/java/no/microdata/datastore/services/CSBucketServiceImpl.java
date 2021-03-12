package no.microdata.datastore.services;

import no.microdata.datastore.CSBucketService;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;

@Service
public class CSBucketServiceImpl implements CSBucketService {

    private final static Logger log = LoggerFactory.getLogger(CSBucketServiceImpl.class);

    @Override
    public Boolean downloadObject(String objectName) {

        // The ID of your GCP project
        String projectId = "ssb-team-microdata-staging";

        // The ID of your GCS bucket
        String bucketName = "data-service-bucket-microdata-poc";

        // The path to which the file should be downloaded
        String destFilePath = "metadata-all-test__1_0_0.json";

        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

        Blob blob = storage.get(BlobId.of(bucketName, objectName));
        blob.downloadTo(Paths.get(destFilePath));

        System.out.println(
                "Downloaded object "
                        + objectName
                        + " from bucket name "
                        + bucketName
                        + " to "
                        + destFilePath);
        return true;
    }
}
