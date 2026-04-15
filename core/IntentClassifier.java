package core;

import models.IntentResult;
import models.IntentResult.IntentType;

import java.util.*;

public class IntentClassifier {

    // --- Keyword Sets ---
    private static final Set<String> COMMAND_VERBS = new HashSet<>(Arrays.asList(
            "open", "start", "run", "launch", "play", "execute"));

    private static final Set<String> SEARCH_KEYWORDS = new HashSet<>(Arrays.asList(
            "search", "find", "lookup", "google"));

    private static final Set<String> DB_KEYWORDS = new HashSet<>(Arrays.asList(
            "store", "save", "remember", "recall", "history", "database"));

    private static final Set<String> WH_WORDS = new HashSet<>(Arrays.asList(
            "who", "what", "when", "where", "why", "how"));

    public IntentResult classify(String input) {

        if (input == null || input.trim().isEmpty()) {
            return new IntentResult(IntentType.CHAT, 0.0, "");
        }

        String cleaned = preprocess(input);
        String[] tokens = cleaned.split("\\s+");

        double commandScore = 0;
        double searchScore = 0;
        double dbScore = 0;
        double chatScore = 0;

        // --- Word Scoring ---
        for (int i = 0; i < tokens.length; i++) {
            String word = tokens[i];

            if (COMMAND_VERBS.contains(word)) {
                commandScore += (i == 0) ? 3 : 1.5;
            }

            if (SEARCH_KEYWORDS.contains(word)) {
                searchScore += 2;
            }

            if (DB_KEYWORDS.contains(word)) {
                dbScore += 2.5;
            }

            if (WH_WORDS.contains(word)) {
                chatScore += 3;
            }
        }

        // --- Pattern Boosts ---
        if (tokens.length > 0 && COMMAND_VERBS.contains(tokens[0])) {
            commandScore += 2; // imperative boost
        }

        if (tokens.length > 0 && WH_WORDS.contains(tokens[0])) {
            chatScore += 2; // question boost
        }

        // --- Fallback Heuristic ---
        if (commandScore == 0 && searchScore == 0 && dbScore == 0 && chatScore == 0) {
            chatScore = 1; // default bias toward CHAT
        }

        // --- Select Best Intent ---
        Map<IntentType, Double> scores = new HashMap<>();
        scores.put(IntentType.COMMAND, commandScore);
        scores.put(IntentType.SEARCH, searchScore);
        scores.put(IntentType.DB, dbScore);
        scores.put(IntentType.CHAT, chatScore);

        IntentType bestIntent = IntentType.CHAT;
        double maxScore = -1;

        for (Map.Entry<IntentType, Double> entry : scores.entrySet()) {
            if (entry.getValue() > maxScore) {
                maxScore = entry.getValue();
                bestIntent = entry.getKey();
            }
        }

        // --- Confidence Calculation ---
        double total = commandScore + searchScore + dbScore + chatScore;
        double confidence = (total == 0) ? 0.5 : (maxScore / total);

        // --- Payload Extraction ---
        String payload = extractPayload(bestIntent, cleaned);

        return new IntentResult(bestIntent, confidence, payload);
    }

    // =========================
    // Helpers
    // =========================

    private String preprocess(String input) {
        return input.toLowerCase()
                .replaceAll("[^a-z0-9 ]", "")
                .trim();
    }

    private String extractPayload(IntentType type, String input) {

        switch (type) {

            case COMMAND:
                return input.replaceFirst("^(open|start|run|launch|play|execute)\\s+", "");

            case SEARCH:
                return input.replaceFirst("^(search|find|lookup|google)\\s+", "");

            case DB:
                return input;

            case CHAT:
            default:
                return input;
        }
    }
}