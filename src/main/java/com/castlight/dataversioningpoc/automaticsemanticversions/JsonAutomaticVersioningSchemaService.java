package com.castlight.dataversioningpoc.automaticsemanticversions;

import com.castlight.dataversioningpoc.hibernateenvers.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.List;


/**
 * Created by anantm on 7/30/17.
 */
@RestController
@RequestMapping("/json_automatic_versioning_schema")
public class JsonAutomaticVersioningSchemaService {

    private JsonAutomaticVersioningSchemaDAO jsonSchemaDAO = new JsonAutomaticVersioningSchemaDAO();
    private ObjectMapper mapper = new ObjectMapper();
    private TestJsonObjectDetailsDAO testJsonObjectDetailsDAO = new TestJsonObjectDetailsDAO();

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity saveJSONSchema(@RequestBody String requestJson) {
        ResponseEntity responseEntity = null;
        try {
            JsonNode node = mapper.readTree(requestJson);
            String name = mapper.convertValue(node.get("name"), String.class);
            JsonNode jsonSchema = mapper.convertValue(node.get("jsonSchema"), JsonNode.class);
            String description = mapper.convertValue(node.get("description"), String.class);
            Boolean isPatch = mapper.convertValue(node.get("isPatch"), Boolean.class);
            String latestVersionFromDb = jsonSchemaDAO.getLatestVersionForSchema(name);
            if(!isDuplicateSchemaDetailsPresent(name, latestVersionFromDb, jsonSchema)) {
                String newVersion = generateSemanticVersion(name, jsonSchema.toString(), latestVersionFromDb, isPatch);
                jsonSchemaDAO.create(name, jsonSchema.toString(), description, newVersion);
            }
            responseEntity = new ResponseEntity(null, HttpStatus.CREATED);
        }
        catch (JsonProcessingException | ProcessingException pe) {
            responseEntity = new ResponseEntity(pe.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        catch (IOException ioe) {
            responseEntity = new ResponseEntity(ioe.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @RequestMapping(value = "/{name_and_version:.*}", method = RequestMethod.PUT)
    public ResponseEntity updateJSONSchema(@PathVariable("name_and_version") String nameAndVersion,
                                           @RequestBody String requestJson) {

        ResponseEntity responseEntity = null;
        try {
            JsonNode node = mapper.readTree(requestJson);
            JsonNode jsonSchema = mapper.convertValue(node.get("jsonSchema"), JsonNode.class);
            String description = mapper.convertValue(node.get("description"), String.class);
            jsonSchemaDAO.update(nameAndVersion.split("-")[0], jsonSchema.toString(), description, nameAndVersion.split("-")[1]);
            responseEntity = new ResponseEntity(null, HttpStatus.OK);
        } catch (NoResultException nre) {
            responseEntity = new ResponseEntity(nre.getMessage(), HttpStatus.NOT_FOUND);
        } catch (JsonProcessingException | ProcessingException pe) {
            responseEntity = new ResponseEntity(pe.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        catch (IOException ioe) {
            responseEntity = new ResponseEntity(ioe.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;

    }

    @RequestMapping(value = "/{name_and_version:.*}", method = RequestMethod.GET)
    public ResponseEntity getJSONSchema(@PathVariable("name_and_version") String nameAndVersion) {

        JsonAutomaticVersioningSchemaDetails jsonSchemaDetails = null;
        ResponseEntity responseEntity = null;
        try {
            jsonSchemaDetails = jsonSchemaDAO.get(nameAndVersion.split("-")[0], nameAndVersion.split("-")[1]);
            responseEntity = new ResponseEntity(jsonSchemaDetails, HttpStatus.FOUND);
        } catch (NoResultException nre) {
            responseEntity = new ResponseEntity(nre.getMessage(), HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    @RequestMapping(value = "/{name}/versions", method = RequestMethod.GET)
    public ResponseEntity getAllVersions(@PathVariable("name") String name) {
        ResponseEntity responseEntity = null;
        List<JsonAutomaticVersioningSchemaDetails> jsonSchemaDetailsList = null;
        try {
            jsonSchemaDetailsList = jsonSchemaDAO.getAllVersions(name);
            responseEntity = new ResponseEntity(jsonSchemaDetailsList, HttpStatus.OK);
        } catch (NoResultException nre) {
            responseEntity = new ResponseEntity(nre.getMessage(), HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    @RequestMapping(value = "/{name_and_version:.*}/changes", method = RequestMethod.GET)
    public ResponseEntity getAllModificationsOnVersion(@PathVariable("name_and_version") String nameAndVersion) {
        ResponseEntity responseEntity = null;
        List<JsonAutomaticVersioningSchemaDetails> jsonSchemaDetailsList = null;
        try {
            jsonSchemaDetailsList = jsonSchemaDAO.getAllModificationsOnVersion(nameAndVersion.split("-")[0], nameAndVersion.split("-")[1]);
            responseEntity = new ResponseEntity(jsonSchemaDetailsList, HttpStatus.OK);
        } catch (NoResultException nre) {
            responseEntity = new ResponseEntity(nre.getMessage(), HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }


    private boolean isDuplicateSchemaDetailsPresent(String name, String version, JsonNode jsonNode) throws IOException, ProcessingException {
        boolean isDuplicateSchema;
        try {
            JsonAutomaticVersioningSchemaDetails jsonSchemaDetails = jsonSchemaDAO.get(name, version);
            isDuplicateSchema = !JsonUtil.isJsonSchemaChanged(jsonSchemaDetails.getJsonSchema(), jsonNode.toString());
        } catch (NoResultException nre) {
            isDuplicateSchema = false;
        }
        return isDuplicateSchema;
    }

    private String generateSemanticVersion(String name, String jsonSchema, String latestVersionFromDb, Boolean isPatch) throws ProcessingException {

        SemanticVersion semanticVersion = new SemanticVersion(latestVersionFromDb);
        boolean isSchemaCompatibleWithPreviousVersion = validatePreviousVersionCompatibilityForSchema(name, latestVersionFromDb, jsonSchema);
        if(isSchemaCompatibleWithPreviousVersion && (isPatch!=null && isPatch)) {
            semanticVersion.setPatchVersion(semanticVersion.getPatchVersion()+1);
        }
        else if(isSchemaCompatibleWithPreviousVersion && (isPatch==null || !isPatch)) {
            semanticVersion.setMinorVersion(semanticVersion.getMinorVersion()+1);
            semanticVersion.setPatchVersion(0);
        }
        else if(!isSchemaCompatibleWithPreviousVersion && (isPatch==null || !isPatch)) {
            semanticVersion.setMajorVersion(semanticVersion.getMajorVersion()+1);
            semanticVersion.setMinorVersion(0);
            semanticVersion.setPatchVersion(0);
        }
        else{
            throw new ProcessingException("Json schema is not compatible with previous versions");
        }
        return semanticVersion.toString();
    }

    private boolean validatePreviousVersionCompatibilityForSchema(String name, String latestVersionFromDb, String jsonSchema) {

        boolean isCompatible = true;
        try {
            JsonAutomaticVersioningSchemaDetails jsonAutomaticVersioningSchemaDetails = jsonSchemaDAO.get(name, latestVersionFromDb);
            if (jsonAutomaticVersioningSchemaDetails != null) {
                List<String> testJsonList = testJsonObjectDetailsDAO.getAllTestJsonForSchema(jsonAutomaticVersioningSchemaDetails.getId());
                for (String testJsonObject : testJsonList) {
                    //TODO: call json validator method
                    if (true) {
                        isCompatible = false;
                        break;
                    }
                }
            } else {
                isCompatible = false;
            }
        }
        catch (NoResultException e) {
            isCompatible = false;
        }
        return isCompatible;
    }

}
