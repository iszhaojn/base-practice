package com.simonvon.basepractice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "com.simonvon")
public class BasePracticeApplication {

	public static void main(String[] args) {
		SpringApplication.run(BasePracticeApplication.class, args);
	}

}
