package no.microdata.datastore.transformations;

import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

public class DataMappingFunctions {

    public static Map addDatumsToDataStructure(Map dataStructure, Map<String, String> resultIdentifier, Boolean includeAttributes){
        if(dataStructure == null)
            throw new IllegalArgumentException("DataStructure argument can't be null.");
        if(ObjectUtils.isEmpty(resultIdentifier))
            throw new IllegalArgumentException("ResultIdentifier argument can't be null");

        String dataUrl = resultIdentifier.get("dataUrl");

        dataStructure = addMeasureDatumsToDataStructure(dataStructure, dataUrl);
        dataStructure = addIdentifierDatumsToDataStructure(dataStructure, dataUrl);

        if (includeAttributes != null && includeAttributes){
            dataStructure = addStartTimeDatumsToDataStructure(dataStructure, dataUrl);
            dataStructure = addEndTimeDatumsToDataStructure(dataStructure, dataUrl);
        }
        return dataStructure;
    }

    public static Map addMeasureDatumsToDataStructure(Map dataStructure, String dataUrl) {

        if (validMeasureVariableExists(dataStructure)){
                ((Map)dataStructure.get("measureVariable")).put("datums", dataUrl);
        } else{
            throw new IllegalArgumentException(
                    "The DataStructure map has illegal measureVariable object. DataStructure = " + dataStructure);
        }
        return dataStructure;
    }

    private static List<Long> stringListToLong(List<String> list){
        return list.stream().map(Long::parseLong).collect(Collectors.toList());
    }

    private static List<Double> stringListToDouble(List<String> list){
        return list.stream().map(Double::parseDouble).collect(Collectors.toList());
    }

    private static boolean validMeasureVariableExists(Map dataStructure) {
        if (dataStructure!=null && dataStructure.get("measureVariable")!=null &&
                        ((Map)dataStructure.get("measureVariable")).containsKey("label")){
            return true;
        }
        return false;
    }

    private static boolean validIdentifierVariableExists(Map identifierVariable) {
        if (identifierVariable!=null && identifierVariable.containsKey("label")){
            return true;
        }
        return false;
    }

    private static boolean validAttributeVariableExists(Map attributeVariable) {
        if (attributeVariable!=null && attributeVariable.containsKey("label")){
            return true;
        }
        return false;
    }

    public static Map addIdentifierDatumsToDataStructure(Map dataStructure, String dataUrl) {

        Map identifierVariable = (Map) ((List)dataStructure.get("identifierVariables")).get(0);

        if (validIdentifierVariableExists(identifierVariable)){
                identifierVariable.put("datums", dataUrl);
        } else{
            throw new IllegalArgumentException(
                    "The DataStructure map has illegal identifier object. DataStructure = " + dataStructure);
        }
        return dataStructure;
    }

    public static Map addStartTimeDatumsToDataStructure(Map dataStructure, String dataUrl) {

        Map startAttributeVariable=null;
        if (dataStructure!=null) {
            List<Map> attributeVariables = (List) dataStructure.getOrDefault("attributeVariables", new ArrayList<>());
            Optional<Map> first = attributeVariables.stream()
                    .filter(attributeVariable -> Objects.equals(attributeVariable.get("variableRole"),"Start"))
                    .findFirst();
            startAttributeVariable = first.orElse(null);
        }

        if (validAttributeVariableExists(startAttributeVariable)){
            startAttributeVariable.put("datums", dataUrl);
        }else {
            startAttributeVariable.put("datums", new ArrayList<>());
        }
        return dataStructure;
    }

    public static Map addEndTimeDatumsToDataStructure(Map dataStructure, String dataUrl) {

        Map endAttributeVariable=null;
        if (dataStructure!=null) {
            List<Map> attributeVariables = (List) dataStructure.getOrDefault("attributeVariables", new ArrayList<>());
            Optional<Map> first = attributeVariables.stream()
                    .filter(attributeVariable -> Objects.equals(attributeVariable.get("variableRole"),"Stop"))
                    .findFirst();
            endAttributeVariable = first.orElse(null);
        }

        if (validAttributeVariableExists(endAttributeVariable)){
            endAttributeVariable.put("datums", dataUrl);
        }else {
            endAttributeVariable.put("datums", new ArrayList<>());
        }
        return dataStructure;
    }
}