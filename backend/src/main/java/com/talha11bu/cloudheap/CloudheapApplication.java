package com.talha11bu.cloudheap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CloudheapApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudheapApplication.class, args);
	}

}
