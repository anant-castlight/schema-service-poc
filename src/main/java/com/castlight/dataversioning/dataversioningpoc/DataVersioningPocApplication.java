package com.castlight.dataversioning.dataversioningpoc;

import com.castlight.dataversioning.dataversioningpoc.hibernateenvers.JsonSchemaService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class DataVersioningPocApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataVersioningPocApplication.class, args);
	}

}
