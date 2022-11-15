package com.instantsystem.demo.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JsonHelperTest {

    public String json = "{" +
            "\"stringField\" : \"value1\"," +
            "\"doubleField\" : 123.456," +
            "\"integerField\" : 789," +
            "\"objectField\" : {" +
            "   \"objectStringField\" : \"value2\"," +
            "   \"objectDoubleField\" : 987.654," +
            "   \"objectIntegerField\" : 321" +
            "}" +
            "}";

    JSONObject jsonObject;

    @BeforeEach
    public void prepare() throws JSONException {
        jsonObject = new JSONObject(json);
    }

    @Test
    public void extractStringFromJson() {
        String stringField = JsonHelper.extractStringValueFromJson(jsonObject, "stringField");
        assertNotNull(stringField);
        assertEquals(stringField, "value1");
    }

    @Test
    public void extractDoubleFromJson() {
        Double doubleField = JsonHelper.extractDoubleValueFromJson(jsonObject, "doubleField");
        assertNotNull(doubleField);
        assertEquals(doubleField, 123.456);
    }

    @Test
    public void extractIntegerFromJson() {
        Integer integerField = JsonHelper.extractIntegerValueFromJson(jsonObject, "integerField");
        assertNotNull(integerField);
        assertEquals(integerField, 789);
    }

    @Test
    public void extractStringFromJsonObject() {
        String stringField = JsonHelper.extractStringValueFromJson(jsonObject, "objectField.objectStringField");
        assertNotNull(stringField);
        assertEquals(stringField, "value2");
    }

    @Test
    public void extractDoubleFromJsonObject() {
        Double doubleField = JsonHelper.extractDoubleValueFromJson(jsonObject, "objectField.objectDoubleField");
        assertNotNull(doubleField);
        assertEquals(doubleField, 987.654);
    }

    @Test
    public void extractIntegerFromJsonObject() {
        Integer integerField = JsonHelper.extractIntegerValueFromJson(jsonObject, "objectField.objectIntegerField");
        assertNotNull(integerField);
        assertEquals(integerField, 321);
    }
}