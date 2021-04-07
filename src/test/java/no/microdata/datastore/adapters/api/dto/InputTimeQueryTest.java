package no.microdata.datastore.adapters.api.dto;

import no.microdata.datastore.exceptions.BadRequestException;
import no.microdata.datastore.exceptions.MicrodataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import static no.microdata.datastore.adapters.api.ErrorMessage.*;
import static org.junit.jupiter.api.Assertions.*;

public class InputTimeQueryTest {

    Credentials credentials;

    @BeforeEach
    void setup(){
        credentials = new Credentials();
        credentials.setUsername(Credentials.VALID_USERNAME);
        credentials.setPassword(Credentials.VALID_PASSWORD);
    }

    @DisplayName("should validate time query with all required fields set")
    @Test
    void testSunnyDay(){
        InputTimeQuery inputQuery = new InputTimeQuery();
        inputQuery.setDataStructureName("KJONN");
        inputQuery.setDate(1259649003000L);
        inputQuery.setVersion("1.1.0.0");
        inputQuery.setCredentials(credentials);

        boolean isValid = inputQuery.validate();
        assertTrue (isValid);
    }

    @DisplayName("should NOT validate when required field dataStructureName is missing")
    @Test
    void testMissingDataStructureName(){
        InputTimeQuery inputQuery = new InputTimeQuery();
        inputQuery.setDate(1259649003000L);
        inputQuery.setCredentials(credentials);

        Throwable exceptionThatWasThrown = assertThrows(BadRequestException.class, () -> {
            inputQuery.validate();
        });

        String expected = MicrodataException.toJsonString(requestValidationError(INPUT_FIELD_DATASTRUCTURE_NAME));
        assertEquals(expected, exceptionThatWasThrown.getMessage());
    }

    @DisplayName("should NOT validate when required field start is missing")
    @Test
    void testMissingStart(){
        InputTimeQuery inputQuery = new InputTimeQuery();
        inputQuery.setDataStructureName("KJONN");
        inputQuery.setVersion("1.1.0.0");
        inputQuery.setCredentials(credentials);

        Throwable exceptionThatWasThrown = assertThrows(BadRequestException.class, () -> {
            inputQuery.validate();
        });

        String expected = MicrodataException.toJsonString(requestValidationError(INPUT_FIELD_START_DATE));
        assertEquals(expected, exceptionThatWasThrown.getMessage());
    }

    @DisplayName("should NOT validate when version is not 4 level numeric version")
    @Test
    void testWrongVersionFormat() {
        InputTimeQuery inputQuery = new InputTimeQuery();
        inputQuery.setDataStructureName("KJONN");
        inputQuery.setDate(1259649003000L);
        inputQuery.setVersion("1.1.0");
        inputQuery.setCredentials(credentials);

        Throwable exceptionThatWasThrown = assertThrows(BadRequestException.class, () -> {
            inputQuery.validate();
        });

        String expected = MicrodataException.toJsonString(versionValidationError("1.1.0"));
        assertEquals(expected, exceptionThatWasThrown.getMessage());
    }

    @DisplayName("should return toString() with required properties")
    @Test
    void testToStringReqProps() {
        InputTimeQuery inputQuery = new InputTimeQuery();
        inputQuery.setDataStructureName("KJONN");
        inputQuery.setDate(1259649003000L);
        inputQuery.setCredentials(credentials);

        String expected = "InputTimeQuery[date=1259649003000, dataStructureName='KJONN', version='null', " +
                "values=null, population=0, intervalFilter='null', includeAttributes=false]";
        String actual = inputQuery.toString();

        assertEquals(expected, actual);
    }

    @DisplayName("should return toString() with all properties")
    @Test
    void testToStringAllProps() {
        InputTimeQuery inputQuery = new InputTimeQuery();
        inputQuery.setDataStructureName("KJONN");
        inputQuery.setDate(1259649003000L);
        inputQuery.setVersion("1.1.0.0");
        inputQuery.setValues(Arrays.asList("a","b","c"));
        inputQuery.setPopulation(
                Map.of(
                        "PersonID", new HashSet<Long>(Arrays.asList(1L, 2L, 3L)),
                        "unitIds", new HashSet<Long>(Arrays.asList(100L, 200L, 300L))
                ));
        inputQuery.setIntervalFilter("[0, 100]");
        inputQuery.setIncludeAttributes(true);
        inputQuery.setCredentials(credentials);

        String expected = "InputTimeQuery[date=1259649003000, dataStructureName='KJONN', version='1.1.0.0', values=3, " +
                "population=3, intervalFilter='[0, 100]', includeAttributes=true]";

        String actual = inputQuery.toString();

        assertEquals(expected, actual);
    }
}