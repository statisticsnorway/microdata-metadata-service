package no.microdata.datastore.services.fixture;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public class DataServiceTestFixture {

    public static final Map datastructure() throws Exception {
        return stringAsMap(DATASTRUCTURE);
    }

    private static Map stringAsMap(String json) throws com.fasterxml.jackson.core.JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map map = mapper.readValue(json, Map.class);
        return map;
    }

    public static final Map<String, String> RESPONSE_FROM_DATASERVICE(){
        return Map.of(
                "name", "FNR",
                "dataUrl", "http://127.0.0.1:8000/retrieveResultSet?file_name=DUMMY.parquet"
        );
    }

    public static final Map<String, String> EXPECTED_RESPONSE() throws Exception {
        Map response = datastructure();
        response.put("dataUrl", "http://127.0.0.1:8000/retrieveResultSet?file_name=DUMMY.parquet");
        return response;
    }

    public static Map DATASTRUCTURE_NO_ATTRS() throws Exception {
        Map datastructure = datastructure();
        datastructure.remove("attributeVariables");
        return datastructure;
    }

    public static Map EXPECTED_RESPONSE_NO_ATTRS() throws Exception {
        Map response = DATASTRUCTURE_NO_ATTRS();
        response.put("dataUrl", "http://127.0.0.1:8000/retrieveResultSet?file_name=DUMMY.parquet");
        return response;
    }

    static final String DATASTRUCTURE = """
    {
        "attributeVariables": [
            {
                "name": "END",
                "label": "End",
                "dataType": "Instant",
                "representedVariables": [
                    {
                        "validPeriod": {
                            "start": 946681200
                        },
                        "valueDomain": {
                            "missingValues": [
                               \s
                            ],
                            "codeList": [
                                {
                                    "category": "0 - 19,9 timer",
                                    "code": 1
                                },
                                {
                                    "category": "20 - 29,9 timer",
                                    "code": 2
                                },
                                {
                                    "category": "30 timer og mer",
                                    "code": 3
                                }
                            ]
                        },
                        "description": "Stoppdato for forl\\u00f8psdata."
                    }
                ],
                "unitType": {
                    "description": "Person",
                    "label": "Person",
                    "name": "PERSON"
                },
                "variableRole": "Stop"
            },
            {
                "dataType": "Instant",
                "label": "Start",
                "name": "START",
                "unitType": {
                    "description": "Person",
                    "label": "Person",
                    "name": "PERSON"
                },
                "representedVariables": [
                    {
                        "validPeriod": {
                            "start": 946681200
                        },
                        "valueDomain": {
                            "missingValues": [
                               \s
                            ],
                            "codeList": [
                                {
                                    "category": "0 - 19,9 timer",
                                    "code": 1
                                },
                                {
                                    "category": "20 - 29,9 timer",
                                    "code": 2
                                },
                                {
                                    "category": "30 timer og mer",
                                    "code": 3
                                }
                            ]
                        },
                        "description": "Startdato for forl\\u00f8psdata eller m\\u00e5letidspunkt for statusdata/tverrsnittsdata."
                    }
                ],
                "variableRole": "Start"
            }
        ],
        "identifierVariables": [
            {
                "label": "F\\u00f8dselsnummer kryptert (HASH)",
                "dataType": "String",
                "name": "FNR_HASH",
                "unitType": {
                    "description": "Person",
                    "label": "Person",
                    "name": "PERSON"
                },
                "representedVariables": [
                    {
                        "validPeriod": {
                            "start": 946681200
                        },
                        "valueDomain": {
                            "missingValues": [
                               \s
                            ],
                            "codeList": [
                                {
                                    "category": "0 - 19,9 timer",
                                    "code": 1
                                },
                                {
                                    "category": "20 - 29,9 timer",
                                    "code": 2
                                },
                                {
                                    "category": "30 timer og mer",
                                    "code": 3
                                }
                            ]
                        },
                        "description": "Person identifisert med kryptert (HASH) f\\u00f8dselsnummer."
                    }
                ],
                "variableRole": "Identifier"
            }
        ],
        "measureVariable": {
            "dataType": "String",
            "label": "F\\u00f8dselsnummer",
            "name": "FNR",
            "unitType": {
                "description": "Person",
                "label": "Person",
                "name": "PERSON"
            },
            "representedVariables": [
                {
                    "validPeriod": {
                        "start": 946681200
                    },
                    "valueDomain": {
                        "missingValues": [
                           \s
                        ],
                        "codeList": [
                            {
                                "category": "0 - 19,9 timer",
                                "code": 1
                            },
                            {
                                "category": "20 - 29,9 timer",
                                "code": 2
                            },
                            {
                                "category": "30 timer og mer",
                                "code": 3
                            }
                        ]
                    },
                    "description": "Variabelen viser f\\u00f8dselsnummer, det vil si f\\u00f8dselsdag, -m\\u00e5ned og -\\u00e5r (6 siffer) og personnummer (5 siffer)"
                }
            ],
            "variableRole": "Measure"
        },
        "name": "FNR",
        "temporality": "Event",
        "temporalCoverage": {
            "start": 946681200,
            "stop": 1419980400
        },
        "subjectFields": [
            "ARBEID_LONN"
        ],
        "languageCode": "no"
    }
    """;
}