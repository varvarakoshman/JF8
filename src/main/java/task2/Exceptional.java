package task2;

public interface Exceptional {
    static <E extends Throwable> void sneakyThrow(Throwable e) throws E{
        throw(E) e;
    }
}
