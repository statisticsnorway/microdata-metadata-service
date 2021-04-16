package no.microdata.datastore.transformations;

import org.springframework.util.ObjectUtils;
import java.util.Map;

public class DataMappingFunctions {

    public static Map addDataUrlToDataStructure(Map dataStructure, Map<String, String> resultIdentifier){
        if (dataStructure == null)
            throw new IllegalArgumentException("DataStructure argument can't be null.");
        if (ObjectUtils.isEmpty(resultIdentifier)  || ObjectUtils.isEmpty(resultIdentifier.get("dataUrl")))
            throw new IllegalArgumentException("ResultIdentifier argument can't be null");

        dataStructure.put("dataUrl", resultIdentifier.get("dataUrl"));
        return dataStructure;
    }
}