package no.microdata.datastore.adapters.api;

import no.microdata.datastore.MetadataService;
import no.microdata.datastore.model.MetadataQuery;
import no.microdata.datastore.transformations.VersionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.microdata.datastore.adapters.api.Constants.*;
import static no.microdata.datastore.adapters.api.RequestId.verifyAndUpdateRequestId;

@RestController
@RequestMapping(produces = {"application/json;charset=UTF-8", "application/x-msgpack"})
class MetadataAPI {

    private final static Logger log = LoggerFactory.getLogger(MetadataAPI.class);

    @Autowired
    MetadataService metadataService;

    @RequestMapping(value = "/metadata/data-store", method = RequestMethod.GET)
    Map getDataStore(@RequestHeader(value = X_REQUEST_ID, required = false) String requestId,
                           @RequestHeader(value = ACCEPT_LANGUAGE, required = false) String language,
                           HttpServletResponse response) {

        log.info("Entering getDataStore() with requestId = {} and language {}", requestId, language);

        var verifiedRequestId = verifyAndUpdateRequestId(requestId);

        response.setHeader(X_REQUEST_ID, verifiedRequestId);
        response.setHeader(CONTENT_LANGUAGE, "no");

        return metadataService.findAllDataStoreVersions(verifiedRequestId);
    }

    @RequestMapping(value = "/metadata/data-structures", method = RequestMethod.GET)
    List<Map<String, Object>> getDataStructures(@RequestHeader(value = X_REQUEST_ID, required = false) String requestId,
                                @RequestHeader(value = ACCEPT_LANGUAGE, required = false) List<String> languages,
                                @RequestParam(value = NAMES, required = false) List names,
                                @RequestParam(value = VERSION, required = true) String version,
                                HttpServletResponse response) {

        MetadataQuery query = new MetadataQuery(
                new HashMap() {{
                    put("names", names);
                    put("languages", joinToString(languages));
                    put("requestId", verifyAndUpdateRequestId(requestId));
                    put("version", VersionUtils.toThreeLabelsIfNotDraft(version));
                }});

        log.info("Entering getDataStructures() where query = {}", query);

        response.setHeader(X_REQUEST_ID, query.getRequestId());
        response.setHeader(CONTENT_LANGUAGE, "no");

        return metadataService.findDataStructures(query);
    }

    @RequestMapping(value = "/metadata/all", method = RequestMethod.GET)
    Map<String, Object> getAllMetadata(@RequestHeader(value = X_REQUEST_ID, required = false) String requestId,
                       @RequestHeader(value = ACCEPT_LANGUAGE, required = false) List<String> languages,
                       @RequestParam(value = VERSION, required = true) String version,
                       HttpServletResponse response) {

        MetadataQuery query = new MetadataQuery(
                new HashMap() {{
                    put("languages", joinToString(languages));
                    put("requestId", verifyAndUpdateRequestId(requestId));
                    put("version", VersionUtils.toThreeLabelsIfNotDraft(version));
                }});

        log.info("Entering getAllMetadata() where query = {}", query);

        response.setHeader(X_REQUEST_ID, query.getRequestId());
        response.setHeader(CONTENT_LANGUAGE, "no");

        return metadataService.findAllMetadata(query);
    }

    @RequestMapping(value = "/languages", method = RequestMethod.GET)
    List<Map<String, Object>> getLanguages(@RequestHeader(value = X_REQUEST_ID, required = false) String requestId,
                           HttpServletResponse response) {

        log.info("Entering getLanguages() with request id = {}", requestId);

        requestId = verifyAndUpdateRequestId(requestId);
        response.setHeader(X_REQUEST_ID, requestId);

        return metadataService.findLanguages(requestId);
    }

    String joinToString(List<String> languages){
        return languages != null ? String.join(", ", languages) : null;
    }
}