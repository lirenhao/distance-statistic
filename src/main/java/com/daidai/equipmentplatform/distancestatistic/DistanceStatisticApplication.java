package com.daidai.equipmentplatform.distancestatistic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DistanceStatisticApplication {

	public static void main(String[] args) {
		SpringApplication.run(DistanceStatisticApplication.class, args);
	}

}
