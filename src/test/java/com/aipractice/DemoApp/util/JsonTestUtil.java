package com.aipractice.DemoApp.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class JsonTestUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T readJsonFromFile(String filename, Class<T> valueType) throws IOException {
        try (InputStream is = JsonTestUtil.class.getClassLoader().getResourceAsStream(filename)) {
            if (is == null) throw new IOException("Test resource not found: " + filename);
            return mapper.readValue(is, valueType);
        }
    }
}
