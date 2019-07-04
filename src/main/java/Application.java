import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Application {
    public static void main(String[] args) throws IOException {
        Map<String, Properties> properties = new HashMap<>();
        Map<String, Object> keys = new HashMap<>();
        Map<String, List<String>> forgotKeys = new HashMap<>();
        LoadProperties(keys, properties);
        CheckMissing(keys, properties, forgotKeys);
        PrintValues(keys, properties);
        PrintMissing(forgotKeys, properties);

    }

    public static void LoadProperties(Map<String, Object> keys, Map<String, Properties> properties) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get("E:/testProp"))) {
            paths.filter(Files::isRegularFile).forEach(path -> {
                Properties prop = new Properties();
                try {
                    prop.load(new FileInputStream(path.toFile()));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                properties.put(path.toString(), prop);
                BustKeys(keys, prop);
            });
        }
    }

    private static void BustKeys(Map<String, Object> keys, Properties properties){
        properties.keySet().forEach(key -> {
            String string = key.toString().trim();
            if(!keys.containsKey(string)){
                keys.put(string, key);
            }
        });
    }

    public static void PrintValues(Map<String, Object> keys, Map<String, Properties> properties){
        String newLine = System.getProperty("line.separator");
        System.out.format("%80s", "Key");
        List<String> languageKeys = new ArrayList<>(properties.keySet());
        for (String propertyKey : languageKeys) {
            System.out.format("%255s", propertyKey);
        }
        System.out.format("%s", newLine);
        keys.keySet().forEach(wordKey -> {
            System.out.format("%80s", wordKey);
            for (String languageKey : languageKeys){
                Properties property = properties.get(languageKey);
                String propertyValue = property.getProperty(wordKey);
                System.out.format("%255s", propertyValue);
            }
            System.out.format("%s", newLine);
        });
    }

    public static void CheckMissing(Map<String, Object> keys, Map<String, Properties> properties, Map<String, List<String>> forgotKeys){
        List<String> languageKeys = new ArrayList<>(properties.keySet());
        keys.keySet().forEach(wordKey -> {
            for (String languageKey : languageKeys){
                Properties property = properties.get(languageKey);
                String propertyValue = property.getProperty(wordKey);
                if (propertyValue == null) {
                    CheckValue(forgotKeys, wordKey, languageKey);
                } else {
                    if (propertyValue.trim().isEmpty()){
                        CheckValue(forgotKeys, wordKey, languageKey);
                    }
                    String[] split2 = languageKey.split("messages")[1].split(".properties");
                    String languageMarker;
                    if (split2.length > 0){
                        languageMarker = split2[0];
                    } else {
                        languageMarker = "_en";
                    }
                    if (propertyValue.trim().toLowerCase().contains(languageMarker)){
                        CheckValue(forgotKeys, wordKey, languageKey);
                    }
                }
            }
        });
    }

    private static void CheckValue(Map<String, List<String>> forgotKeys, String wordKey, String languageKey){
        if(!forgotKeys.containsKey(languageKey)){
            forgotKeys.put(languageKey, new ArrayList<>());
        }
        forgotKeys.get(languageKey).add(wordKey);
    }

    public static void PrintMissing(Map<String, List<String>> forgotKeys, Map<String, Properties> properties){
        String newLine = System.getProperty("line.separator");
        List<String> languageKeys = new ArrayList<>(properties.keySet());
        String englishPropertyKey = languageKeys.stream().filter(name -> !name.contains("messages_")).findFirst().get();
        Properties englishProperties = properties.get(englishPropertyKey);
        for (String languageFileKey : forgotKeys.keySet()){
            System.out.println("-----------------------------------------------------------------------------------");
            System.out.println(languageFileKey);
            System.out.println("-----------------------------------------------------------------------------------");
            for (String textKey : forgotKeys.get(languageFileKey)){
                if(properties.get(languageFileKey).getProperty(textKey) != null){
                    System.out.println(textKey + " = " + properties.get(languageFileKey).getProperty(textKey));
                }
            }
            System.out.println("-----------------------------------------------------------------------------------");
            for (String textKey : forgotKeys.get(languageFileKey)){
                if(properties.get(languageFileKey).getProperty(textKey) == null){
                    System.out.println(textKey + " = " + englishProperties.getProperty(textKey));
                }
            }
            System.out.format("%s", newLine);
        }
    }
}