package com.zerobase.bdg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@EnableScheduling
@EnableCaching
@SpringBootApplication
public class BdgApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(BdgApplication.class, args);
	}
}
