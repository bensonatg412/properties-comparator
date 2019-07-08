package com.yukon.utils.propertiescomparator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.*;

@SpringBootApplication
public class PropertiesComparatorApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(PropertiesComparatorApplication.class, args);
		Map<String, Properties> properties = new LinkedHashMap<>();
		Map<String, Object> keys = new LinkedHashMap<>();
		Map<String, List<String>> forgotKeys = new HashMap<>();
		PropertiesComparator.loadProperties(keys, properties);
		PropertiesComparator.checkMissingKeys(keys, properties, forgotKeys);
		PropertiesComparator.printAllValues(keys, properties);
		PropertiesComparator.printMissingValues(forgotKeys, properties);
	}
}
