package com.chapo.aggregator;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AggregatorApplication {

	public static void main(String[] args) throws JsonProcessingException {
		SpringApplication.run(AggregatorApplication.class, args);
	}

}
