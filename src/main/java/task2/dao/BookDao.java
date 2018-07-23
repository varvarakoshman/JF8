package task2.dao;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.val;
import task2.CheckedRunnable;
import task2.cp.JdbcDao;
import task2.model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE)
@FunctionalInterface
public interface BookDao extends JdbcDao<Book>, Supplier<Connection> {

    String INSERT_SQL = "INSERT INTO books (title, author, year) VALUES (?,?,?)";
    String GET_ALL_BOOKS_SQL = "SELECT * FROM books";
    String GET_BOOKS_SQL = "SELECT * FROM books WHERE id = ?";
    String UPDATE_SQL = "UPDATE books SET TITLE = ?, AUTHOR = ?, YEAR = ? WHERE id = ?";
    String DELETE_BOOKS_SQL = "DELETE FROM books WHERE id = ?";
    String DELETE_ALL_BOOKS_SQL = "DELETE FROM books";
    String COUNT_SQL = "SELECT count(id) FROM books";

    String ID_FIELD = "id";
    String TITLE_FIELD = "title";
    String AUTHOR_FIELD = "author";
    String YEAR_FIELD = "year";

    @Override
    @SneakyThrows
    default <U extends Book> U save(U book) {
        @Cleanup Connection connection = get();
        @Cleanup val ps = connection.prepareStatement(INSERT_SQL, RETURN_GENERATED_KEYS);
        ps.setString(1, book.getTitle());
        ps.setString(2, book.getAuthor());
        ps.setInt(3, book.getYear());
        ps.executeUpdate();
        @Cleanup ResultSet rs = ps.getGeneratedKeys();
        if (!rs.next())
            throw new RuntimeException("Не был сгенерирован ключ!");
        return (U) book.setId(rs.getInt(1));
    }

    @Override
    @SneakyThrows
    default <U extends Book> Stream<U> findAll() {
        Connection connection = get();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(GET_ALL_BOOKS_SQL);
        Iterable<U> us = () -> new Iterator<>() {
            @Override
            @SneakyThrows
            public boolean hasNext() {
                return resultSet.next();
            }

            @Override
            @SneakyThrows
            @SuppressWarnings("unchecked")
            public U next() {
                return (U) new Book(resultSet.getLong(ID_FIELD),
                        resultSet.getString(TITLE_FIELD),
                        resultSet.getString(AUTHOR_FIELD),
                        resultSet.getInt(YEAR_FIELD));
            }
        };
        return StreamSupport.stream(us.spliterator(), false)
                .onClose(CheckedRunnable.narrow(resultSet::close).unchecked())
                .onClose(CheckedRunnable.narrow(statement::close).unchecked())
                .onClose(CheckedRunnable.narrow(connection::close).unchecked());
    }

    @Override
    @SneakyThrows
    default <U extends Book> U update(U book) {
        @Cleanup Connection connection = get();
        @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);
        preparedStatement.setString(1, book.getTitle());
        preparedStatement.setString(2, book.getAuthor());
        preparedStatement.setInt(3, book.getYear());
        preparedStatement.setLong(4, book.getId());
        preparedStatement.executeUpdate();
        return book;
    }

    @Override
    @SneakyThrows
    default <U extends Book> JdbcDao<Book> delete(U book) {
        @Cleanup val connection = get();
        @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BOOKS_SQL);
        preparedStatement.setLong(1, book.getId());
        preparedStatement.executeLargeUpdate();
        return this;
    }

    @SneakyThrows
    @Override
    default <U extends Book> Optional<U> findById(long id) {
        @Cleanup val connection = get();
        @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(GET_BOOKS_SQL);
        preparedStatement.setLong(1, id);
        @Cleanup val resultSet = preparedStatement.executeQuery();
        //noinspection unchecked
        return resultSet.next() ? Optional.of((U) new Book(id,
                resultSet.getString(TITLE_FIELD),
                resultSet.getString(AUTHOR_FIELD),
                resultSet.getInt(YEAR_FIELD))) : Optional.empty();
    }

    @Override
    @SneakyThrows
    default JdbcDao<Book> clear() {
        @Cleanup val connection = get();
        @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ALL_BOOKS_SQL);
        preparedStatement.executeLargeUpdate();
        return this;
    }

    @Override
    @SneakyThrows
    default long count() {
        @Cleanup val connection = get();
        @Cleanup val statement = connection.createStatement();
        @Cleanup val resultSet = statement.executeQuery(COUNT_SQL);
        return resultSet.next() ? resultSet.getLong(1) : 0L;
    }
}