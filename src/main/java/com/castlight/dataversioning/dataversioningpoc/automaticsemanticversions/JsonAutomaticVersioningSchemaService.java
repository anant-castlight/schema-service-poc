package com.castlight.dataversioning.dataversioningpoc.automaticsemanticversions;

import com.castlight.dataversioning.dataversioningpoc.hibernateenvers.JsonUtil;
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

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity saveJSONSchema(@RequestBody String requestJson) {
        ResponseEntity responseEntity = null;
        try {
            JsonNode node = mapper.readTree(requestJson);
            String name = mapper.convertValue(node.get("name"), String.class);
            JsonNode jsonSchema = mapper.convertValue(node.get("jsonSchema"), JsonNode.class);
            String description = mapper.convertValue(node.get("description"), String.class);
            String latestVersionFromDb = jsonSchemaDAO.getLatestVersionForSchema(name);
            if(!isDuplicateSchemaDetailsPresent(name, latestVersionFromDb, jsonSchema)) {
                jsonSchemaDAO.create(name, jsonSchema.toString(), description, version);
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

    @RequestMapping(value = "/{name_and_version}", method = RequestMethod.PUT)
    public ResponseEntity updateJSONSchema(@PathVariable("name_and_version") String name,
                                           @RequestBody String requestJson) {

        ResponseEntity responseEntity = null;
        try {
            JsonNode node = mapper.readTree(requestJson);
            JsonNode jsonSchema = mapper.convertValue(node.get("jsonSchema"), JsonNode.class);
            String description = mapper.convertValue(node.get("description"), String.class);
            jsonSchemaDAO.update(name.split("-")[0], jsonSchema.toString(), description, name.split("-")[1]);
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

    @RequestMapping(value = "/{name_and_version}", method = RequestMethod.GET)
    public ResponseEntity getJSONSchema(@PathVariable("name_and_version") String name) {

        JsonAutomaticVersioningSchemaDetails jsonSchemaDetails = null;
        ResponseEntity responseEntity = null;
        try {
            jsonSchemaDetails = jsonSchemaDAO.get(name.split("-")[0], name.split("-")[1]);
            responseEntity = new ResponseEntity(jsonSchemaDetails, HttpStatus.FOUND);
        } catch (NoResultException nre) {
            responseEntity = new ResponseEntity(nre.getMessage(), HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
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

    @RequestMapping(value = "/{name_and_version}/changes", method = RequestMethod.GET)
    public ResponseEntity getAllModificationsOnVersion(@PathVariable("name_and_version") String name) {
        ResponseEntity responseEntity = null;
        List<JsonAutomaticVersioningSchemaDetails> jsonSchemaDetailsList = null;
        try {
            jsonSchemaDetailsList = jsonSchemaDAO.getAllModificationsOnVersion(name.split("-")[0], name.split("-")[1]);
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
            isDuplicateSchema = true;
        }
        return isDuplicateSchema;
    }

}
