package ayaviri.common;

// Represents a logger for debugging purposes that might log to STDERR
public class DebuggingLogger {
    private final boolean shouldLog;

    public DebuggingLogger(boolean shouldLog) {
        this.shouldLog = shouldLog;
    }

    public void println(Object object) {
        if (this.shouldLog) {
            System.err.println(object.toString());
        }
    }
}
