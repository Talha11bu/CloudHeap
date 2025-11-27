package com.talha11bu.silkroad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SilkroadApplication {

	public static void main(String[] args) {
		SpringApplication.run(SilkroadApplication.class, args);
	}

}
