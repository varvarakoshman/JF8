package task2;

import static task2.Exceptional.sneakyThrow;

public interface CheckedRunnable extends io.vavr.CheckedRunnable{
    static CheckedRunnable narrow(CheckedRunnable checkedRunnable) {
        return checkedRunnable;
    }

    default  Runnable unchecked() {
        return () -> {
            try {
                run();
            } catch (Throwable t) {
                sneakyThrow(t);
            }
        };
    }
}
