package core;

import java.io.*;
import java.util.*;

public class MemoryManager {

    private static final int MAX_MEMORY = 6;
    private static final String FILE_PATH = "memory/memory.json";
    private Deque<MemoryEntry> buffer;

    public MemoryManager() {
        buffer = new ArrayDeque<>();
        loadRecentFromFile();
    }

    // =========================
    // Memory Entry
    // =========================
    public static class MemoryEntry {
        String user;
        String assistant;

        public MemoryEntry(String user, String assistant) {
            this.user = user;
            this.assistant = assistant;
        }
    }

    // =========================
    // Add Memory
    // =========================
    public void add(String userInput, String assistantOutput) {

        if (userInput == null || assistantOutput == null)
            return;

        MemoryEntry entry = new MemoryEntry(userInput, assistantOutput);

        buffer.addLast(entry);

        if (buffer.size() > MAX_MEMORY) {
            buffer.removeFirst();
        }

        appendToFile(entry);
    }

    // =========================
    // Context Builder
    // =========================
    public String getFormattedContext() {

        StringBuilder sb = new StringBuilder();
        sb.append("Conversation History:\n");

        for (MemoryEntry entry : buffer) {
            sb.append("User: ").append(entry.user).append("\n");
            sb.append("Assistant: ").append(entry.assistant).append("\n\n");
        }

        return sb.toString();
    }

    // =========================
    // File Persistence
    // =========================
    private void appendToFile(MemoryEntry entry) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {

            String jsonLine = String.format(
                    "{\"user\":\"%s\",\"assistant\":\"%s\"}",
                    escape(entry.user),
                    escape(entry.assistant));

            bw.write(jsonLine);
            bw.newLine();

        } catch (IOException e) {
            System.out.println("JARVIS: Memory write error - " + e.getMessage());
        }
    }

    // =========================
    // Load Last N Entries
    // =========================
    private void loadRecentFromFile() {

        File file = new File(FILE_PATH);
        if (!file.exists())
            return;

        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

        } catch (IOException e) {
            System.out.println("JARVIS: Memory load error - " + e.getMessage());
            return;
        }

        int start = Math.max(0, lines.size() - MAX_MEMORY);

        for (int i = start; i < lines.size(); i++) {
            MemoryEntry entry = parseLine(lines.get(i));
            if (entry != null) {
                buffer.addLast(entry);
            }
        }
    }

    // =========================
    // JSON Parsing (Simple)
    // =========================
    private MemoryEntry parseLine(String json) {

        try {
            String user = extract(json, "\"user\":\"", "\"");
            String assistant = extract(json, "\"assistant\":\"", "\"");

            return new MemoryEntry(user, assistant);

        } catch (Exception e) {
            return null;
        }
    }

    private String extract(String text, String start, String end) {
        int s = text.indexOf(start) + start.length();
        int e = text.indexOf(end, s);
        return text.substring(s, e);
    }

    private String escape(String text) {
        return text.replace("\"", "\\\"");
    }
}