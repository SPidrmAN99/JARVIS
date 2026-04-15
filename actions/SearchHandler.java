package actions;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class SearchHandler {

    public String search(String query) {

        if (query == null || query.trim().isEmpty()) {
            return "JARVIS: Invalid search query.";
        }

        try {
            // Encode query safely
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());

            String url = "https://www.google.com/search?q=" + encodedQuery;

            // Windows command
            String command = "start chrome \"" + url + "\"";

            Runtime.getRuntime().exec(new String[] { "cmd", "/c", command });

            return "JARVIS: Searching " + query;

        } catch (IOException e) {
            return "JARVIS: Search failed - " + e.getMessage();
        }
    }
}