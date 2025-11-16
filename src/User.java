import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private String userName;
    private int registrationNumber;
    private String phoneNumber;
    private String course;
    private char section;
    private String rollNumber;
    private List<IssuedBookInfo> issuedBooks;
    private double fine;

    public User(String userId, String userName, int registrationNumber, String phoneNumber,
                String course, char section, String rollNumber) {
        this.userId = userId;
        this.userName = userName;
        this.registrationNumber = registrationNumber;
        this.phoneNumber = phoneNumber;
        this.course = course;
        this.section = section;
        this.rollNumber = rollNumber;
        this.issuedBooks = new ArrayList<>();
        this.fine = 0.0;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public List<IssuedBookInfo> getIssuedBooks() { return issuedBooks; }
    public double getFine() { return fine; }
    public void addIssuedBook(IssuedBookInfo book) { issuedBooks.add(book); }
    public void removeIssuedBook(String bookCode, int bookNo) {
        issuedBooks.removeIf(b -> b.getBookCode().equals(bookCode) && b.getBookNo() == bookNo);
    }
    public void addFine(double amount) { fine += amount; }
    public void clearFine() { fine = 0; }
}

