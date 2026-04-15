package core;

import execution.SystemInfoExecutor;
import models.IntentResult;
import models.IntentResult.IntentType;

import execution.SmartCommandExecutor;
import actions.SearchHandler;
import llm.LLMClient;
import memory.DatabaseManager;

public class Router {

    private IntentClassifier classifier;
    private MemoryManager memoryManager;
    private SmartCommandExecutor commandExecutor;
    private SearchHandler searchHandler;
    private LLMClient llmClient;
    private DatabaseManager db;
    private SystemInfoExecutor systemExecutor = new SystemInfoExecutor();

    public Router() {
        classifier = new IntentClassifier();
        memoryManager = new MemoryManager();
        commandExecutor = new SmartCommandExecutor();
        searchHandler = new SearchHandler();
        llmClient = new LLMClient();
        db = new DatabaseManager();
    }

    public String handle(String userInput) {

        if (userInput == null || userInput.trim().isEmpty()) {
            return "JARVIS: Invalid input.";
        }

        IntentResult intent = classifier.classify(userInput);

        if (intent.getConfidence() < 0.4) {
            intent.setType(IntentType.CHAT);
        }

        String response;

        switch (intent.getType()) {

            case COMMAND:
                return commandExecutor.execute(userInput);

            case SEARCH:
                response = searchHandler.search(intent.getPayload());
                break;

            case DB:
                response = handleDatabase(userInput);
                break;

            case CHAT:
            default:
                response = handleChat(userInput);
                break;
        }

        memoryManager.add(userInput, response);

        return response;
    }

    // =========================

    private String handleCommand(String cmd) {

        String result = commandExecutor.execute(cmd);

        if (result == null || result.isEmpty()) {
            return "JARVIS: Command executed.";
        }

        return result;
    }

    // =========================

    private String handleChat(String input) {

        String context = memoryManager.getFormattedContext();
        return llmClient.askWithContext(context, input);
    }

    // =========================

    private String handleDatabase(String input) {

        if (input.toLowerCase().contains("store")) {
            db.save(input);
            return "JARVIS: Data stored successfully.";
        }

        if (input.toLowerCase().contains("show") ||
                input.toLowerCase().contains("read")) {

            return db.readAll();
        }

        return "JARVIS: Specify store/read operation.";
    }
}