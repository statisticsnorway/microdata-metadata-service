package no.microdata.datastore.adapters.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.microdata.datastore.MetadataService;
import no.microdata.datastore.MockApplication;
import no.microdata.datastore.MockConfig;
import no.microdata.datastore.model.MetadataQuery;
import no.microdata.datastore.transformations.VersionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,classes = {MockApplication.class, MockConfig.class})
class MetadataAPITest {

    public static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";
    public static final String CONTENT_TYPE_DESCRIPTION = CONTENT_TYPE_JSON;
    public static final String VERSION_DESCRIPTION = "The version of the required datastructures (mandatory)";
    public static final String NAMES_DESCRIPTION = "A list of names separated by comma for the required datastructures (optional)";
    public static final String NAMES_DESCRIPTION_CURRENT = "A list of names separated by comma for the required datastructures (mandatory)";
    public static final String REQUESTID_DESCRIPTION = "The request id (optional)";
    public static final String ACCEPT_LANGUAGE_DESCRIPTION = "Language code (optional)";
    public static final String DATASTUCTURE_NAME_DESCRIPTION = "A name for the required datastructure (mandatory)";
    public static final String DATASTORE_VERSION_DESCRIPTION = "The required version of the datastore (mandatory)";

    private MockMvc mockMvc;

    MetadataService metadataService;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();

