package task2.cp;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import lombok.experimental.FieldDefaults;

import java.sql.Connection;
import java.util.function.Consumer;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class PooledConnection implements Connection {

    Consumer<PooledConnection> closer;

    @Delegate(excludes = AutoCloseable.class)
    Connection connection;

    @Override
    public void close() {
        closer.accept(this);
    }

    @SneakyThrows
    public void reallyClose() {
        connection.close();
    }
}
