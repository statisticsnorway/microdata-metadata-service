package no.microdata.datastore.adapters.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.microdata.datastore.DataService;
import no.microdata.datastore.MockApplication;
import no.microdata.datastore.MockConfig;
import no.microdata.datastore.adapters.api.dto.Credentials;
import no.microdata.datastore.adapters.api.dto.DataStoreVersionQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,classes = {MockApplication.class, MockConfig.class})
public class DataAPITest {

    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_MSGPACK = "application/x-msgpack";
    public static final String REQUESTID_DESCRIPTION = "The request id. Optional.";
    public static final String DATASTRUCTURE_NAME_DESCRIPTION = "The name of the data structure.";
    public static final String ACCEPT_LANGUAGE_DESCRIPTION = "ISO 639-1 language code. Optional.";
    public static final String CONTENT_TYPE_DESCRIPTION = "The content type of the request body. We expect \"application/json\".";
    public static final String ACCEPT_DESCRIPTION = "The API support both \"application/json\" and \"application/x-msgpack\"";
    public static final String DATASTORE_VERSION = "1.1.0.0";
    public static final String DATASTRUCTURE_VERSION = "1.0.0.0";
    public static final String VERSION_DESCRIPTION = "The version of the datastore.";
    public static final String VALUE_FILTER_DESCRIPTION = "The value filter. It is a collection of Strings";
    public static final String POPULATION_DESCRIPTION = "The population filter. It is an object with a key named 'unitIds' where the value is an array of unit IDs.";
    public static final String POPULATION_UNITIDS_DESCRIPTION = "The unit ids in the population.";
    public static final String INTERVALFILTER_DESCRIPTION = "An interval of integers between 0 and 999. Format: '[10, 100]'. This can be used by the http client in order to" +
            " run parallel requests.  Optional.";
    public static final String INCLUDE_ATTRIBUTES_DESCRIPTION = "No attributes will be returned if 'includeAttributes' is false or not provided. Optional.";
    public static final String CREDENTIALS_USERNAME = "The username";
    public static final String CREDENTIALS_PASSWORD = "The password";

    @Autowired
    private WebApplicationContext webApplicationContext;

