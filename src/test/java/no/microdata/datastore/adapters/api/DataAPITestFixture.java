package no.microdata.datastore.adapters.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

class DataAPITestFixture {

    static final Map datastructureFnr() throws JsonProcessingException {
        String json = """
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
                          "description": "Stoppdato for forløpsdata."
                        }
                      ],
                      "unitType": {
                        "description": "Person",
                        "label": "Person",
                        "name": "PERSON"
                      },
                      "variableRole": "Stop",
                      "datums": "https://data-service.staging-bip-app.ssb.no/retrieveResultSet?file_name=624f0e13-9f85-49fe-8ad4-9239d4b45858.parquet"
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
                          "description": "Startdato for forløpsdata eller måletidspunkt for statusdata/tverrsnittsdata."
                        }
                      ],
                      "variableRole": "Start",
                      "datums": "https://data-service.staging-bip-app.ssb.no/retrieveResultSet?file_name=624f0e13-9f85-49fe-8ad4-9239d4b45858.parquet"
                    }
                  ],
                  "identifierVariables": [
                    {
                      "label": "Fødselsnummer kryptert (HASH)",
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
                          "description": "Person identifisert med kryptert (HASH) fødselsnummer."
                        }
                      ],
                      "variableRole": "Identifier",
                      "datums": "https://data-service.staging-bip-app.ssb.no/retrieveResultSet?file_name=624f0e13-9f85-49fe-8ad4-9239d4b45858.parquet"
                    }
                  ],
                  "measureVariable": {
                    "dataType": "String",
                    "label": "Fødselsnummer",
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
                        "description": "Variabelen viser fødselsnummer, det vil si fødselsdag, -måned og -år (6 siffer) og personnummer (5 siffer)"
                      }
                    ],
                    "variableRole": "Measure",
                    "datums": "https://data-service.staging-bip-app.ssb.no/retrieveResultSet?file_name=624f0e13-9f85-49fe-8ad4-9239d4b45858.parquet"
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

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(json, Map.class);

        return map;
    }
}