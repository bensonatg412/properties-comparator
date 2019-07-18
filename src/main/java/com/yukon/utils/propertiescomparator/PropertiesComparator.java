package com.yukon.utils.propertiescomparator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.*;

@Component
public class PropertiesComparator {
    private Map<String, Properties> properties = new LinkedHashMap<>();
    private Map<String, Object> keys = new LinkedHashMap<>();
    private Map<String, List<String>> forgotKeys = new HashMap<>();
    private StringBuilder strLog = new StringBuilder(); // variable for Logging
    private String newLine = System.getProperty("line.separator");

    public void Execute(){
        loadProperties(keys, properties);
        checkMissingKeys(keys, properties, forgotKeys);
        printAllValues(keys, properties);
        printUntranslatedValues(forgotKeys, properties);
        printNullValues(forgotKeys, properties);
        loadLog(); // for Logging
    }


    private static String pathToFiles;
    @Value("${path.toFiles}")
    public void setPathToFiles(String value){
        pathToFiles = value;
    }

    private static String[] fileSuffixMutate;
    @Value("${suffix.file}")
    public void setPathSuffix(String value){
        fileSuffixMutate = value.trim().split(",");
    }

    private static String nameFile;
    @Value("${name.file}")
    public void setNameFile(String value){
        nameFile = value;
    }

    // Loading files *.properties
    private Map<String, Object> loadProperties(Map<String, Object> keys, Map<String, Properties> properties){
        builderPath();
        for(String path: fileSuffixMutate){
            Properties prop = new PropertiesSorted();
            try{
                prop.load(new FileInputStream(path));
            }catch(IOException e){
                System.err.println("No such files !!!");
                e.printStackTrace();
            }
            properties.put(path, prop);
            runThroughKeys(keys, prop);
        }

        return keys;
    }

    // for adding all keys of properties files
    private void runThroughKeys(Map<String, Object> keys, Properties properties){
        properties.keySet().forEach(key -> {
            String string = key.toString().trim();
            if(!keys.containsKey(string)){
                keys.put(string, key);
            }
        });
    }

    // building path of files *.properties
    private static void builderPath(){
        for(int i = 0; i< fileSuffixMutate.length; i++){
            fileSuffixMutate[i] = pathToFiles+nameFile+fileSuffixMutate[i]+".properties";
        }
    }
    //---------------------------------------------------------------------------

    // Selection of untranslated and null values
    private Map<String, List<String>> checkMissingKeys(Map<String, Object> keys, Map<String, Properties> properties, Map<String, List<String>> forgotKeys){
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
                    } else { // этот блок не обязателен если файл с суфиксом "_en" уже есть
                        languageMarker = "_en";
                    }
                    if (propertyValue.trim().toLowerCase().contains(languageMarker)){
                        checkValueAbsence(forgotKeys, wordKey, languageKey);
                    }
                }
            }
        });

        return forgotKeys;
    }

    // for adding null and untranslated values
    private void checkValueAbsence(Map<String, List<String>> forgotKeys, String wordKey, String languageKey){
        if(!forgotKeys.containsKey(languageKey)){
            forgotKeys.put(languageKey, new ArrayList<>());
        }
        forgotKeys.get(languageKey).add(wordKey);
    }
    //-----------------------------------------------------------------------------------


    // For printing all keys = values
    private void printAllValues(Map<String, Object> keys, Map<String, Properties> properties){
        strLog.append(String.format("%s%s%80s", newLine, newLine, "Key")); // for Logging
        List<String> languageKeys = new ArrayList<>(properties.keySet());
        for (String propertyKey : languageKeys) {
            strLog.append(String.format("%255s", propertyKey)); // for Logging
        }
        strLog.append(String.format("%s", newLine)); // for Logging
        keys.keySet().forEach(wordKey -> {
            strLog.append(String.format("%80s", wordKey)); // for Logging
            for (String languageKey : languageKeys){
                Properties property = properties.get(languageKey);
                String propertyValue = property.getProperty(wordKey);
                strLog.append(String.format("%255s", propertyValue)); // for Logging
            }
            strLog.append(String.format("%s", newLine)); // for Logging
        });
    }

    // For printing all untranslated keys = values
    private void printUntranslatedValues(Map<String, List<String>> forgotKeys, Map<String, Properties> properties){
        String newLine = System.getProperty("line.separator");
        strLog.append(String.format("%s%80s", newLine,"THIS IS UNTRANSLATED VALUES"+newLine));

        for (String languageFileKey : forgotKeys.keySet()){
            strLog.append(String.format("%s","-----------------------------------------------------------------------------------"+newLine+  // for Logging
                    languageFileKey+newLine+"-----------------------------------------------------------------------------------"+newLine)); // for Logging
            for (String textKey : forgotKeys.get(languageFileKey)){
                if(properties.get(languageFileKey).getProperty(textKey) != null){
                    strLog.append(String.format("%s",textKey + " = " + properties.get(languageFileKey).getProperty(textKey)+newLine)); // for Logging
                }
            }
            strLog.append(String.format("%s","-----------------------------------------------------------------------------------"+newLine+newLine)); // for Logging
        }
        strLog.append(String.format("%80s", newLine+"==================================================================================="+newLine+newLine));
    }

    // For printing all keys = value that are null
    private void printNullValues(Map<String, List<String>> forgotKeys, Map<String, Properties> properties){
        String newLine = System.getProperty("line.separator");
        List<String> languageKeys = new ArrayList<>(properties.keySet());
        String englishPropertyKey = languageKeys.stream().filter(name -> name.contains("messages_en")).findFirst().get();
        Properties englishProperties = properties.get(englishPropertyKey);
        strLog.append(String.format("%80s", "THIS IS NULL VALUES"+newLine));
        for (String languageFileKey : forgotKeys.keySet()){
            strLog.append(String.format("%s","-----------------------------------------------------------------------------------"+newLine+  // for Logging
                    languageFileKey+newLine+"-----------------------------------------------------------------------------------"+newLine)); // for Logging
            for (String textKey : forgotKeys.get(languageFileKey)){
                if(properties.get(languageFileKey).getProperty(textKey) == null){
                    strLog.append(String.format("%s",textKey + " = " + englishProperties.getProperty(textKey)+newLine)); // for Logging
                }
            }
            strLog.append(String.format("%s","-----------------------------------------------------------------------------------"+newLine)); // for Logging
        }
        strLog.append(String.format("%80s", newLine+"==================================================================================="+newLine+newLine));
    }
    //---------------------------------------------------------------------------------

    // For Logging ------------
    private void loadLog(){

        Logger LOGGER = null;
        FileHandler FILEH = null;

        try{
            System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");
            String pathToLog = System.getProperty("user.dir") + "/LogFile.log";
            FILEH = new FileHandler(pathToLog);
            LOGGER = Logger.getLogger(PropertiesComparator.class.getName());
            LOGGER.addHandler(FILEH);
            SimpleFormatter format = new SimpleFormatter();
            FILEH.setFormatter(format);
            LOGGER.setUseParentHandlers(true);
        }catch(Exception e){
            e.printStackTrace();
        }

        LOGGER.info(strLog.toString());
    }
    //-----------------------------------

}
