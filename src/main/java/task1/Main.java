package task1;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.sql.DriverManager;

import static lombok.AccessLevel.PRIVATE;

@UtilityClass
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class Main {
    String DB_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";

    String CREATE_TABLE_SQL = "CREATE TABLE books (id INT PRIMARY KEY, title VARCHAR, author VARCHAR, price REAL)";
    String INSERT_BOOKS_SQL1 = "INSERT INTO books (id, title,author, price) VALUES (1, 'OCA study guide', 'Scott Selikoff', 1100)";//вставка новой записи
    String INSERT_BOOKS_SQL2 = "INSERT INTO books (id, title,author, price) VALUES (2, 'OCP study guide', 'Scott Selikoff', 1200)";
    String INSERT_BOOKS_SQL3 = "INSERT INTO books (id, title,author, price) VALUES (3,'OCP Java SE7', 'Mala Gupta', 800)";
    String GET_BOOKS_SQL = "SELECT * FROM books WHERE price > 1000 AND author='Scott Selikoff'"; //выбор конкретной записи
    String UPDATE_BOOKS_SQL = "UPDATE books SET price=799,99 WHERE author='Mala Gupta'";//обновление записи
    String DELETE_BOOKS_SQL = "DELETE FROM books";//удаление таблицы
    String GET_BOOKS = "SELECT * FROM books";

    String ID_FIELD = "id";
    String TITLE_FIELD = "title";
    String AUTHOR_FIELD = "author";
    String PRICE_FIELD = "price";

    @SneakyThrows
    public void main(String[] args) {
        @Cleanup val connection = DriverManager.getConnection(DB_URL);
        @Cleanup val statement = connection.createStatement();
        statement.executeUpdate(CREATE_TABLE_SQL);
        statement.executeUpdate(INSERT_BOOKS_SQL1);
        statement.executeUpdate(INSERT_BOOKS_SQL2);
        statement.executeUpdate(INSERT_BOOKS_SQL3);
        @Cleanup val resultSet = statement.executeQuery(GET_BOOKS_SQL);
        while (resultSet.next()) {
            System.out.printf("%d|%s|%s|%f\n",
                    resultSet.getInt(ID_FIELD),
                    resultSet.getString(TITLE_FIELD),
                    resultSet.getString(AUTHOR_FIELD),
                    resultSet.getDouble(PRICE_FIELD));
        }
        statement.executeUpdate(UPDATE_BOOKS_SQL);
        @Cleanup val resultSet2 = statement.executeQuery(GET_BOOKS);
        while (resultSet2.next()) {
            System.out.printf("%d|%s|%s|%f\n",
                    resultSet2.getInt(ID_FIELD),
                    resultSet2.getString(TITLE_FIELD),
                    resultSet2.getString(AUTHOR_FIELD),
                    resultSet2.getDouble(PRICE_FIELD));
        }
        statement.executeUpdate(DELETE_BOOKS_SQL);
    }
}
