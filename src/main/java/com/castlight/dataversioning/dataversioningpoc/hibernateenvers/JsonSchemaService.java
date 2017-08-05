package com.castlight.dataversioning.dataversioningpoc.hibernateenvers;

import com.castlight.dataversioning.dataversioningpoc.hibernateenvers.JsonSchemaDAO;
import com.castlight.dataversioning.dataversioningpoc.hibernateenvers.JsonSchemaDetails;
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
@RequestMapping("/json_schema")
public class JsonSchemaService {

    private JsonSchemaDAO jsonSchemaDAO = new JsonSchemaDAO();
    private ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity saveJSONSchema(@RequestBody String requestJson) {
        ResponseEntity responseEntity = null;
        try {
            JsonNode node = mapper.readTree(requestJson);
            String name = mapper.convertValue(node.get("name"), String.class);
            JsonNode jsonSchema = mapper.convertValue(node.get("jsonSchema"), JsonNode.class);
            String description = mapper.convertValue(node.get("description"), String.class);
            if(!isDuplicateSchemaDetailsPresent(name, jsonSchema)) {
                jsonSchemaDAO.create(name, jsonSchema.toString(), description);
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

    @RequestMapping(value = "/{name}", method = RequestMethod.PUT)
    public ResponseEntity updateJSONSchema(@PathVariable("name") String name,
                                           @RequestBody String requestJson) {

        ResponseEntity responseEntity = null;
        try {
            JsonNode node = mapper.readTree(requestJson);
            JsonNode jsonSchema = mapper.convertValue(node.get("jsonSchema"), JsonNode.class);
            String description = mapper.convertValue(node.get("description"), String.class);
            jsonSchemaDAO.update(name, jsonSchema.toString(), description);
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

    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
    public ResponseEntity getJSONSchema(@PathVariable("name") String name) {

        JsonSchemaDetails jsonSchemaDetails = null;
        ResponseEntity responseEntity = null;
        try {
            jsonSchemaDetails = jsonSchemaDAO.get(name);
            responseEntity = new ResponseEntity(jsonSchemaDetails, HttpStatus.FOUND);
        } catch (NoResultException nre) {
            responseEntity = new ResponseEntity(nre.getMessage(), HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    @RequestMapping(value = "/{name}/version", method = RequestMethod.GET)
    public ResponseEntity getAllVersions(@PathVariable("name") String name) {
        ResponseEntity responseEntity = null;
        List<JsonSchemaDetails> jsonSchemaDetailsList = null;
        try {
            jsonSchemaDetailsList = jsonSchemaDAO.getAllVersions(name);
            responseEntity = new ResponseEntity(jsonSchemaDetailsList, HttpStatus.OK);
        } catch (NoResultException nre) {
            responseEntity = new ResponseEntity(nre.getMessage(), HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    @RequestMapping(value = "/{name}/version/{version}", method = RequestMethod.GET)
    public ResponseEntity getJsonByVersion(@PathVariable("name") String name,
                                           @PathVariable("version") Integer version) {
        ResponseEntity responseEntity = null;
        JsonSchemaDetails jsonSchemaDetails = null;
        try {
            jsonSchemaDetails = jsonSchemaDAO.getJsonByVersion(name, version);
            responseEntity = new ResponseEntity(jsonSchemaDetails, HttpStatus.OK);
        } catch (NoResultException nre) {
            responseEntity = new ResponseEntity(nre.getMessage(), HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }


    private boolean isDuplicateSchemaDetailsPresent(String name, JsonNode jsonNode) throws IOException, ProcessingException {
        boolean isDuplicateSchema;
        try {
            JsonSchemaDetails jsonSchemaDetails = jsonSchemaDAO.get(name);
            isDuplicateSchema = !JsonUtil.isJsonSchemaChanged(jsonSchemaDetails.getJsonSchema(), jsonNode.toString());
        } catch (NoResultException nre) {
            isDuplicateSchema = false;
        }
        return isDuplicateSchema;
    }

}
