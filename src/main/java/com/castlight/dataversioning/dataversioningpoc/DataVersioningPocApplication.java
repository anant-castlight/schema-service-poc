package com.castlight.dataversioning.dataversioningpoc;

import com.castlight.dataversioning.dataversioningpoc.hibernateenvers.JsonSchemaService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class DataVersioningPocApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataVersioningPocApplication.class, args);
		try {
			new DataVersioningPocApplication().jsonVersioningHibernateEnver();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void jsonVersioningHibernateEnver() throws IOException{
		JsonSchemaService jsonSchemaService = new JsonSchemaService();
		/*jsonSchemaService.saveJSONSchema("Product", "{\n" +
				"    \"title\": \"Product\",\n" +
				"    \"description\": \"A product from Acme's catalog\",\n" +
				"    \"type\": \"object\",\n" +
				"    \"properties\": {\n" +
				"        \"id\": {\n" +
				"            \"description\": \"The unique identifier for a product\",\n" +
				"            \"type\": \"integer\"\n" +
				"        }\n" +
				"    },\n" +
				"    \"required\": [\"id\"]\n" +
				"}");*/
		/*jsonSchemaService.updateJSONSchema(2L, "{\n" +
				"    \"title\": \"Geo v4\",\n" +
				"    \"description\": \"A geographical coordinate\",\n" +
				"    \"type\": \"object\",\n" +
				"    \"properties\": {\n" +
				"        \"longitude\": { \"type\": \"number\" },\n" +
				"        \"latitude\": { \"type\": \"number\" }\n" +
				"    }\n" +
				"}");*/
		//System.out.println(jsonSchemaService.getJSONSchema(2L));
		//System.out.println(jsonSchemaService.getAllVersions(2L));
		//System.out.println(jsonSchemaService.getJsonByVersion(2L, 3));
	}

}
