package execution;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SmartCommandExecutor {

    public String execute(String command) {

        try {

            Process process = Runtime.getRuntime().exec(
                    new String[] { "cmd", "/c", command });

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));

            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            while ((line = errorReader.readLine()) != null) {
                output.append(line).append("\n");
            }

            return output.toString().trim();

        } catch (Exception e) {
            return "Command failed: " + e.getMessage();
        }
    }
}