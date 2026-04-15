package llm;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class LLMClient {

    private static final String API_URL = "http://localhost:11434/api/chat";
    private static final String MODEL = "devstral-small-2:24b-cloud";

    // =========================
    // Public Methods
    // =========================

    public String ask(String prompt) {
        try {
            String payload = buildPayload(prompt);
            String response = sendRequest(payload);
            return extractContent(response);

        } catch (Exception e) {
            return "JARVIS: LLM error - " + e.getMessage();
        }
    }

    public String askWithContext(String context, String query) {

        String fullPrompt = "You are JARVIS, an intelligent assistant.\n" +
                "Maintain continuity using conversation history.\n\n" +
                context + "\n" +
                "User: " + query + "\n" +
                "Assistant:";

        return ask(fullPrompt);
    }

    // =========================
    // Internal Helpers
    // =========================

    private String buildPayload(String prompt) {
        return "{"
                + "\"model\":\"devstral-small-2:24b-cloud\","
                + "\"stream\": false,"
                + "\"messages\":[{\"role\":\"user\",\"content\":\"" + escape(prompt) + "\"}]"
                + "}";
    }

    private String extractChunk(String jsonLine) {
        try {
            String key = "\"content\":\"";
            int start = jsonLine.indexOf(key);

            if (start == -1)
                return null;

            start += key.length();

            int end = jsonLine.indexOf("\"", start);

            if (end == -1)
                return null;

            return jsonLine.substring(start, end);

        } catch (Exception e) {
            return null;
        }
    }

    private String sendRequest(String payload) throws Exception {

        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.getBytes());
            os.flush();
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));

        StringBuilder fullResponse = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {

            // Extract content from each chunk
            String content = extractChunk(line);

            if (content != null) {
                fullResponse.append(content);
            }
        }

        return fullResponse.toString();
    }

    private String extractContent(String response) {
        try {
            String key = "\"content\":\"";
            int start = response.indexOf(key);

            if (start == -1)
                return response;

            start += key.length();

            StringBuilder result = new StringBuilder();

            boolean escape = false;

            for (int i = start; i < response.length(); i++) {
                char c = response.charAt(i);

                if (escape) {
                    result.append(c);
                    escape = false;
                    continue;
                }

                if (c == '\\') {
                    escape = true;
                    continue;
                }

                if (c == '"') {
                    break;
                }

                result.append(c);
            }

            return result.toString();

        } catch (Exception e) {
            return response;
        }
    }

    private String escape(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }
}