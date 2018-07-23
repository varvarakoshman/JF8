package task2.cp;

import io.vavr.CheckedFunction1;
import io.vavr.Function1;
import io.vavr.Function2;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.val;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConnectionPool implements Supplier<Connection>, AutoCloseable {

    final BlockingQueue<PooledConnection> freeConnections;
    private volatile boolean isOpened = true;

    public ConnectionPool() {
        JdbcProperties jdbcProperties = PropsBinder.from("jdbc", JdbcProperties.class);

        int poolSize = jdbcProperties.getPoolSize();

        Function<Connection, PooledConnection> pooledConnectionCreator =
                Function2.of(PooledConnection::new)
                        .apply(this::closer);

        freeConnections = IntStream.iterate(0, operand -> operand + 1)
                .limit(poolSize)
                .mapToObj(__ -> jdbcProperties.get())
                .map(pooledConnectionCreator)
                .collect(Collectors.toCollection(() -> new ArrayBlockingQueue<>(poolSize)));

        String sqlFolder = jdbcProperties.getSqlFolder();

        Function1<String, Optional<String>> getFileAsString =
                Function2.narrow(ConnectionPool::getFileAsString)
                        .apply(sqlFolder);

        execute(IntStream.iterate(1, operand -> operand + 1)
                .mapToObj(String::valueOf)
                .map(getFileAsString)
                .takeWhile(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.joining()));
    }

    @SneakyThrows
    public void execute(String sql) {
        @Cleanup Connection connection = get();
        @Cleanup Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
    }

    private static Optional<String> getFileAsString(String initScriptsPath, String name) {
        val path = String.format("/%s/%s.sql", initScriptsPath, name);
        return Optional.ofNullable(ConnectionPool.class.getResource(path))
                .map(CheckedFunction1.narrow(URL::toURI).unchecked())
                .map(Paths::get)
                .map(CheckedFunction1.<Path, Stream<String>>narrow(Files::lines).unchecked())
                .map(stringStream -> stringStream.collect(Collectors.joining()));
    }

    @SneakyThrows
    private void closer(PooledConnection pooledConnection) {
        if (isOpened) {
            if (!pooledConnection.getAutoCommit()){
                pooledConnection.setAutoCommit(true);
            }
            if (pooledConnection.isReadOnly()){
                pooledConnection.setReadOnly(false);
            }
            freeConnections.put(pooledConnection);
        }else{
            pooledConnection.reallyClose();
        }
    }

    @SneakyThrows
    public Connection get() {
        //return DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        return freeConnections.take();
    }

    @Override
    public void close() {
        isOpened = false;
        freeConnections.forEach(PooledConnection::close);
    }
}