        metadataService = (MetadataService) webApplicationContext.getBean("metadataService");
        reset(metadataService);
    }

    @Test
    public void testGetDataStore() throws Exception {
        var expectedDataStoreVersions = MetadataAPIFixture.dataStoreVersions();
        var requestId = "test-123";

        when(metadataService.findAllDataStoreVersions(requestId)).thenReturn(expectedDataStoreVersions);

        mockMvc.perform(
                get("/metadata/data-store", requestId, "no")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.X_REQUEST_ID, requestId)
                        .header(Constants.ACCEPT_LANGUAGE, "no")
                        .header(Constants.ACCEPT, Constants.ACCEPT_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_JSON))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedDataStoreVersions)))
                .andDo(document("metadata-api-getDataStore",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                HeaderDocumentation.headerWithName(Constants.CONTENT_TYPE).description(CONTENT_TYPE_DESCRIPTION),
                                HeaderDocumentation.headerWithName(Constants.X_REQUEST_ID).description(REQUESTID_DESCRIPTION),
                                HeaderDocumentation.headerWithName(Constants.ACCEPT_LANGUAGE).description(ACCEPT_LANGUAGE_DESCRIPTION),
                                HeaderDocumentation.headerWithName(Constants.ACCEPT).description(Constants.ACCEPT_DESCRIPTION)),
                        responseHeaders(
                                HeaderDocumentation.headerWithName(Constants.CONTENT_TYPE).description(CONTENT_TYPE_DESCRIPTION))
                ));

        verify(metadataService).findAllDataStoreVersions(requestId);
    }

    @Test
    public void testGetDataStructures() throws Exception {
        var requestVersion = "3.2.1.0";
        var expectedDataStructures = MetadataAPIFixture.dataStructures();
        MetadataQuery query = new MetadataQuery(Map.of(
                "names", List.of("FNR", "AKT_ARBAP"),
                "languages", "no",
                "requestId", "56",
                "version", VersionUtils.toThreeLabelsIfNotDraft(requestVersion)
        ));
        var names = "FNR,AKT_ARBAP";

        when(metadataService.findDataStructures(query)).thenReturn(expectedDataStructures);

        mockMvc.perform(
                get("/metadata/data-structures?names={names}&version={version}", names, requestVersion)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.X_REQUEST_ID, query.getRequestId())
                        .header(Constants.ACCEPT_LANGUAGE, query.getLanguages())
                        .header(Constants.ACCEPT, Constants.ACCEPT_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_JSON))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedDataStructures)))
                .andDo(document("getDataStructures",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestParameters(
                                parameterWithName("names").description(NAMES_DESCRIPTION),
                                parameterWithName("version").description(VERSION_DESCRIPTION)
                        ),
                        requestHeaders(
                                HeaderDocumentation.headerWithName(Constants.CONTENT_TYPE).description(CONTENT_TYPE_DESCRIPTION),
                                HeaderDocumentation.headerWithName(Constants.X_REQUEST_ID).description(REQUESTID_DESCRIPTION),
                                HeaderDocumentation.headerWithName(Constants.ACCEPT_LANGUAGE).description(ACCEPT_LANGUAGE_DESCRIPTION),
                                HeaderDocumentation.headerWithName(Constants.ACCEPT).description(Constants.ACCEPT_DESCRIPTION)),
                        responseHeaders(
                                HeaderDocumentation.headerWithName(Constants.CONTENT_TYPE).description(CONTENT_TYPE_DESCRIPTION))
                        )
                );

        verify(metadataService).findDataStructures(query);
    }

    @Test
    public void testGetAllMetadata() throws Exception {
        var requestVersion = "3.2.1.0";
        var expectedAllMetadata = MetadataAPIFixture.allMetadata();
        MetadataQuery query = new MetadataQuery(Map.of(
                "languages", "no",
                "requestId", "56",
                "version", VersionUtils.toThreeLabelsIfNotDraft(requestVersion))
        );

        when(metadataService.findAllMetadata(query)).thenReturn(expectedAllMetadata);

        mockMvc.perform(
                get("/metadata/all?version={version}", requestVersion)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.X_REQUEST_ID, query.getRequestId())
                        .header(Constants.ACCEPT_LANGUAGE, query.getLanguages())
                        .header(Constants.ACCEPT, Constants.ACCEPT_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_JSON))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedAllMetadata)))
                .andDo(document("getAllMetadata",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestParameters(
                                parameterWithName("version").description(VERSION_DESCRIPTION)),
                        requestHeaders(
                                HeaderDocumentation.headerWithName(Constants.CONTENT_TYPE).description(CONTENT_TYPE_DESCRIPTION),
                                HeaderDocumentation.headerWithName(Constants.X_REQUEST_ID).description(REQUESTID_DESCRIPTION),
                                HeaderDocumentation.headerWithName(Constants.ACCEPT_LANGUAGE).description(ACCEPT_LANGUAGE_DESCRIPTION),
                                HeaderDocumentation.headerWithName(Constants.ACCEPT).description(Constants.ACCEPT_DESCRIPTION)),
                        responseHeaders(
                                HeaderDocumentation.headerWithName(Constants.CONTENT_TYPE).description(CONTENT_TYPE_DESCRIPTION)),
                        responseFields(
                                subsectionWithPath("dataStore").description("The metadatastore"),
                                subsectionWithPath("languages").description("All languages"),
                                subsectionWithPath("dataStructures").description("All datastructures")
                        )));

        verify(metadataService).findAllMetadata(query);
    }

    @Test
    public void testGetDataStructuresWithMsgPack() throws Exception {
        var requestVersion = "3.2.1.0";
        var expectedDataStructures = MetadataAPIFixture.dataStructures();
        MetadataQuery query = new MetadataQuery(Map.of(
                "names", List.of("FNR", "AKT_ARBAP"),
                "languages", "no",
                "requestId", "56",
                "version", VersionUtils.toThreeLabelsIfNotDraft(requestVersion))
        );

        when(metadataService.findDataStructures(query)).thenReturn(expectedDataStructures);
        var names = "FNR,AKT_ARBAP";

        mockMvc.perform(
                get("/metadata/data-structures?names={names}&version={version}", names, requestVersion)
                        .header(Constants.X_REQUEST_ID, query.getRequestId())
                        .header(Constants.ACCEPT_LANGUAGE, query.getLanguages())
                        .header(Constants.ACCEPT, Constants.ACCEPT_MSGPACK))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(Constants.ACCEPT_MSGPACK));

        verify(metadataService).findDataStructures(query);
    }

    @Test
    public void testLanguages() throws Exception{
        var expectedLanguages = GenericAPIFixture.LANGUAGES;
        var requestId = "we34";

        when(metadataService.findLanguages(requestId)).thenReturn(expectedLanguages);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/languages")
                .header(Constants.X_REQUEST_ID, requestId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MetadataAPITest.CONTENT_TYPE_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("[0].code").value(expectedLanguages.get(0).get("code")))
                .andExpect(MockMvcResultMatchers.jsonPath("[0].label").value(expectedLanguages.get(0).get("label")))
                .andExpect(MockMvcResultMatchers.jsonPath("[1].code").value(expectedLanguages.get(1).get("code")))
                .andExpect(MockMvcResultMatchers.jsonPath("[1].label").value(expectedLanguages.get(1).get("label")))
                .andDo(document("getLanguages",
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestHeaders(
                                HeaderDocumentation.headerWithName(Constants.X_REQUEST_ID).description(MetadataAPITest.REQUESTID_DESCRIPTION)),
                        responseFields(
                                PayloadDocumentation.fieldWithPath("[].code").description("The language code"),
                                PayloadDocumentation.fieldWithPath("[].label").description("The name of the language"))));

        verify(metadataService).findLanguages(requestId);
    }

}