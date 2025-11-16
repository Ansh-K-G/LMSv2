import java.time.LocalDateTime;

public class Log {
    private String message;
    private String userId;
    private String bookCode;
    private int bookNo;
    private LocalDateTime timestamp;

    // Constructor for actions without book info
    public Log(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    // Constructor for actions with book info
    public Log(String message, String userId, String bookCode, int bookNo) {
        this.message = message;
        this.userId = userId;
        this.bookCode = bookCode;
        this.bookNo = bookNo;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public String getMessage() { return message; }
    public String getUserId() { return userId; }
    public String getBookCode() { return bookCode; }
    public int getBookNo() { return bookNo; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return message + " | userId=" + userId + " | bookCode=" + bookCode + " | bookNo=" + bookNo + " | " + timestamp;
    }
}

