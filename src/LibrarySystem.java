import java.util.Scanner;

public class LibrarySystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Library library = new Library();

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
                case 1:
                    System.out.println("1.Add User 2.Delete User 3.Show Users");
                    int uChoice = Integer.parseInt(sc.nextLine());
                    if (uChoice == 1) library.addUser();
                    else if (uChoice == 2) library.deleteUser();
                    else System.out.println("Feature not implemented");
                    break;
                case 2:
                    library.showBooks();
                    break;
                case 3:
                    System.out.println("Search not implemented yet");
                    break;
                case 4:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}
