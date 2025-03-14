#![allow(dead_code)]

use serde_json::{json, Value};
use std::{fs::File, io::Read};

pub fn datastore_versions() -> Value {
    json!({
      "name": "no.ssb.test",
      "label": "Test data fra SSB",
      "description": "Syntetiske registerdata som inngår i SSBs test",
      "versions": [
        {
          "version": "0.0.0.1608000000",
          "description": "Draft",
          "releaseTime": 1608000000,
          "languageCode": "no",
          "dataStructureUpdates": [
            {
              "description": "Første publisering",
              "name": "TEST_PERSON_HOBBIES",
              "operation": "ADD",
              "releaseStatus": "DRAFT"
            },
            {
              "description": "Første publisering",
              "name": "TEST_PERSON_SAVINGS",
              "operation": "ADD",
              "releaseStatus": "PENDING_RELEASE"
            }
          ],
          "updateType": "MINOR"
        },
        {
          "version": "2.0.0.0",
          "description": "Dette er description til testversjon 2.0.0.0",
          "releaseTime": 1607332762,
          "languageCode": "no",
          "dataStructureUpdates": [
            {
              "description": "Fjernet variabel",
              "name": "TEST_PERSON_INCOME",
              "operation": "REMOVE",
              "releaseStatus": "DELETED"
            }
          ],
          "updateType": "MAJOR"
        },
        {
          "version": "1.0.0.0",
          "description": "Dette er description til testversjon 1.0.0.0",
          "releaseTime": 1607332752,
          "languageCode": "no",
          "dataStructureUpdates": [
            {
              "description": "Første publisering",
              "name": "TEST_PERSON_INCOME",
              "operation": "ADD",
              "releaseStatus": "RELEASED"
            },
            {
              "description": "Første publisering",
              "name": "TEST_PERSON_PETS",
              "operation": "ADD",
              "releaseStatus": "RELEASED"
            }
          ],
          "updateType": "MAJOR"
        }
      ]
    })
}

pub fn data_structure_test_person_pets() -> Value {
    json!({
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
    })
}

pub fn data_structure_test_person_income() -> Value {
    json!({
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
    })
}

pub fn test_person_income_data_structure_status() -> Value {
    json!({
        "TEST_PERSON_INCOME": {
            "operation": "REMOVE",
            "releaseStatus": "DELETED", "releaseTime": 1607332762
        }
    })
}

pub fn all_data_structures_status() -> Value {
    json!({
        "TEST_PERSON_HOBBIES": {"operation": "ADD", "releaseStatus": "DRAFT", "releaseTime": 1608000000},
        "TEST_PERSON_INCOME": {"operation": "REMOVE", "releaseStatus": "DELETED", "releaseTime": 1607332762},
        "TEST_PERSON_PETS": {"operation": "ADD", "releaseStatus": "RELEASED", "releaseTime": 1607332752},
        "TEST_PERSON_SAVINGS": {"operation": "ADD", "releaseStatus": "PENDING_RELEASE", "releaseTime": 1608000000}
    })
}

pub fn languages() -> Value {
    json!([
        {"code": "no", "label": "Norsk"}
    ])
}

pub fn metadata_all() -> Value {
    let mut f =
        File::open("tests/resources/test_datastore/datastore/metadata_all__1_0_0.json").unwrap();
    let mut contents = String::new();
    f.read_to_string(&mut contents).unwrap();
    serde_json::from_str(&contents).unwrap()
}
