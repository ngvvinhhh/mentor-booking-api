package com.swd392.mentorbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MentorbookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(MentorbookingApplication.class, args);
	}

}
