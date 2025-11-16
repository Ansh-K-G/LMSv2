import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
            saveUsers();
            saveLogs(new Log("User added", userId, null, 0));
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
            saveUsers();
            saveLogs(new Log("User deleted", userId, null, 0));
            System.out.println("User deleted successfully.");
        }
    }

    private User getUserById(String userId) {
        for (User u : users)
            if (u.getUserId().equalsIgnoreCase(userId))
                return u;
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

        saveBooks();
        saveUsers();
        saveIssuedBooks();
        saveLogs(new Log("Book issued", userId, bookCode, bookNo));
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
        saveBooks();
        saveUsers();
        saveIssuedBooks();
        saveLogs(new Log("Book returned", userId, bookCode, bookNo));
        System.out.println("Book returned successfully");
    }

    // ------------------ LOGS ----------------------

    public void showLogs() {
        for (Log log : logs)
            System.out.println(log);
    }

    // ------------------ FILE HANDLING ----------------------

    private void loadCounter() {
        try {
            if (!Files.exists(Paths.get(counterFile))) {
                countUser = 0;
                saveCounter();
                return;
            }
            String content = new String(Files.readAllBytes(Paths.get(counterFile)));
            JSONObject obj = new JSONObject(content);
            countUser = obj.getInt("countUser");
        } catch (Exception e) {
            countUser = 0;
        }
    }

    private void saveCounter() {
        try (FileWriter writer = new FileWriter(counterFile)) {
            JSONObject obj = new JSONObject();
            obj.put("countUser", countUser);
            writer.write(obj.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCounter() {
        try (FileWriter writer = new FileWriter("data/userCounter.json")) {
            JSONObject obj = new JSONObject();
            obj.put("countUser", countUser); // save the current user count
            writer.write(obj.toString(4)); // pretty print with 4 spaces
        } catch (IOException e) {
            System.out.println("Error updating user counter: " + e.getMessage());
        }
    }

    // ------------------ Users ----------------------

    private void loadUsers() {
        users.clear();
        try {
            if (!Files.exists(Paths.get(userFile)))
                return;
            String content = new String(Files.readAllBytes(Paths.get(userFile)));
            JSONArray arr = new JSONArray(content);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject u = arr.getJSONObject(i);
                User user = new User(
                        u.getString("userId"),
                        u.getString("userName"),
                        u.getInt("registrationNumber"),
                        u.getString("phoneNumber"),
                        u.getString("course"),
                        u.getString("section").charAt(0),
                        u.getString("rollNumber"));
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveUsers() {
        try (FileWriter writer = new FileWriter(userFile)) {
            JSONArray arr = new JSONArray();
            for (User u : users) {
                JSONObject obj = new JSONObject();
                obj.put("userId", u.getUserId());
                obj.put("userName", u.getUserName());
                obj.put("registrationNumber", u.getRegistrationNumber());
                obj.put("phoneNumber", u.getPhoneNumber());
                obj.put("course", u.getCourse());
                obj.put("section", String.valueOf(u.getSection()));
                obj.put("rollNumber", u.getRollNumber());
                arr.put(obj);
            }
            writer.write(arr.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ------------------ Books ----------------------

    private void loadBooks() {
        books.clear();
        try {
            if (!Files.exists(Paths.get(bookFile)))
                return;
            String content = new String(Files.readAllBytes(Paths.get(bookFile)));
            JSONArray arr = new JSONArray(content);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject b = arr.getJSONObject(i);
                Book book = new Book(
                        b.getString("bookName"),
                        b.getString("author"),
                        b.getString("bookCode"),
                        b.getInt("totalBooks"));
                JSONArray avail = b.getJSONArray("availableBooks");
                for (int j = 0; j < avail.length(); j++)
                    book.getAvailableBooks().add(avail.getInt(j));
                books.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveBooks() {
        try (FileWriter writer = new FileWriter(bookFile)) {
            JSONArray arr = new JSONArray();
            for (Book b : books) {
                JSONObject obj = new JSONObject();
                obj.put("bookName", b.getBookName());
                obj.put("author", b.getAuthor());
                obj.put("bookCode", b.getBookCode());
                obj.put("totalBooks", b.getTotalBooks());
                JSONArray avail = new JSONArray();
                for (int x : b.getAvailableBooks())
                    avail.put(x);
                obj.put("availableBooks", avail);
                arr.put(obj);
            }
            writer.write(arr.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ------------------ Issued Books ----------------------

    private void loadIssuedBooks() {
        issuedBooks.clear();
        try {
            if (!Files.exists(Paths.get(issuedFile)))
                return;
            String content = new String(Files.readAllBytes(Paths.get(issuedFile)));
            JSONArray arr = new JSONArray(content);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject b = arr.getJSONObject(i);
                IssuedBookInfo info = new IssuedBookInfo(
                        b.getString("userId"),
                        b.getString("bookCode"),
                        b.getInt("bookNo"));
                issuedBooks.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveIssuedBooks() {
        try (FileWriter writer = new FileWriter(issuedFile)) {
            JSONArray arr = new JSONArray();
            for (IssuedBookInfo b : issuedBooks) {
                JSONObject obj = new JSONObject();
                obj.put("userId", b.getUserId());
                obj.put("bookCode", b.getBookCode());
                obj.put("bookNo", b.getBookNo());
                arr.put(obj);
            }
            writer.write(arr.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ------------------ Logs ----------------------

    private void loadLogs() {
        logs.clear();
        try {
            if (!Files.exists(Paths.get(logFile)))
                return;

            String content = new String(Files.readAllBytes(Paths.get(logFile)));
            JSONArray arr = new JSONArray(content);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject l = arr.getJSONObject(i);
                Log log = new Log(
                        l.getString("message"),
                        l.optString("userId", null),
                        l.optString("bookCode", null),
                        l.optInt("bookNo", 0));
                // Parse timestamp
                if (l.has("timestamp"))
                    log = new Log(l.getString("message"),
                            l.optString("userId", null),
                            l.optString("bookCode", null),
                            l.optInt("bookNo", 0));
                logs.add(log);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveLogs(Log log) {
        logs.add(log);
        try (FileWriter writer = new FileWriter(logFile)) {
            JSONArray arr = new JSONArray();

            for (Log l : logs) {
                JSONObject obj = new JSONObject();
                obj.put("message", l.getMessage());
                obj.put("userId", l.getUserId());
                obj.put("bookCode", l.getBookCode());
                obj.put("bookNo", l.getBookNo());
                obj.put("timestamp", l.getTimestamp().toString());
                arr.put(obj);
            }

            writer.write(arr.toString(4)); // pretty print JSON
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Book getBookByCode(String code) {
        for (Book b : books)
            if (b.getBookCode().equalsIgnoreCase(code))
                return b;
        return null;
    }
}
