import org.json.JSONArray;
import org.json.JSONObject;
import org.json.parser.JSONParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Library {
    private List<User> users;
    private List<Book> books;
    private List<IssuedBookInfo> issuedBooks;
    private List<Log> logs;
    private int countUser;
    private final String userFile = "data/users.json";
    private final String bookFile = "data/books.json";
    private final String issuedFile = "data/issuedbooks.json";
    private final String logFile = "data/logs.json";
    private final String counterFile = "data/userCounter.json";

    private Scanner sc = new Scanner(System.in);

    public Library() {
        users = new ArrayList<>();
        books = new ArrayList<>();
        issuedBooks = new ArrayList<>();
        logs = new ArrayList<>();
        loadCounter();
        loadUsers();
        loadBooks();
        loadIssuedBooks();
        loadLogs();
    }

    // ------------------ USER MANAGEMENT ----------------------
    public void addUser() {
        try {
            System.out.print("Enter Name: ");
            String name = sc.nextLine();
            System.out.print("Enter Branch: ");
            String branch = sc.nextLine();
            System.out.print("Enter Roll No: ");
            String roll = sc.nextLine();

            // Check existing user
            for (User u : users) {
                if (u.getUserName().equalsIgnoreCase(name) && u.getRollNumber().equalsIgnoreCase(roll)) {
                    System.out.println("User already exists!");
                    return;
                }
            }

            System.out.print("Phone Number: ");
            String phone = sc.nextLine();
            System.out.print("Course: ");
            String course = sc.nextLine();
            System.out.print("Section: ");
            char section = sc.nextLine().charAt(0);

            String userId = generateUserId(countUser + 1, name, branch);
            countUser++;
            User user = new User(userId, name, countUser, phone, course, section, roll);
            users.add(user);
            updateCounter();
            updateUsersFile();
            updateLog(new Log("User added", userId, null, 0));
            System.out.println("User added successfully with ID: " + userId);
        } catch (Exception e) {
            System.out.println("Error adding user: " + e.getMessage());
        }
    }

    public void deleteUser() {
        System.out.print("Enter User ID to delete: ");
        String userId = sc.nextLine();
        User user = getUserById(userId);
        if (user == null) {
            System.out.println("User not found");
            return;
        }
        if (user.getFine() != 0 || !user.getIssuedBooks().isEmpty()) {
            System.out.println("Cannot delete user: User has fine or issued books.");
            return;
        }
        System.out.print("Confirm delete? (y/n): ");
        if (sc.nextLine().equalsIgnoreCase("y")) {
            users.remove(user);
            updateUsersFile();
            updateLog(new Log("User deleted", userId, null, 0));
            System.out.println("User deleted successfully.");
        }
    }

    private User getUserById(String userId) {
        for (User u : users) if (u.getUserId().equalsIgnoreCase(userId)) return u;
        return null;
    }

    private String generateUserId(int count, String name, String branch) {
        String countStr = String.format("%03d", count);
        String namePart = name.length() >= 3 ? name.substring(0, 3).toUpperCase() : name.toUpperCase();
        return countStr + namePart + "#" + branch.toUpperCase();
    }

    // ------------------ BOOK MANAGEMENT ----------------------
    public void showBooks() {
        System.out.println("Available Books:");
        for (Book b : books) {
            System.out.println(b.getBookCode() + " | " + b.getBookName() + " | Author: " + b.getAuthor()
                    + " | Available: " + b.getAvailableBooks().size());
        }
    }

    public void issueBook() {
        System.out.print("Enter Book Code: ");
        String bookCode = sc.nextLine();
        System.out.print("Enter User ID: ");
        String userId = sc.nextLine();

        Book book = getBookByCode(bookCode);
        User user = getUserById(userId);

        if (book == null || user == null) {
            System.out.println("Invalid Book or User");
            return;
        }
        if (!book.isAvailable()) {
            System.out.println("Book not available");
            return;
        }

        int bookNo = book.issueBook();
        IssuedBookInfo info = new IssuedBookInfo(userId, bookCode, bookNo);
        user.addIssuedBook(info);
        issuedBooks.add(info);

        updateBooksFile();
        updateUsersFile();
        updateIssuedBooksFile();
        updateLog(new Log("Book issued", userId, bookCode, bookNo));
        System.out.println("Book issued successfully. BookNo: " + bookNo);
    }

    public void returnBook() {
        System.out.print("Enter Book Code: ");
        String bookCode = sc.nextLine();
        System.out.print("Enter Book Number: ");
        int bookNo = Integer.parseInt(sc.nextLine());
        System.out.print("Enter User ID: ");
        String userId = sc.nextLine();

        User user = getUserById(userId);
        Book book = getBookByCode(bookCode);
        if (user == null || book == null) {
            System.out.println("Invalid User or Book");
            return;
        }

        boolean hasBook = false;
        for (IssuedBookInfo info : user.getIssuedBooks()) {
            if (info.getBookCode().equals(bookCode) && info.getBookNo() == bookNo && !info.isReturned()) {
                info.markReturned();
                hasBook = true;
                break;
            }
        }

        if (!hasBook) {
            System.out.println("Cannot return book: Book not issued to this user");
            return;
        }

        book.returnBook(bookNo);
        updateBooksFile();
        updateUsersFile();
        updateIssuedBooksFile();
        updateLog(new Log("Book returned", userId, bookCode, bookNo));
        System.out.println("Book returned successfully");
    }

    // ------------------ LOGS ----------------------
    public void showLogs() {
        for (Log log : logs) System.out.println(log);
    }

    // ------------------ FILE HANDLING ----------------------
    private void loadCounter() {
        File f = new File(counterFile);
        if (!f.exists()) {
            countUser = 0;
            updateCounter();
            return;
        }
        try (FileReader reader = new FileReader(f)) {
            JSONObject obj = (JSONObject) new JSONParser().parse(reader);
            countUser = ((Long)obj.get("countUser")).intValue();
        } catch (Exception e) { countUser = 0; }
    }

    private void updateCounter() {
        try (FileWriter writer = new FileWriter(counterFile)) {
            JSONObject obj = new JSONObject();
            obj.put("countUser", countUser);
            writer.write(obj.toJSONString());
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadUsers() { users = new ArrayList<>(); }
    private void loadBooks() { books = new ArrayList<>(); }
    private void loadIssuedBooks() { issuedBooks = new ArrayList<>(); }
    private void loadLogs() { logs = new ArrayList<>(); }

    private void updateUsersFile() {}
    private void updateBooksFile() {}
    private void updateIssuedBooksFile() {}
    private void updateLog(Log log) { logs.add(log); }

    private Book getBookByCode(String code) {
        for (Book b : books) if (b.getBookCode().equalsIgnoreCase(code)) return b;
        return null;
    }
}
