package models;

public class IntentResult {

    public enum IntentType {
        CHAT,
        COMMAND,
        SEARCH,
        DB
    }

    private IntentType type;
    private double confidence;
    private String payload;

    public IntentResult(IntentType type, double confidence, String payload) {
        this.type = type;
        this.confidence = confidence;
        this.payload = payload;
    }

    public IntentType getType() {
        return type;
    }

    public void setType(IntentType type) {
        this.type = type;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "IntentResult{" +
                "type=" + type +
                ", confidence=" + confidence +
                ", payload='" + payload + '\'' +
                '}';
    }
}