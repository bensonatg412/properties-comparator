package com.yukon.utils.propertiescomparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@SpringBootApplication
public class PropertiesComparatorApplication {
	public static void main(String[] args) throws IOException {
		SpringApplication.run(PropertiesComparatorApplication.class, args);
		PropertiesComparator comparator = new PropertiesComparator();
		comparator.Execute();
	}
}
