import java.io.IOException;
import java.util.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
class Application {
    public static void main(String[] args) throws IOException {
        Map<String, Properties> properties = new HashMap<>();
        Map<String, Object> keys = new HashMap<>();
        Map<String, List<String>> forgotKeys = new HashMap<>();
        PropertiesComparator.loadProperties(keys, properties);
        PropertiesComparator.checkMissingKeys(keys, properties, forgotKeys);
        PropertiesComparator.printAllValues(keys, properties);
        PropertiesComparator.printMissingValues(forgotKeys, properties);
    }
}