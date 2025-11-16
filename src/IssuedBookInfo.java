import java.time.LocalDate;

public class IssuedBookInfo {
    private String userId;
    private String bookCode;
    private int bookNo;
    private LocalDate issueDate;
    private LocalDate returnDate;
    private boolean status; // true if returned

    public IssuedBookInfo(String userId, String bookCode, int bookNo) {
        this.userId = userId;
        this.bookCode = bookCode;
        this.bookNo = bookNo;
        this.issueDate = LocalDate.now();
        this.status = false;
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public String getBookCode() { return bookCode; }
    public int getBookNo() { return bookNo; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean isReturned() { return status; }

    public void markReturned() {
        this.status = true;
        this.returnDate = LocalDate.now();
    }
}
