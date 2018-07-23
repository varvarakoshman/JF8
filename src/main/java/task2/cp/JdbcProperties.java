package task2.cp;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.Value;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.function.Supplier;

@Value
@Getter(AccessLevel.NONE)
public class JdbcProperties implements Supplier<Connection> {
    String url;
    String user;
    String password;

    @Getter
    int poolSize;

    @Getter
    String sqlFolder;

    @Override
    @SneakyThrows
    public Connection get() {
        return DriverManager.getConnection(url, user, password);
    }
}
