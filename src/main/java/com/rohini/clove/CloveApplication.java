package com.rohini.clove;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class CloveApplication {

	public static void main(String[] args) {

		SpringApplication.run(CloveApplication.class, args);
	}

}
