package no.microdata.datastore.repository;

import java.util.Map;

public interface MetadataRepository {

    Map getMetadataAllFile();

    Map getDataStoreFile();

    Map getVersionsFile();

}
