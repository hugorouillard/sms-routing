import java.util.Scanner;

public class UserClient {
    private User user;
    private String username;
    private String currentAntenna;
    private Scanner scanner = new Scanner(System.in);

    public UserClient(String username, String initialAntenna) {
        this.username = username;
        this.currentAntenna = initialAntenna;
    }

    public void start() {
        System.out.println("### User Client: " + username + " ###");

        try {
            user = new User(username, currentAntenna);
            System.out.println("Connected to antenna " + currentAntenna);

            // Interactive loop
            boolean running = true;
            while (running) {
                printMenu();
                int choice = readIntInput();

                try {
                    switch (choice) {
                        case 1:
                            sendMessage();
                            break;
                        case 2:
                            moveToAntenna();
                            break;
                        case 0:
                            running = false;
                            break;
                        default:
                            System.out.println("Invalid option. Please try again.");
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.println("Disconnecting user " + username);

        } catch (Exception e) {
            System.out.println("Error initializing user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void printMenu() {
        System.out.println("\n" + username + "@" + currentAntenna + " - Available actions:");
        System.out.println("1. Send message");
        System.out.println("2. Move to another antenna");
        System.out.println("0. Exit");
        System.out.print("Enter choice: ");
    }

    private void sendMessage() throws Exception {
        System.out.print("Recipient username: ");
        String recipient = scanner.nextLine().trim();

        System.out.print("Message content: ");
        String content = scanner.nextLine();

        System.out.println("Sending message to " + recipient + "...");
        user.sendSMS(recipient, content);
    }

    private void moveToAntenna() throws Exception {
        System.out.print("Move to antenna (A-I): ");
        String antenna = scanner.nextLine().trim().toUpperCase();

        System.out.println("Moving to antenna " + antenna + "...");
        user.moveTo(antenna);
        currentAntenna = antenna; // Update local tracking of antenna
    }

    private int readIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java UserClient <username> <initial-antenna>");
            return;
        }

        new UserClient(args[0], args[1]).start();
    }
}
