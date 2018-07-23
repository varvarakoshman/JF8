package task2;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import task2.cp.ConnectionPool;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal = true)
class ConnectionPoolTest {

    String GET_BOOKS_SQL = "SELECT * FROM books WHERE year > 2000 AND author='Scott Selikoff'";

    String ID_FIELD = "id";
    String TITLE_FIELD = "title";
    String AUTHOR_FIELD = "author";
    String YEAR_FIELD = "year";

    @Test
    @DisplayName("ConnectionPool works correctly")
    @SneakyThrows
    void testGet() {
        @Cleanup val connection = new ConnectionPool().get();
        @Cleanup val statement = connection.createStatement();
        @Cleanup val resultSet = statement.executeQuery(GET_BOOKS_SQL);
        assertTrue(resultSet.next());
        assertEquals(resultSet.getInt(ID_FIELD), 1);
        assertEquals(resultSet.getString(TITLE_FIELD), "OCA study guide");
        assertEquals(resultSet.getString(AUTHOR_FIELD), "Scott Selikoff");
        assertEquals(resultSet.getInt(YEAR_FIELD), 2014);
    }
}