    DataService dataService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
        dataService = (DataService) webApplicationContext.getBean("dataService");
        reset(dataService);
    }

    @Test
    public void testGetEvent() throws Exception{

        List populationFilter = List.of(1, 2, 2, 3, 4);
        Map expectedEvent = DataAPITestFixture.datastructureFnr();

        long startDate = 1345;
        long endDate = 2456;

        Set<String> valueFilter = Stream.of("1","2","45","3","4").collect(Collectors.toCollection(HashSet::new));

        Map queryAsMap =
                new HashMap() {{
                    put("version", DATASTORE_VERSION);
                    put("dataStructureName", "FNR");
                    put("startDate", startDate); //.toEpochDay());
                    put("stopDate", endDate); //.toEpochDay());
                    put("values", valueFilter);
                    put("population", Map.of("unitIds", populationFilter));
                    put("intervalFilter",  "[0, 999]");
                    put("credentials", Map.of( "username", Credentials.VALID_USERNAME,
                            "password", Credentials.VALID_PASSWORD));
                    put("includeAttributes", true);
                }};

        when(dataService.getDataStructureVersion(any(DataStoreVersionQuery.class))).thenReturn(
                Map.of( "datastructureName", "FNR",
                        "datastructureVersion", DATASTRUCTURE_VERSION)
        );

        when(dataService.getEvent(any(), any())).thenReturn(expectedEvent);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/data/data-structure/event")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(queryAsMap))
                    .header(Constants.X_REQUEST_ID, "56")
                    .header(Constants.ACCEPT_LANGUAGE, "no")
                    .header(Constants.ACCEPT, Constants.ACCEPT_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE_JSON))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedEvent)))
                .andDo(document("getEvent-request", Preprocessors.preprocessRequest(maskPassword())))
                .andDo(document("getEvent",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestFields(
                                fieldWithPath("version").description(VERSION_DESCRIPTION),
                                fieldWithPath("dataStructureName").description(DATASTRUCTURE_NAME_DESCRIPTION),
                                fieldWithPath("startDate").description("The start date of the time period"),
                                fieldWithPath("stopDate").description("The end date of the time period"),
                                fieldWithPath("values").description(VALUE_FILTER_DESCRIPTION),
                                fieldWithPath("population").description(POPULATION_DESCRIPTION),
                                fieldWithPath("population.unitIds").description(POPULATION_UNITIDS_DESCRIPTION),
                                fieldWithPath("intervalFilter").description(INTERVALFILTER_DESCRIPTION),
                                fieldWithPath("includeAttributes").description(INCLUDE_ATTRIBUTES_DESCRIPTION),
                                fieldWithPath("credentials.username").description(CREDENTIALS_USERNAME),
                                fieldWithPath("credentials.password").description(CREDENTIALS_PASSWORD)
                        ),
                        HeaderDocumentation.requestHeaders(
                                HeaderDocumentation.headerWithName(Constants.X_REQUEST_ID).description(REQUESTID_DESCRIPTION),
                                HeaderDocumentation.headerWithName(Constants.ACCEPT_LANGUAGE).description(ACCEPT_LANGUAGE_DESCRIPTION),
                                HeaderDocumentation.headerWithName(Constants.CONTENT_TYPE).description(CONTENT_TYPE_DESCRIPTION),
                                HeaderDocumentation.headerWithName(Constants.ACCEPT).description(ACCEPT_DESCRIPTION)
                        )));
    }

    @Test
    public void testGetStatus() throws Exception{

        List populationFilter = List.of(1, 2, 2, 3, 4);
        Map expectedEvent = DataAPITestFixture.datastructureFnr();
        Long date = Long.valueOf(14579);

        Set<String> valueFilter = Stream.of("1","2","45","3","4").collect(Collectors.toCollection(HashSet::new));

        Map queryAsMap =
                new HashMap() {{
                    put("version", DATASTORE_VERSION);
                    put("dataStructureName", "FNR");
                    put("date", date);
                    put("values", valueFilter);
                    put("population", Map.of("unitIds", populationFilter));
                    put("intervalFilter",  "[0, 999]");
                    put("credentials", Map.of( "username", Credentials.VALID_USERNAME,
                            "password", Credentials.VALID_PASSWORD));
                    put("includeAttributes", true);
                }};

        when(dataService.getDataStructureVersion(any(DataStoreVersionQuery.class))).thenReturn(
                Map.of( "datastructureName", "FNR",
                        "datastructureVersion", DATASTRUCTURE_VERSION)
        );

        when(dataService.getStatus(any(), any())).thenReturn(expectedEvent);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/data/data-structure/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(queryAsMap))
                .header(Constants.X_REQUEST_ID, "56")
                .header(Constants.ACCEPT_LANGUAGE, "no")
                .header(Constants.ACCEPT, Constants.ACCEPT_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE_JSON))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedEvent)))
                .andDo(document("getStatus-request", Preprocessors.preprocessRequest(maskPassword())))
                .andDo(document("getStatus",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestFields(
                                fieldWithPath("version").description(VERSION_DESCRIPTION),
                                fieldWithPath("dataStructureName").description(DATASTRUCTURE_NAME_DESCRIPTION),
                                fieldWithPath("date").description("The date of the metadataQuery"),
                                fieldWithPath("values").description(VALUE_FILTER_DESCRIPTION),
                                fieldWithPath("population").description(POPULATION_DESCRIPTION),
                                fieldWithPath("population.unitIds").description(POPULATION_UNITIDS_DESCRIPTION),
                                fieldWithPath("intervalFilter").description(INTERVALFILTER_DESCRIPTION),
                                fieldWithPath("includeAttributes").description(INCLUDE_ATTRIBUTES_DESCRIPTION),
                                fieldWithPath("credentials.username").description(CREDENTIALS_USERNAME),
                                fieldWithPath("credentials.password").description(CREDENTIALS_PASSWORD)
                        ),
                        HeaderDocumentation.requestHeaders(
                                HeaderDocumentation.headerWithName(Constants.X_REQUEST_ID).description(REQUESTID_DESCRIPTION),
                                HeaderDocumentation.headerWithName(Constants.ACCEPT_LANGUAGE).description(ACCEPT_LANGUAGE_DESCRIPTION),
                                HeaderDocumentation.headerWithName(Constants.CONTENT_TYPE).description(CONTENT_TYPE_DESCRIPTION),
                                HeaderDocumentation.headerWithName(Constants.ACCEPT).description(ACCEPT_DESCRIPTION))
                        ));
    }

    @Test
    public void testGetFixed() throws Exception{

        List populationFilter = List.of(1, 2, 2, 3, 4);
        Map expectedEvent = DataAPITestFixture.datastructureFnr();

        Set<String> valueFilter = Stream.of("1","2","45","3","4")
                .collect(Collectors.toCollection(HashSet::new));

        Map queryAsMap =
                new HashMap() {{
                    put("version", DATASTORE_VERSION);
                    put("dataStructureName", "FNR");
                    put("values", valueFilter);
                    put("population", Map.of("unitIds", populationFilter));
                    put("intervalFilter",  "[0, 999]");
                    put("credentials", Map.of( "username", Credentials.VALID_USERNAME,
                            "password", Credentials.VALID_PASSWORD));
                }};

        when(dataService.getDataStructureVersion(any(DataStoreVersionQuery.class))).thenReturn(
                Map.of( "datastructureName", "FNR",
                        "datastructureVersion", DATASTRUCTURE_VERSION)
        );

        when(dataService.getFixed(any(), any())).thenReturn(expectedEvent);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/data/data-structure/fixed")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(queryAsMap))
                .header(Constants.X_REQUEST_ID, "56")
                .header(Constants.ACCEPT_LANGUAGE, "no")
                .header(Constants.ACCEPT, Constants.ACCEPT_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE_JSON))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedEvent)))
                .andDo(document("getFixed-request", Preprocessors.preprocessRequest(maskPassword())))
                .andDo(document("getFixed",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestFields(
                                fieldWithPath("version").description(VERSION_DESCRIPTION),
                                fieldWithPath("dataStructureName").description(DATASTRUCTURE_NAME_DESCRIPTION),
                                fieldWithPath("values").description(VALUE_FILTER_DESCRIPTION),
                                fieldWithPath("population").description(POPULATION_DESCRIPTION),
                                fieldWithPath("population.unitIds").description(POPULATION_UNITIDS_DESCRIPTION),
                                fieldWithPath("intervalFilter").description(INTERVALFILTER_DESCRIPTION),
                                fieldWithPath("credentials.username").description(CREDENTIALS_USERNAME),
                                fieldWithPath("credentials.password").description(CREDENTIALS_PASSWORD)
                        ),
                        HeaderDocumentation.requestHeaders(
                                HeaderDocumentation.headerWithName(Constants.X_REQUEST_ID).description(REQUESTID_DESCRIPTION),
                                HeaderDocumentation.headerWithName(Constants.ACCEPT_LANGUAGE).description(ACCEPT_LANGUAGE_DESCRIPTION),
                                HeaderDocumentation.headerWithName(Constants.CONTENT_TYPE).description(CONTENT_TYPE_DESCRIPTION),
                                HeaderDocumentation.headerWithName(Constants.ACCEPT).description(ACCEPT_DESCRIPTION))
                        ));
    }


    private OperationPreprocessor maskPassword() {
        return new PasswordMaskingPreprocessor();
    }

    static class PasswordMaskingPreprocessor implements OperationPreprocessor {

        @Override
        public OperationRequest preprocess(OperationRequest request) {
            String contentAsString = request.getContentAsString();

            Map contentAsJson = null;
            String resultingContentAsString = null;
            ObjectMapper mapper = new ObjectMapper();
            try {
                contentAsJson = mapper.readValue(contentAsString, Map.class);
                Map credentials = (Map) contentAsJson.get("credentials");
                credentials.computeIfPresent("password",(k,v)->"XXX");
                resultingContentAsString = mapper.writeValueAsString(contentAsJson);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return new OperationRequestFactory().create(request.getUri(),
                    request.getMethod(), resultingContentAsString.getBytes(), request.getHeaders(),
                    request.getParameters(), request.getParts());
        }

        @Override
        public OperationResponse preprocess(OperationResponse response) {
            return response;
        }

    }
}