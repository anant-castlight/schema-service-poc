package com.castlight.dataversioningpoc.manualsemanticversions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.InvalidSchemaException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import javassist.tools.web.BadHttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.NoResultException;
import java.util.List;


/**
 * Created by anantm on 7/30/17.
 */
@RestController
@RequestMapping("/json_manual_versioning_schema")
public class JsonSchemaDetailsController {

    @Autowired
    JsonSchemaDetailsService jsonSchemaDetailsService;

    private ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "/{name}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity saveJSONSchema(@PathVariable("name") String name,
                                         @RequestBody String requestJson) {
        ResponseEntity responseEntity = null;
        try {
            JsonNode node = mapper.readTree(requestJson);
            JsonNode jsonSchema = mapper.convertValue(node.get("jsonSchema"), JsonNode.class);
            String description = mapper.convertValue(node.get("description"), String.class);
            ChangeType changeType = mapper.convertValue(node.get("changeType"), ChangeType.class);
            String latestVersionFromDb = jsonSchemaDetailsService.createJsonSchemaDetails(name,description,jsonSchema,changeType);
            responseEntity = new ResponseEntity("Version "+latestVersionFromDb+" of "+name+" has been created", HttpStatus.CREATED);
        }
        catch (JsonProcessingException | ProcessingException pe) {
            responseEntity = new ResponseEntity(pe.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        catch (Exception ioe) {
            responseEntity = new ResponseEntity(ioe.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @RequestMapping(value = "/{name}/versions/{version:.*}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateJSONSchema(@PathVariable("name") String name,
                                           @PathVariable("version") String version,
                                           @RequestBody String requestJson) {

        ResponseEntity responseEntity = null;
        try {
            JsonNode node = mapper.readTree(requestJson);
            JsonNode jsonSchema = mapper.convertValue(node.get("jsonSchema"), JsonNode.class);
            String description = mapper.convertValue(node.get("description"), String.class);
            ChangeType changeType = mapper.convertValue(node.get("changeType"), ChangeType.class);
            String latestVersionFromDb = jsonSchemaDetailsService.updateOlderVersionJsonSchemaDetails(name, version, description, jsonSchema, changeType);
            responseEntity = new ResponseEntity("Updated Version "+latestVersionFromDb+" of "+name+" has been created", HttpStatus.CREATED);
        } catch (NoResultException nre) {
            responseEntity = new ResponseEntity(nre.getMessage(), HttpStatus.NOT_FOUND);
        } catch (JsonProcessingException | ProcessingException pe) {
            responseEntity = new ResponseEntity(pe.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        catch (Exception ioe) {
            responseEntity = new ResponseEntity(ioe.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;

    }

    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
    public ResponseEntity getLatestJSONSchema(@PathVariable("name") String name) {

        ResponseEntity responseEntity = null;
        try {
            JsonSchemaDetail jsonSchemaDetail = jsonSchemaDetailsService.getLatestJsonSchemaDetails(name);
            if (jsonSchemaDetail != null) {
                responseEntity = new ResponseEntity(jsonSchemaDetail, HttpStatus.OK);
            } else {
                responseEntity = new ResponseEntity("Schema Not Found", HttpStatus.NOT_FOUND);
            }
        }
        catch (NoResultException e) {
            responseEntity = new ResponseEntity("Schema Not Found", HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    @RequestMapping(value = "/{name}/versions/{version:.*}", method = RequestMethod.GET)
    public ResponseEntity getJSONSchema(@PathVariable("name") String name,
                                        @PathVariable("version") String version) {

        ResponseEntity responseEntity = null;
        try {
            JsonSchemaDetail jsonSchemaDetail = jsonSchemaDetailsService.getJsonSchemaDetails(name, version);
            if (jsonSchemaDetail != null) {
                responseEntity = new ResponseEntity(jsonSchemaDetail, HttpStatus.OK);
            } else {
                responseEntity = new ResponseEntity("Requested schema Not Found", HttpStatus.NOT_FOUND);
            }
        }
        catch (NoResultException e) {
            responseEntity = new ResponseEntity("Requested schema Not Found", HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    @RequestMapping(value = "/{name}/versions", method = RequestMethod.GET)
    public ResponseEntity getAllVersions(@PathVariable("name") String name) {
        ResponseEntity responseEntity = null;
        try {
            JsonSchemaAllVersionDetail jsonSchemaAllVersionDetail = jsonSchemaDetailsService.getAllVersionsOfJsonSchema(name);
            responseEntity = new ResponseEntity(jsonSchemaAllVersionDetail, HttpStatus.OK);
        } catch (NoResultException nre) {
            responseEntity = new ResponseEntity("Requested schema Not Found", HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

}
