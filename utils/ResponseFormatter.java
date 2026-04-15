package utils;

public class ResponseFormatter {

    public static String format(String input) {

        if (input == null)
            return "";

        String output = input;

        output = output.replace("**", "");
        output = output.replace("*", "");

        output = output.replace("\\n", "\n");

        output = output.replaceAll("\\(Note:.*?\\)", "");

        output = output.replaceAll("\n{3,}", "\n\n");

        output = output.replace("\\", "");

        return output.trim();
    }
}