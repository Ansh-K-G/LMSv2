import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public void searchUser() {
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("Search user by:\n1. User ID\n2. Name\n3. Roll Number\n4. Phone Number");
        System.out.print("Choice: ");
        int choice = Integer.parseInt(sc.nextLine());
        System.out.print("Enter search value: ");
        String query = sc.nextLine().trim();

        List<User> results = new java.util.ArrayList<>();

        for (User u : users) {
            switch (choice) {
                case 1:
                    if (u.getUserId().equalsIgnoreCase(query))
                        results.add(u);
                    break;
                case 2:
                    if (u.getUserName().toLowerCase().contains(query.toLowerCase()))
                        results.add(u);
                    break;
                case 3:
                    if (u.getRollNumber().equalsIgnoreCase(query))
                        results.add(u);
                    break;
                case 4:
                    if (u.getPhoneNumber().equalsIgnoreCase(query))
                        results.add(u);
                    break;
                default:
                    System.out.println("Invalid choice");
                    return;
            }
        }

        if (results.isEmpty()) {
            System.out.println("No matching users found.");
        } else {
            System.out.println("Found Users:");
            for (User u : results) {
                System.out.println("ID: " + u.getUserId() + ", Name: " + u.getUserName() + ", Fine: " + u.getFine());
            }
        }
    }

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
    public void searchBook() {
        if (books.isEmpty()) {
            System.out.println("No books found.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("Search book by:\n1. Book Code\n2. Name\n3. Author");
        System.out.print("Choice: ");
        int choice = Integer.parseInt(sc.nextLine());
        System.out.print("Enter search value: ");
        String query = sc.nextLine().trim();

        List<Book> results = new java.util.ArrayList<>();

        for (Book b : books) {
            switch (choice) {
                case 1:
                    if (b.getBookCode().equalsIgnoreCase(query))
                        results.add(b);
                    break;
                case 2:
                    if (b.getBookName().toLowerCase().contains(query.toLowerCase()))
                        results.add(b);
                    break;
                case 3:
                    if (b.getAuthor().toLowerCase().contains(query.toLowerCase()))
                        results.add(b);
                    break;
                default:
                    System.out.println("Invalid choice");
                    return;
            }
        }

        if (results.isEmpty()) {
            System.out.println("No matching books found.");
        } else {
            System.out.println("Found Books:");
            for (Book b : results) {
                System.out.println(
                        "Code: " + b.getBookCode() + ", Name: " + b.getBookName() + ", Author: " + b.getAuthor() +
                                ", Available: " + b.getAvailableBooks().size() + "/" + b.getTotalBooks());
            }
        }
    }

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

    public void showUsers(int n, String mode) {
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.println("------ Library Users ------");

        int count = Math.min(n, users.size());

        for (int i = 0; i < count; i++) {
            User u = users.get(i);

            switch (mode.toLowerCase()) {
                case "important":
                    System.out.println("User ID: " + u.getUserId());
                    System.out.println("Name: " + u.getUserName());
                    System.out.println("Fine: " + u.getFine());
                    break;

                case "all":
                    System.out.println("User ID: " + u.getUserId());
                    System.out.println("Name: " + u.getUserName());
                    System.out.println("Course: " + u.getCourse());
                    System.out.println("Section: " + u.getSection());
                    System.out.println("Roll Number: " + u.getRollNumber());
                    System.out.println("Phone Number: " + u.getPhoneNumber());
                    System.out.println("Registration Number: " + u.getRegistrationNumber());
                    System.out.println("Fine: " + u.getFine());
                    if (!u.getIssuedBooks().isEmpty()) {
                        System.out.println("Issued Books:");
                        for (IssuedBookInfo b : u.getIssuedBooks()) {
                            System.out.println("  Book Code: " + b.getBookCode() + ", Book No: " + b.getBookNo());
                        }
                    }
                    break;

                default:
                    // field mode: comma separated fields, e.g., "id,name,fine"
                    String[] fields = mode.split(",");
                    for (String f : fields) {
                        f = f.trim().toLowerCase();
                        switch (f) {
                            case "id":
                                System.out.println("User ID: " + u.getUserId());
                                break;
                            case "name":
                                System.out.println("Name: " + u.getUserName());
                                break;
                            case "course":
                                System.out.println("Course: " + u.getCourse());
                                break;
                            case "section":
                                System.out.println("Section: " + u.getSection());
                                break;
                            case "roll":
                                System.out.println("Roll Number: " + u.getRollNumber());
                                break;
                            case "phone":
                                System.out.println("Phone Number: " + u.getPhoneNumber());
                                break;
                            case "reg":
                                System.out.println("Registration Number: " + u.getRegistrationNumber());
                                break;
                            case "fine":
                                System.out.println("Fine: " + u.getFine());
                                break;
                            case "issuedbooks":
                                if (!u.getIssuedBooks().isEmpty()) {
                                    System.out.println("Issued Books:");
                                    for (IssuedBookInfo b : u.getIssuedBooks()) {
                                        System.out.println(
                                                "  Book Code: " + b.getBookCode() + ", Book No: " + b.getBookNo());
                                    }
                                }
                                break;
                            default:
                                System.out.println("Unknown field: " + f);
                        }
                    }
                    break;
            }

            System.out.println("---------------------------");
        }

        if (count < users.size()) {
            System.out.println("...and " + (users.size() - count) + " more users");
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

    public void addBook() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Book Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Author Name: ");
        String author = sc.nextLine();

        System.out.print("Enter Book Code: ");
        String code = sc.nextLine();

        // Check if book already exists
        for (Book b : books) {
            if (b.getBookCode().equalsIgnoreCase(code)) {
                System.out.println("Book with this code already exists!");
                return;
            }
        }

        System.out.print("Enter total number of copies: ");
        int total = Integer.parseInt(sc.nextLine());

        Book book = new Book(name, author, code, total);
        books.add(book);

        saveBooks(); // save to JSON file
        addLog("Added Book: " + name + " (" + code + ")");
        System.out.println("Book added successfully!");
    }

    public void removeBook() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Book Code to remove: ");
        String code = sc.nextLine();

        Book toRemove = null;

        for (Book b : books) {
            if (b.getBookCode().equalsIgnoreCase(code)) {
                toRemove = b;
                break;
            }
        }

        if (toRemove == null) {
            System.out.println("Book not found!");
            return;
        }

        // Check if any copy is issued
        if (toRemove.getAvailableBooks().size() < toRemove.getTotalBooks()) {
            System.out.println("Cannot remove book. Some copies are currently issued.");
            return;
        }

        books.remove(toRemove);
        saveBooks(); // update JSON
        addLog("Removed Book: " + toRemove.getBookName() + " (" + toRemove.getBookCode() + ")");
        System.out.println("Book removed successfully!");
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

    public void addLog(String message) {
        // Get current date & time
        LocalDateTime now = LocalDateTime.now();
        String dateTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Create log entry
        Log log = new Log(dateTime + " - " + message);

        // Add to logs list and save
        saveLogs(log);
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
