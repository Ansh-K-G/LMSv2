import java.util.ArrayList;
import java.util.List;

public class Book {
    private String bookName;
    private String author;
    private String bookCode;
    private int totalBooks;
    private List<Integer> availableBooks;

    public Book(String bookName, String author, String bookCode, int totalBooks) {
        this.bookName = bookName;
        this.author = author;
        this.bookCode = bookCode;
        this.totalBooks = totalBooks;
        this.availableBooks = new ArrayList<>();
        for (int i = 1; i <= totalBooks; i++) availableBooks.add(i);
    }

    // Getters and Setters
    public String getBookCode() { return bookCode; }
    public String getBookName() { return bookName; }
    public List<Integer> getAvailableBooks() { return availableBooks; }

    public boolean isAvailable() { return !availableBooks.isEmpty(); }

    public int issueBook() {
        if (isAvailable()) return availableBooks.remove(0);
        return -1;
    }

    public void returnBook(int bookNo) { availableBooks.add(bookNo); }
}
