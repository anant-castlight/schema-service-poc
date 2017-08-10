package com.castlight.dataversioningpoc.manualsemanticversions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.InvalidSchemaException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ListProcessingReport;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.processors.syntax.SyntaxValidator;

import java.io.IOException;

/**
 * Created by anantm on 7/30/17.
 */
public class JsonUtil {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();

    public static boolean isJsonSchemaChanged(JsonNode AlredyPresentSchema, JsonNode newSchema) {
        return !AlredyPresentSchema.equals(newSchema);
    }

    public static boolean isJsonSchema(String jsonSchema) throws IOException, ProcessingException {
        JsonNode node = mapper.readTree(jsonSchema);
        SyntaxValidator syntaxValidator = JsonSchemaFactory.byDefault().getSyntaxValidator();
        ProcessingReport report = syntaxValidator.validateSchema(node);
        if(!report.isSuccess()) {
            throw new ProcessingException(report.toString());
        }
        return true;
    }
}
