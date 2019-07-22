package com.yukon.utils.propertiescomparator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileNotFoundException;
import java.io.IOException;

@SpringBootApplication
public class PropertiesComparatorApplication {
	public static void main(String[] args) throws IOException, FileNotFoundException {
		SpringApplication.run(PropertiesComparatorApplication.class, args);
		PropertiesComparator comparator = new PropertiesComparator();
		comparator.Execute();
	}
}
