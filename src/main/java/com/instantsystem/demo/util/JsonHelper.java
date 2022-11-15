package com.instantsystem.demo.util;

import com.jayway.jsonpath.JsonPath;
import org.json.JSONObject;

public class JsonHelper {

    /**
     * Retrieve an element from {@link JSONObject} as {@link String}
     * @param element   The {@link JSONObject} we extract the data from
     * @param path      The path to the data
     * @return  A {@link String} value of the data we extracted
     */
    public static String extractStringValueFromJson(JSONObject element, String path) {
        return (String) extractValueFromJson(element, path, String.class);
    }


    /**
     * Retrieve an element from {@link JSONObject} as {@link Double}
     * @param element   The {@link JSONObject} we extract the data from
     * @param path      The path to the data
     * @return  A {@link Double} value of the data we extracted
     */
    public static Double extractDoubleValueFromJson(JSONObject element, String path) {
        return (Double) extractValueFromJson(element, path, Double.class);
    }


    /**
     * Retrieve an element from {@link JSONObject} as {@link Integer}
     * @param element   The {@link JSONObject} we extract the data from
     * @param path      The path to the data
     * @return  A {@link Integer} value of the data we extracted
     */
    public static Integer extractIntegerValueFromJson(JSONObject element, String path) {
        return (Integer) extractValueFromJson(element, path, Integer.class);
    }


    /**
     * Retrieve an element from {@link JSONObject} as {@link Object} that can be cast into whatever {@link Class} we gave
     * @param element   The {@link JSONObject} we extract the data from
     * @param path      The path to the data
     * @param aClass    The Class of the object returned
     * @return  A {@link Object} value of the data we extracted
     */
    public static Object extractValueFromJson(JSONObject element, String path, Class aClass) {
        return JsonPath.parse(element.toString()).read("$." + path, aClass);
    }
}
