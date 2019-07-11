package com.yukon.utils.propertiescomparator;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;
import java.util.logging.*;

public class PropertiesComparator {
    private Map<String, Properties> properties;
    private Map<String, Object> keys;
    private Map<String, List<String>> forgotKeys;
    //--------- Peter German ------------
    private static String str = null;
    private static Logger LOGGER = null;
    private static FileHandler FILEH = null;


    static{
        try{
            System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");
            FILEH = new FileHandler("./LogFile.log");
            LOGGER = Logger.getLogger(PropertiesComparator.class.getName());
            LOGGER.addHandler(FILEH);
            SimpleFormatter formt = new SimpleFormatter();
            FILEH.setFormatter(formt);
            LOGGER.setUseParentHandlers(false);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //-----------------------------------

    public PropertiesComparator(){
        properties = new LinkedHashMap<>();
        keys = new LinkedHashMap<>();
        forgotKeys = new HashMap<>();
    }


    public void Execute() throws IOException{
        loadProperties(keys, properties);
        checkMissingKeys(keys, properties, forgotKeys);
        printAllValues(keys, properties);
        printMissingValues(forgotKeys, properties);
    }

    private void loadProperties(Map<String, Object> keys, Map<String, Properties> properties) throws IOException {
        String pathToFiles = System.getProperty("user.dir") + "/testProp";
        try (Stream<Path> paths = Files.walk(Paths.get(pathToFiles))) {
            paths.filter(Files::isRegularFile).forEach(path -> {
                Properties prop = new PropertiesSorted();
                try {
                    prop.load(new FileInputStream(path.toFile()));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                properties.put(path.toString(), prop);
                runThroughKeys(keys, prop);
            });
        }
    }

    private void runThroughKeys(Map<String, Object> keys, Properties properties){
        properties.keySet().forEach(key -> {
            String string = key.toString().trim();
            if(!keys.containsKey(string)){
                keys.put(string, key);
            }
        });
    }

    private void printAllValues(Map<String, Object> keys, Map<String, Properties> properties){
        String newLine = System.getProperty("line.separator");
        System.out.format("%80s", "Key");
        //------ Peter German ------
        str = String.format("%80s", "Key");
        //---------------------
        List<String> languageKeys = new ArrayList<>(properties.keySet());
        for (String propertyKey : languageKeys) {
            System.out.format("%255s", propertyKey);
            //------ Peter German ------
            str += String.format("%255s", propertyKey);
            //---------------------
        }
        System.out.format("%s", newLine);
        //------ Peter German ------
        LOGGER.info(str);
        //---------------------
        keys.keySet().forEach(wordKey -> {
            System.out.format("%80s", wordKey);
            //------ Peter German ------
            str = String.format("%80s", wordKey);
            //---------------------
            for (String languageKey : languageKeys){
                Properties property = properties.get(languageKey);
                String propertyValue = property.getProperty(wordKey);
                System.out.format("%255s", propertyValue);
                //------ Peter German ------
                str += String.format("%255s", propertyValue);
                //---------------------
            }
            //------ Peter German ------
            LOGGER.info(str);
            //---------------------
            System.out.format("%s", newLine);
        });
    }

    private void checkMissingKeys(Map<String, Object> keys, Map<String, Properties> properties, Map<String, List<String>> forgotKeys){
        List<String> languageKeys = new ArrayList<>(properties.keySet());
        keys.keySet().forEach(wordKey -> {
            for (String languageKey : languageKeys){
                Properties property = properties.get(languageKey);
                String propertyValue = property.getProperty(wordKey);
                if (propertyValue == null) {
                    checkValueAbsence(forgotKeys, wordKey, languageKey);
                } else {
                    if (propertyValue.trim().isEmpty()){
                        checkValueAbsence(forgotKeys, wordKey, languageKey);
                    }
                    String[] split2 = languageKey.split("messages")[1].split(".properties");
                    String languageMarker;
                    if (split2.length > 0){
                        languageMarker = split2[0];
                    } else {
                        languageMarker = "_en";
                    }
                    if (propertyValue.trim().toLowerCase().contains(languageMarker)){
                        checkValueAbsence(forgotKeys, wordKey, languageKey);
                    }
                }
            }
        });
    }

    private void checkValueAbsence(Map<String, List<String>> forgotKeys, String wordKey, String languageKey){
        if(!forgotKeys.containsKey(languageKey)){
            forgotKeys.put(languageKey, new ArrayList<>());
        }
        forgotKeys.get(languageKey).add(wordKey);
    }

    private void printMissingValues(Map<String, List<String>> forgotKeys, Map<String, Properties> properties){
        String newLine = System.getProperty("line.separator");
        List<String> languageKeys = new ArrayList<>(properties.keySet());
        String englishPropertyKey = languageKeys.stream().filter(name -> !name.contains("messages_")).findFirst().get();
        Properties englishProperties = properties.get(englishPropertyKey);
        for (String languageFileKey : forgotKeys.keySet()){
            System.out.println("-----------------------------------------------------------------------------------");
            System.out.println(languageFileKey);
            System.out.println("-----------------------------------------------------------------------------------");
            //------ Peter German ------
            str = String.format("%s","-----------------------------------------------------------------------------------\n" +
                    languageFileKey+ "\n-----------------------------------------------------------------------------------");
            LOGGER.info(str);
            //---------------------
            for (String textKey : forgotKeys.get(languageFileKey)){
                if(properties.get(languageFileKey).getProperty(textKey) != null){
                    System.out.println(textKey + " = " + properties.get(languageFileKey).getProperty(textKey));
                    //------ Peter German ------
                    str = String.format("%s",textKey + " = " + properties.get(languageFileKey).getProperty(textKey));
                    LOGGER.info(str);
                    //---------------------
                }
            }
            System.out.println("-----------------------------------------------------------------------------------");
            //------ Peter German ------
            str = String.format("%s","-----------------------------------------------------------------------------------");
            LOGGER.info(str);
            //---------------------
            for (String textKey : forgotKeys.get(languageFileKey)){
                if(properties.get(languageFileKey).getProperty(textKey) == null){
                    System.out.println(textKey + " = " + englishProperties.getProperty(textKey));
                    //------ Peter German ------
                    str = String.format("%s",textKey + " = " + englishProperties.getProperty(textKey));
                    LOGGER.info(str);
                    //---------------------
                }
            }
            System.out.format("%s", newLine);
            //------ Peter German ------
            LOGGER.info("");
            //---------------------
        }
    }
}
