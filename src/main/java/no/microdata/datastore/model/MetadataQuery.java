package no.microdata.datastore.model;


import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MetadataQuery {

    String languages;
    String requestId;
    List<String> names;
    String version;
    Boolean includeAttributes;

    public MetadataQuery(Map inputFields) {

        if(!hasRequiredField((String) inputFields.get("version"))) {
            throw new AssertionError("Missing required field. Field version = " + inputFields.get("version"));
        }

        if (inputFields.get("names") == null){
            this.names = List.of();
        } else if (inputFields.get("names") instanceof List){
            this.names = (List<String>) inputFields.get("names");
        } else if (inputFields.get("names") instanceof String){
            String [] names = ((String)inputFields.get("names")).split(",");

            this.names = Stream.of(names).map(String::strip).collect(Collectors.toList());
        } else {
            this.names = List.of();
        }

        // MVP1, remember?
        this.version = "1.0.0.0";
        this.requestId = (String) inputFields.get("requestId");
        this.languages = (String) inputFields.get("languages");
        this.includeAttributes = (Boolean)inputFields.get("includeAttributes");
    }

    private boolean hasRequiredField(String field) {
        return field != null && !field.isEmpty();
    }

    @Override
    public String toString() {
        return "MetadataQuery["
                + "languages=" + languages
                + ", requestId=" + requestId
                + ", names=" + String.join(", ", names)
                + ", version=" + version
                + ", includeAttributes=" + includeAttributes
                + ']';
    }

    @Override
    public int hashCode() {
        return Objects.hash(languages, requestId, names, version);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MetadataQuery other = (MetadataQuery) obj;
        return Objects.equals(languages, other.languages)
                && Objects.equals(requestId, other.requestId)
                && Objects.equals(names, other.names)
                && Objects.equals(version, other.version)
                && Objects.equals(includeAttributes, other.includeAttributes);
    }

    public String getLanguages() {
        return languages;
    }

    public String getRequestId() {
        return requestId;
    }

    public List<String> getNames() {
        return names;
    }

    public String getVersion() {
        return version;
    }

    public Boolean getIncludeAttributes() { return includeAttributes; }
}