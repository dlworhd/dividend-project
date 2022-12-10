package com.zerobase.bdg;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class BdgApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(BdgApplication.class, args);
	}
}
