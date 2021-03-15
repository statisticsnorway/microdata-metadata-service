package no.microdata.datastore.services;

import no.microdata.datastore.DataStructureService;
import no.microdata.datastore.model.MetadataQuery;
import no.microdata.datastore.repository.MetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DataStructureServiceImpl implements DataStructureService {

    @Autowired
    @Qualifier("fileMetadataRepository")
    MetadataRepository metadataRepository;

    @Override
    public List<Map<String, Object>> find(MetadataQuery query) {

        Map metadataAll = metadataRepository.getMetadataAll();
        List<Map<String, Object>> datasets = (List<Map<String, Object>>) metadataAll.get("dataStructures");
        List<Map<String, Object>> found = new ArrayList<>();

        for (Map<String, Object> dataset: datasets) {
            if (query.getNames().contains(dataset.get("name"))) {
                found.add(dataset);
            }
        }
        return found;
    }
}