import java.util.Scanner;

public class LibrarySystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Library library = new Library();

        // Librarian login
        String id = getSafeString(sc, "Enter Librarian ID: ");
        String name = getSafeString(sc, "Enter Librarian Name: ");

        if (!id.equals("lib001") || !name.equalsIgnoreCase("admin")) {
            System.out.println("Invalid login!");
            return;
        }
        System.out.println("Welcome to Library Management System!");

        while (true) {
            System.out.println("\nMenu:\n1.Users 2.Books 3.Search 4.View Logs 5.Logout");
            int choice = getSafeInt(sc, "Choice: ");

            switch (choice) {
                case 1:
                    System.out.println("1.Add User 2.Delete User 3.Show Users");
                    int uChoice = getSafeInt(sc, "Choice: ");

                    switch (uChoice) {
                        case 1:
                            library.addUser();
                            break;
                        case 2:
                            library.deleteUser();
                            break;
                        case 3:
                            int n = getSafeInt(sc, "Enter number of users to display: ");
                            String mode = getSafeString(sc, "Enter mode (important / all / fields separated by comma): ");
                            if (mode.isEmpty()) mode = "important";
                            library.showUsers(n, mode);
                            break;
                        default:
                            System.out.println("Invalid choice");
                    }
                    break;

                case 2:
                    System.out.println("1.Add Book 2.Remove Book 3.Issue Book 4.Return Book 5.Show Books");
                    int bChoice = getSafeInt(sc, "Choice: ");
                    switch (bChoice) {
                        case 1: library.addBook(); break;
                        case 2: library.removeBook(); break;
                        case 3: library.issueBook(); break;
                        case 4: library.returnBook(); break;
                        case 5: library.showBooks(); break;
                        default: System.out.println("Invalid choice");
                    }
                    break;

                case 3:
                    System.out.println("1.Search User 2.Search Book");
                    int sChoice = getSafeInt(sc, "Choice: ");
                    switch (sChoice) {
                        case 1: library.searchUser(); break;
                        case 2: library.searchBook(); break;
                        default: System.out.println("Invalid choice");
                    }
                    break;

                case 4:
                    library.showLogs();
                    break;

                case 5:
                    System.out.println("Logging out...");
                    library.addLog("Librarian logged out");
                    return;

                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    // Safe integer input
    private static int getSafeInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Try again.");
                continue;
            }
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a valid integer.");
            }
        }
    }

    // Safe string input
    private static String getSafeString(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("Input cannot be empty. Try again.");
        }
    }
}
