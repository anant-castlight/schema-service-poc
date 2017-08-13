package com.castlight.dataversioningpoc.manualsemanticversions;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.List;

/**
 * Created by anantm on 8/9/17.
 */
@Service
public class JsonSchemaDetailsService {

    @Autowired
    private JsonSchemaDetailsRepository jsonSchemaDetailsRepository;

    @Transactional
    public String saveJsonSchemaDetails(String name, String description, JsonNode jsonSchema, ChangeType changeType) throws Exception {
        List<String> versionList = jsonSchemaDetailsRepository.findLatestVersionByName(name, new PageRequest(0,1));
        String latestVersionFromDb = null;
        if(versionList != null && versionList.size() != 0){
            latestVersionFromDb = versionList.get(0);
        }
        else{
            latestVersionFromDb = new SemanticVersion().toString();
        }
        description = generateDescriptionAutomatically(description, new SemanticVersion(latestVersionFromDb));
        if(!isDuplicateSchemaDetailsPresent(name, latestVersionFromDb, jsonSchema)) {
            String newVersion = generateSemanticVersion(changeType, latestVersionFromDb);
            JsonSchemaDetail JsonSchemaDetail = new JsonSchemaDetail(name, description, jsonSchema.toString(), newVersion);
            jsonSchemaDetailsRepository.save(JsonSchemaDetail);
            latestVersionFromDb = newVersion;
        }
        return latestVersionFromDb;
    }

    public JsonSchemaDetail getJsonSchemaDetails(String nameAndVersion) throws NoResultException {
        JsonSchemaDetail jsonSchemaDetail;
        if(isVersionPresent(nameAndVersion)) {
            String name = nameAndVersion.substring(0,nameAndVersion.lastIndexOf("-"));
            String version = nameAndVersion.substring(nameAndVersion.lastIndexOf("-")+1);
            jsonSchemaDetail = jsonSchemaDetailsRepository.findJsonSchemaDetailsByNameAndVersion(name, version);
        }
        else{
            String name =nameAndVersion;
            List<JsonSchemaDetail> jsonSchemaDetailList = jsonSchemaDetailsRepository.findLatestSchemaDetailsByName(name, new PageRequest(0,1));
            if(jsonSchemaDetailList != null && jsonSchemaDetailList.size() != 0) {
                jsonSchemaDetail = jsonSchemaDetailList.get(0);
            }
            else {
                throw new NoResultException("Requested schema not found");
            }
        }
        return jsonSchemaDetail;
    }

    public JsonSchemaAllVersionDetail getAllVersionsOfJsonSchema(String name) throws NoResultException{
        List<JsonSchemaDetail> jsonSchemaDetails = jsonSchemaDetailsRepository.findAllJsonSchemaDetailsByName(name);
        if(jsonSchemaDetails == null || jsonSchemaDetails.size() == 0) {
            throw new NoResultException();
        }
        return new JsonSchemaAllVersionDetail(jsonSchemaDetails);
    }

    private boolean isDuplicateSchemaDetailsPresent(String name, String version, JsonNode jsonNode) throws IOException, ProcessingException {
        boolean isDuplicateSchema;

        JsonSchemaDetail jsonSchemaDetail = jsonSchemaDetailsRepository.findJsonSchemaDetailsByNameAndVersion(name, version);
        if(jsonSchemaDetail != null){
            isDuplicateSchema = !JsonUtil.isJsonSchemaChanged(jsonSchemaDetail.getJsonSchema(), jsonNode);
        } else {
            isDuplicateSchema = false;
        }
        return isDuplicateSchema;
    }

    private String generateSemanticVersion(ChangeType changeType, String latestVersionFromDb) throws Exception {

        SemanticVersion semanticVersion = new SemanticVersion(latestVersionFromDb);
        if(ChangeType.PATCH == changeType && !isNewVersion(semanticVersion)) {
            semanticVersion.setPatchVersion(semanticVersion.getPatchVersion()+1);
        }
        else if(ChangeType.MINOR == changeType && !isNewVersion(semanticVersion)) {
            semanticVersion.setMinorVersion(semanticVersion.getMinorVersion()+1);
            semanticVersion.setPatchVersion(0);
        }
        else if(isNewVersion(semanticVersion)
                || ChangeType.MAJOR == changeType) {
            semanticVersion.setMajorVersion(semanticVersion.getMajorVersion()+1);
            semanticVersion.setMinorVersion(0);
            semanticVersion.setPatchVersion(0);
        }
        else if(!isNewVersion(semanticVersion)) {
            throw new Exception("changeType as Major,Minor or Patch is required to update version");
        }
        return semanticVersion.toString();
    }

    private String generateDescriptionAutomatically(String description, SemanticVersion semanticVersion) {

        if(description == null) {
            if(semanticVersion.getMajorVersion() == 0 && semanticVersion.getMinorVersion() == 0
                    && semanticVersion.getPatchVersion() == 0) {
                description = "Schema Created";
            }
            else{
                description = "Schema Updated";
            }
        }

        return description;

    }

    private boolean isVersionPresent(String nameAndVersion) {

        try {
            new SemanticVersion(nameAndVersion.substring(nameAndVersion.lastIndexOf("-")));
        }
        catch (Exception e){
            return false;
        }
        return true;
    }

    private boolean isNewVersion(SemanticVersion semanticVersion) {
        return semanticVersion.getPatchVersion() == 0 && semanticVersion.getMinorVersion() == 0
                && semanticVersion.getMajorVersion() == 0;
    }

}
