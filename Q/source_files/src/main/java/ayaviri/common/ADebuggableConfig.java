package ayaviri.common;

// Represents a configuration that can turn on/off debugging output on the standard error port
public abstract class ADebuggableConfig {
    private final boolean quiet;
    private final DebuggingLogger logger;

    // Creates a debuggable config that will NOT LOG
    public ADebuggableConfig() {
        this.quiet = false;
        this.logger = new DebuggingLogger(!this.quiet);
    }

    // Creates a debuggable config with a logger that logs if the given _quiet_
    // parameter is FALSE
    public ADebuggableConfig(boolean quiet) {
        this.quiet = quiet;
        this.logger = new DebuggingLogger(!quiet);
    }

    public boolean isQuiet() {
        return this.quiet;
    }

    public DebuggingLogger getLogger() {
        return this.logger;
    }
}
