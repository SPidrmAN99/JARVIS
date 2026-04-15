import core.Router;

import java.util.Scanner;

public class Mainv1 {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Router router = new Router();

        System.out.println("JARVIS: System initialized. Awaiting commands...");

        while (true) {

            System.out.print("J.A.R.V.I.S. Input: ");
            if (!scanner.hasNextLine())
                break;
            String input = scanner.nextLine();

            if (input == null || input.trim().isEmpty()) {
                continue;
            }

            if (input.equalsIgnoreCase("/bye") ||
                    input.equalsIgnoreCase("exit")) {

                System.out.println("JARVIS: Shutting down...");
                break;
            }

            try {
                String response = router.handle(input);
                System.out.println(response);

            } catch (Exception e) {
                System.out.println("JARVIS: System error - " + e.getMessage());
            }
        }

        scanner.close();
    }
}