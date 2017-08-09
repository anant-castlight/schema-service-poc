package com.castlight.dataversioningpoc.hibernateenvers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import java.io.IOException;

/**
 * Created by anantm on 7/30/17.
 */
public class JsonUtil {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();

    public static boolean isJsonSchemaChanged(String AlredyPresentSchema, String newSchema) {
        JsonNode tree1 = null;
        JsonNode tree2 = null;
        try {
            tree1 = mapper.readTree(AlredyPresentSchema);
            tree2 = mapper.readTree(newSchema);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return !tree1.equals(tree2);
    }

    public static boolean isJsonSchema(String jsonSchema) throws IOException{
        mapper.readTree(jsonSchema);
        return true;
    }
}
