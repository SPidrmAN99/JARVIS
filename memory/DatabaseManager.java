package memory;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DatabaseManager {

    private static final String FILE = "memory/database.json";

    public void save(String data) {
        try {
            FileWriter writer = new FileWriter(FILE, true);
            writer.write(data + "\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readAll() {
        try {
            return new String(Files.readAllBytes(Paths.get(FILE)));
        } catch (Exception e) {
            return "No data found.";
        }
    }
}