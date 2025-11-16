import java.util.Scanner;

public class LibrarySystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Library library = new Library();

        // Librarian login
        System.out.print("Enter Librarian ID: ");
        String id = sc.nextLine();
        System.out.print("Enter Librarian Name: ");
        String name = sc.nextLine();

        if (!id.equals("lib001") || !name.equalsIgnoreCase("admin")) {
            System.out.println("Invalid login!");
            return;
        }
        System.out.println("Welcome to Library Management System!");

        while (true) {
            System.out.println("\nMenu:\n1.Users 2.Books 3.Search 4.Logout");
            System.out.print("Choice: ");
            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {

                // -------------------- USERS --------------------
                case 1:
                    System.out.println("1.Add User 2.Delete User 3.Show Users");
                    System.out.print("Choice: ");
                    int uChoice = Integer.parseInt(sc.nextLine());

                    switch (uChoice) {
                        case 1:
                            library.addUser();
                            break;

                        case 2:
                            library.deleteUser();
                            break;

                        case 3:
                            // Ask how many users to show
                            System.out.print("Enter number of users to display: ");
                            int n = Integer.parseInt(sc.nextLine());

                            // Ask mode
                            System.out.print("Enter mode (important / all / fields separated by comma): ");
                            String mode = sc.nextLine().trim();

                            library.showUsers(n, mode); // call the new method
                            break;

                        default:
                            System.out.println("Invalid choice");
                    }
                    break;

                // -------------------- BOOKS --------------------
                case 2:
                    System.out.println("1.Add Book 2.Remove Book 3.Issue Book 4.Return Book 5.Show Books");
                    System.out.print("Choice: ");
                    int bChoice = Integer.parseInt(sc.nextLine());
                    switch (bChoice) {
                        case 1:
                            library.addBook();
                            break;
                        case 2:
                            library.removeBook();
                            break;
                        case 3:
                            library.issueBook();
                            break;
                        case 4:
                            library.returnBook();
                            break;
                        case 5:
                            library.showBooks();
                            break;
                        default:
                            System.out.println("Invalid choice");
                    }
                    break;

                // -------------------- SEARCH --------------------
                case 3:
                    System.out.println("1.Search User 2.Search Book");
                    System.out.print("Choice: ");
                    int sChoice = Integer.parseInt(sc.nextLine());
                    switch (sChoice) {
                        case 1:
                            library.searchUser();
                            break;
                        case 2:
                            library.searchBook();
                            break;
                        default:
                            System.out.println("Invalid choice");
                    }
                    break;

                // -------------------- LOGOUT --------------------
                case 4:
                    System.out.println("Logging out...");
                    library.addLog("Librarian logged out");
                    return;

                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}
