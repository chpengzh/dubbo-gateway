package org.apache.dubbo.gateway.client.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class InnerJsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String toJson(Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }

    public static String safeToString(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (Throwable ignore) {
            return "";
        }
    }

    public static <T> T fromJson(String json, Class<T> type) throws IOException {
        return MAPPER.readValue(json, type);
    }

    public static <T> T fromJson(String json, TypeReference<T> type) throws IOException {
        return MAPPER.readValue(json, type);
    }
}