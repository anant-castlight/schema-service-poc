package com.castlight.dataversioningpoc.manualsemanticversions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.InvalidSchemaException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
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

    @RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity saveJSONSchema(@RequestBody String requestJson) {
        ResponseEntity responseEntity = null;
        try {
            JsonNode node = mapper.readTree(requestJson);
            String name = mapper.convertValue(node.get("name"), String.class);
            JsonNode jsonSchema = mapper.convertValue(node.get("jsonSchema"), JsonNode.class);
            String description = mapper.convertValue(node.get("description"), String.class);
            ChangeType changeType = mapper.convertValue(node.get("changeType"), ChangeType.class);
            String latestVersionFromDb = jsonSchemaDetailsService.saveJsonSchemaDetails(name,description,jsonSchema,changeType);
            responseEntity = new ResponseEntity("Version "+latestVersionFromDb+" of "+name+" has been created", HttpStatus.CREATED);
        }
        catch(InvalidSchemaException e) {
            responseEntity = new ResponseEntity(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        catch (JsonProcessingException | ProcessingException pe) {
            responseEntity = new ResponseEntity(pe.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        catch (Exception ioe) {
            responseEntity = new ResponseEntity(ioe.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    /*@RequestMapping(value = "/{name_and_version:.*}", method = RequestMethod.PUT)
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

    }*/

    @RequestMapping(value = "/{name_and_version:.*}", method = RequestMethod.GET)
    public ResponseEntity getJSONSchema(@PathVariable("name_and_version") String nameAndVersion) {

        ResponseEntity responseEntity = null;
        try {
            JsonSchemaDetail jsonSchemaDetail = jsonSchemaDetailsService.getJsonSchemaDetails(nameAndVersion);
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

    @RequestMapping(value = "/{name}/versions", method = RequestMethod.GET)
    public ResponseEntity getAllVersions(@PathVariable("name") String name) {
        ResponseEntity responseEntity = null;
        try {
            JsonSchemaAllVersionDetail jsonSchemaAllVersionDetail = jsonSchemaDetailsService.getAllVersionsOfJsonSchema(name);
            responseEntity = new ResponseEntity(jsonSchemaAllVersionDetail, HttpStatus.OK);
        } catch (NoResultException nre) {
            responseEntity = new ResponseEntity("Schema Not Found", HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    /*@RequestMapping(value = "/{name_and_version:.*}/changes", method = RequestMethod.GET)
    public ResponseEntity getAllModificationsOnVersion(@PathVariable("name_and_version") String nameAndVersion) {
        ResponseEntity responseEntity = null;
        List<JsonSchemaDetail> jsonSchemaDetailsList = null;
        try {
            jsonSchemaDetailsList = jsonSchemaDAO.getAllModificationsOnVersion(nameAndVersion.split("-")[0], nameAndVersion.split("-")[1]);
            responseEntity = new ResponseEntity(jsonSchemaDetailsList, HttpStatus.OK);
        } catch (NoResultException nre) {
            responseEntity = new ResponseEntity(nre.getMessage(), HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }*/

}
