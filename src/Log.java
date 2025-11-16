import java.time.LocalDateTime;

public class Log {
    private String message;
    private LocalDateTime dateTime;
    private String userId;
    private String bookCode;
    private int bookNo;

    public Log(String message) {
        this.message = message;
        this.dateTime = LocalDateTime.now();
    }

    public Log(String message, String userId, String bookCode, int bookNo) {
        this(message);
        this.userId = userId;
        this.bookCode = bookCode;
        this.bookNo = bookNo;
    }

    @Override
    public String toString() {
        String info = dateTime + " | " + message;
        if (userId != null) info += " | UserId: " + userId;
        if (bookCode != null) info += " | BookCode: " + bookCode;
        if (bookNo != 0) info += " | BookNo: " + bookNo;
        return info;
    }
}
