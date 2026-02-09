import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class User {
    private final String name;
    private int age;
    private final List<Book> borrowedBooks = new ArrayList<Book>();

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void haveBirthday() {
        age++;
    }

    public void borrow(Book book) {
        borrowedBooks.add(book);
    }

    public int getBorrowedCount() {
        return borrowedBooks.size();
    }

    public List<Book> getBorrowedBooks() {
        return Collections.unmodifiableList(borrowedBooks);
    }
}
