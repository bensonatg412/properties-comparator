package com.yukon.utils.propertiescomparator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PropertiesComparatorApplication {
	public static void main(String[] args) {
		SpringApplication.run(PropertiesComparatorApplication.class, args);
		PropertiesComparator comparator = new PropertiesComparator();
		comparator.Execute();
	}
}
