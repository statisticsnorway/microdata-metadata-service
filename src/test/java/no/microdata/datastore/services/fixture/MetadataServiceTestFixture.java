package no.microdata.datastore.services.fixture;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MetadataServiceTestFixture {


    public static final List<Map> expectedTwoDatastructuresWithAttrs() throws Exception {
        return Arrays.asList(new Map[]{
                testPersonIncome(),
                testPersonPets()
        });
    }

    public static final List<Map> expectedTwoDatastructuresNoAttrs() throws Exception {
        Map testPersonIncome = testPersonIncome();
        Map testPersonPets = testPersonPets();
        testPersonIncome.remove("attributeVariables");
        testPersonPets.remove("attributeVariables");
        return Arrays.asList(new Map[]{testPersonIncome, testPersonPets});
    }

    public static final Map datastoreFile() throws Exception {
        return stringAsMap(DATASTORE_FILE);
    }

    public static final Map versionsFile() throws Exception {
        return stringAsMap(VERSIONS_FILE);
    }

    public static final Map expectedDatastoreVersions() throws Exception {
        return stringAsMap(EXPECTED_DATASTORE_VERSIONS);
    }

    public static final Map metadataAll() throws Exception {
        return stringAsMap(METADATA_ALL);
    }

    public static final Map testPersonIncome() throws Exception {
        return stringAsMap(TEST_PERSON_INCOME);
    }

    public static final Map testPersonPets() throws Exception {
        return stringAsMap(TEST_PERSON_PETS);
    }

    private static Map stringAsMap(String json) throws com.fasterxml.jackson.core.JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map map = mapper.readValue(json, Map.class);
        return map;
    }

    static final String DATASTORE_FILE = """
          {
             "name": "no.ssb.test",
             "label": "Test data fra SSB",
             "description": "Syntetiske registerdata som inngår i SSBs test"
          }      
        """;

    static final String VERSIONS_FILE = """
        {
            "versions": [
                {
                    "version": "1.0.0.0",
                    "description": "Dette er description til testversjon 1.0.0.0",
                    "releaseTime": 1607332752,
                    "languageCode": "no",
                    "dataStructureUpdates": [
                        {
                            "description": "Første publisering",
                            "name": "TEST_PERSON_INCOME",
                            "operation": "ADD"
                        },
                        {
                            "description": "Første publisering",
                            "name": "TEST_PERSON_PETS",
                            "operation": "ADD"
                        }
                    ],
                    "updateType": "MAJOR"
                }
            ]
        }
    """;

    static final String EXPECTED_DATASTORE_VERSIONS = """
        {
            "name": "no.ssb.test",
            "label": "Test data fra SSB",
            "description": "Syntetiske registerdata som inngår i SSBs test",
            "versions": [
                {
                    "version": "1.0.0.0",
                    "description": "Dette er description til testversjon 1.0.0.0",
                    "releaseTime": 1607332752,
                    "languageCode": "no",
                    "dataStructureUpdates": [
                        {
                            "description": "Første publisering",
                            "name": "TEST_PERSON_INCOME",
                            "operation": "ADD"
                        },
                        {
                            "description": "Første publisering",
                            "name": "TEST_PERSON_PETS",
                            "operation": "ADD"
                        }
                    ],
                    "updateType": "MAJOR"
                }
            ]
        }
            
    """;

    static final String METADATA_ALL = """
    {
        "dataStore": {
            "name": "no.ssb.test",
            "label": "Test data fra SSB",
            "description": "Syntetiske registerdata som inngår i SSBs test",
            "languageCode": "no"
        },
        "dataStructures": [
            {
                "attributeVariables": [
                    {
                        "name": "START",
                        "label": "Startdato",
                        "representedVariables": [
                            {
                                "validPeriod": {
                                    "start": 16801,
                                    "stop": 18261
                                },
                                "description": "Startdato/måletidspunktet for hendelsen",
                                "valueDomain": {
                                    "description": "N/A",
                                    "unitOfMeasure": "N/A"
                                }
                            }
                        ],
                        "dataType": "Instant",
                        "variableRole": "Start"
                    },
                    {
                        "name": "STOP",
                        "label": "Stoppdato",
                        "representedVariables": [
                            {
                                "validPeriod": {
                                    "start": 16801,
                                    "stop": 18261
                                },
                                "description": "Stoppdato/sluttdato for hendelsen",
                                "valueDomain": {
                                    "description": "N/A",
                                    "unitOfMeasure": "N/A"
                                }
                            }
                        ],
                        "dataType": "Instant",
                        "variableRole": "Stop"
                    }
                ],
                "identifierVariables": [
                    {
                        "name": "PERSON_ID_1",
                        "label": "Personidentifikator",
                        "dataType": "Long",
                        "representedVariables": [
                            {
                                "validPeriod": {
                                    "start": 16801,
                                    "stop": 18261
                                },
                                "description": "Identifikator for person",
                                "valueDomain": {
                                    "description": "N/A",
                                    "unitOfMeasure": "N/A"
                                }
                            }
                        ],
                        "keyType": {
                            "name": "PERSON",
                            "label": "Person",
                            "description": "Statistisk enhet er person (individ, enkeltmenneske)."
                        },
                        "format": "RandomUInt48",
                        "variableRole": "Identifier"
                    }
                ],
                "measureVariable": {
                    "name": "TEST_PERSON_INCOME",
                    "label": "Inntekt",
                    "dataType": "Long",
                    "representedVariables": [
                        {
                            "validPeriod": {
                                "start": 16801,
                                "stop": 18261
                            },
                            "description": "Personinntekt.",
                            "valueDomain": {
                                "description": "Personinntekt i norske kroner (NOK)."
                            }
                        }
                    ],
                    "variableRole": "Measure"
                },
                "name": "TEST_PERSON_INCOME",
                "populationDescription": "Alle personer med inntekt.",
                "temporality": "ACCUMULATED",
                "temporalCoverage": {
                    "start": 16801,
                    "stop": 18261
                },
                "subjectFields": [
                    "Inntekt"
                ],
                "languageCode": "no"
            },
            {
                "attributeVariables": [
                    {
                        "name": "START",
                        "label": "Startdato",
                        "representedVariables": [
                            {
                                "validPeriod": {
                                    "start": 5562,
                                    "stop": 18579
                                },
                                "description": "Startdato/måletidspunktet for hendelsen",
                                "valueDomain": {
                                    "description": "N/A",
                                    "unitOfMeasure": "N/A"
                                }
                            }
                        ],
                        "dataType": "Instant",
                        "variableRole": "Start"
                    },
                    {
                        "name": "STOP",
                        "label": "Stoppdato",
                        "representedVariables": [
                            {
                                "validPeriod": {
                                    "start": 5562,
                                    "stop": 18579
                                },
                                "description": "Stoppdato/sluttdato for hendelsen",
                                "valueDomain": {
                                    "description": "N/A",
                                    "unitOfMeasure": "N/A"
                                }
                            }
                        ],
                        "dataType": "Instant",
                        "variableRole": "Stop"
                    }
                ],
                "identifierVariables": [
                    {
                        "name": "PERSON_ID_1",
                        "label": "Personidentifikator",
                        "dataType": "Long",
                        "representedVariables": [
                            {
                                "validPeriod": {
                                    "start": 5562,
                                    "stop": 18579
                                },
                                "description": "Identifikator for person",
                                "valueDomain": {
                                    "description": "N/A",
                                    "unitOfMeasure": "N/A"
                                }
                            }
                        ],
                        "keyType": {
                            "name": "PERSON",
                            "label": "Person",
                            "description": "Statistisk enhet er person (individ, enkeltmenneske)."
                        },
                        "format": "RandomUInt48",
                        "variableRole": "Identifier"
                    }
                ],
                "measureVariable": {
                    "name": "TEST_PERSON_PETS",
                    "label": "Kjæledyr",
                    "dataType": "String",
                    "representedVariables": [
                        {
                            "description": "Mine kjæledyr.",
                            "validPeriod": {
                                "start": 0
                            },
                            "valueDomain": {
                                "codeList": [
                                    {
                                        "category": "Katt",
                                        "code": "CAT"
                                    },
                                    {
                                        "category": "Hund",
                                        "code": "DOG"
                                    },
                                    {
                                        "category": "Fisk",
                                        "code": "FISH"
                                    },
                                    {
                                        "category": "Fugl",
                                        "code": "BIRD"
                                    },
                                    {
                                        "category": "Kanin",
                                        "code": "RABBIT"
                                    },
                                    {
                                        "category": "Hamster",
                                        "code": "HAMSTER"
                                    }
                                ],
                                "missingValues": []
                            }
                        }
                    ],
                    "variableRole": "Measure"
                },
                "name": "TEST_PERSON_PETS",
                "populationDescription": "Alle personer som eier et kjæledyr.",
                "temporality": "EVENT",
                "temporalCoverage": {
                    "start": 5562,
                    "stop": 18579
                },
                "subjectFields": [
                    "Befolkning"
                ],
                "languageCode": "no"
            }
        ]
    }                
    """;

    static final String TEST_PERSON_INCOME = """
    {
       "attributeVariables": [
           {
               "name": "START",
               "label": "Startdato",
               "representedVariables": [
                   {
                       "validPeriod": {
                           "start": 16801,
                           "stop": 18261
                       },
                       "description": "Startdato/måletidspunktet for hendelsen",
                       "valueDomain": {
                           "description": "N/A",
                           "unitOfMeasure": "N/A"
                       }
                   }
               ],
               "dataType": "Instant",
               "variableRole": "Start"
           },
           {
               "name": "STOP",
               "label": "Stoppdato",
               "representedVariables": [
                   {
                       "validPeriod": {
                           "start": 16801,
                           "stop": 18261
                       },
                       "description": "Stoppdato/sluttdato for hendelsen",
                       "valueDomain": {
                           "description": "N/A",
                           "unitOfMeasure": "N/A"
                       }
                   }
               ],
               "dataType": "Instant",
               "variableRole": "Stop"
           }
       ],
       "identifierVariables": [
           {
               "name": "PERSON_ID_1",
               "label": "Personidentifikator",
               "dataType": "Long",
               "representedVariables": [
                   {
                       "validPeriod": {
                           "start": 16801,
                           "stop": 18261
                       },
                       "description": "Identifikator for person",
                       "valueDomain": {
                           "description": "N/A",
                           "unitOfMeasure": "N/A"
                       }
                   }
               ],
               "keyType": {
                   "name": "PERSON",
                   "label": "Person",
                   "description": "Statistisk enhet er person (individ, enkeltmenneske)."
               },
               "format": "RandomUInt48",
               "variableRole": "Identifier"
           }
       ],
       "measureVariable": {
           "name": "TEST_PERSON_INCOME",
           "label": "Inntekt",
           "dataType": "Long",
           "representedVariables": [
               {
                   "validPeriod": {
                       "start": 16801,
                       "stop": 18261
                   },
                   "description": "Personinntekt.",
                   "valueDomain": {
                       "description": "Personinntekt i norske kroner (NOK)."
                   }
               }
           ],
           "variableRole": "Measure"
       },
       "name": "TEST_PERSON_INCOME",
       "populationDescription": "Alle personer med inntekt.",
       "temporality": "ACCUMULATED",
       "temporalCoverage": {
           "start": 16801,
           "stop": 18261
       },
       "subjectFields": [
           "Inntekt"
       ],
       "languageCode": "no"
    }     
    """;

    static final String TEST_PERSON_PETS = """
    {
        "attributeVariables": [
            {
                "name": "START",
                "label": "Startdato",
                "representedVariables": [
                    {
                        "validPeriod": {
                            "start": 5562,
                            "stop": 18579
                        },
                        "description": "Startdato/måletidspunktet for hendelsen",
                        "valueDomain": {
                            "description": "N/A",
                            "unitOfMeasure": "N/A"
                        }
                    }
                ],
                "dataType": "Instant",
                "variableRole": "Start"
            },
            {
                "name": "STOP",
                "label": "Stoppdato",
                "representedVariables": [
                    {
                        "validPeriod": {
                            "start": 5562,
                            "stop": 18579
                        },
                        "description": "Stoppdato/sluttdato for hendelsen",
                        "valueDomain": {
                            "description": "N/A",
                            "unitOfMeasure": "N/A"
                        }
                    }
                ],
                "dataType": "Instant",
                "variableRole": "Stop"
            }
        ],
        "identifierVariables": [
            {
                "name": "PERSON_ID_1",
                "label": "Personidentifikator",
                "dataType": "Long",
                "representedVariables": [
                    {
                        "validPeriod": {
                            "start": 5562,
                            "stop": 18579
                        },
                        "description": "Identifikator for person",
                        "valueDomain": {
                            "description": "N/A",
                            "unitOfMeasure": "N/A"
                        }
                    }
                ],
                "keyType": {
                    "name": "PERSON",
                    "label": "Person",
                    "description": "Statistisk enhet er person (individ, enkeltmenneske)."
                },
                "format": "RandomUInt48",
                "variableRole": "Identifier"
            }
        ],
        "measureVariable": {
            "name": "TEST_PERSON_PETS",
            "label": "Kjæledyr",
            "dataType": "String",
            "representedVariables": [
                {
                    "description": "Mine kjæledyr.",
                    "validPeriod": {
                        "start": 0
                    },
                    "valueDomain": {
                        "codeList": [
                            {
                                "category": "Katt",
                                "code": "CAT"
                            },
                            {
                                "category": "Hund",
                                "code": "DOG"
                            },
                            {
                                "category": "Fisk",
                                "code": "FISH"
                            },
                            {
                                "category": "Fugl",
                                "code": "BIRD"
                            },
                            {
                                "category": "Kanin",
                                "code": "RABBIT"
                            },
                            {
                                "category": "Hamster",
                                "code": "HAMSTER"
                            }
                        ],
                        "missingValues": []
                    }
                }
            ],
            "variableRole": "Measure"
        },
        "name": "TEST_PERSON_PETS",
        "populationDescription": "Alle personer som eier et kjæledyr.",
        "temporality": "EVENT",
        "temporalCoverage": {
            "start": 5562,
            "stop": 18579
        },
        "subjectFields": [
            "Befolkning"
        ],
        "languageCode": "no"
    }
    """;
}