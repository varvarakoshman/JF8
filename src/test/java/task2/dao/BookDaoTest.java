package task2.dao;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.*;
import task2.CheckedRunnable;
import task2.cp.ConnectionPool;
import task2.model.Book;

import static org.junit.jupiter.api.Assertions.*;

/*
Задание 2. DAO&ConnectionPool
Спроектируйте БД, хранящую информацию, например, о домашней библиотеке.
Реализуйте функциональность добавления, поиска и удаления разнообразной информации
из этой БД. При реализации используйте (напишите) пул соединений и DAO.
 */

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class BookDaoTest {

    static ConnectionPool connectionPool = new ConnectionPool();
    static BookDao bookDao = connectionPool::get;

    @Test
    @DisplayName("Save method works correctly")
    void testSave() {
        Book bookLambdas = bookDao.save(new Book("Java 8 Lambdas", "Richard Warburton", 2014));
        assertNotEquals(bookLambdas.getId(), 0L);
        bookDao.delete(bookLambdas);
    }

    @Test
    @DisplayName("FindAll method works correctly")
    void testFindAll() {
        Book bookLambdas = bookDao.save(new Book("Java 8 Lambdas", "Richard Warburton", 2014));
        assertEquals(bookDao.findAll().count(), 2L);
        bookDao.delete(bookLambdas);
    }

    @Test
    @DisplayName("Update method works correctly")
    void testUpdate() {
        Book book1 = bookDao.findById(1L).orElseThrow(() -> {
            CheckedRunnable.narrow(Assertions::fail).unchecked();
            return null;
        });
        bookDao.update(book1.setYear(1914));
        Book book2 = bookDao.findById(1L).orElseThrow(() -> {
            CheckedRunnable.narrow(Assertions::fail).unchecked();
            return null;
        });
        assertEquals(book2, book1);
    }

    @Test
    @DisplayName("Delete method works correctly")
    void testDelete() {
        assertEquals(bookDao.delete(bookDao.findById(1L).orElseThrow(() -> {
            CheckedRunnable.narrow(Assertions::fail).unchecked();
            return null;
        })), 0L);
    }

    @Test
    @DisplayName("Clear method works correctly")
    void testClear() {
        assertEquals(bookDao.clear().count(), 0L);
    }

    @AfterAll
    static void tearDown() {
        connectionPool.close();
    }
}