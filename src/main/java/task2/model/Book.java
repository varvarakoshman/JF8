package task2.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import task2.cp.Identifiable;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class Book implements Identifiable<Book> {

    public Book setId(long id) {
        this.id = id;
        return this;
    }

    public Book setYear(int year) {
        this.year = year;
        return this;
    }

    long id;
    String title;
    String author;
    int year;

    public Book(String title, String author, int year) {
        this(0L, title, author, year);
    }
}
