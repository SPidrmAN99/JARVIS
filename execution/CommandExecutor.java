package execution;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class CommandExecutor {

    public String execute(String command) {

        if (command == null || command.trim().isEmpty()) {
            return "JARVIS: Invalid command.";
        }

        try {

            // Ensure Windows compatibility
            Process process = Runtime.getRuntime().exec(
                    new String[] { "cmd", "/c", command });

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));

            StringBuilder output = new StringBuilder();
            String line;

            // Read standard output
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Read error output
            while ((line = errorReader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                return "JARVIS: Command executed successfully.\n" +
                        output.toString().trim();
            } else {
                return "JARVIS: Command exited with code " + exitCode + "\n" +
                        output.toString().trim();
            }

        } catch (IOException e) {
            return "JARVIS: Execution failed - " + e.getMessage();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "JARVIS: Execution interrupted.";
        }
    }
}