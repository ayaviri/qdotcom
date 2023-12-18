package chen_ayaviri.common;

// Represents a configuration that can turn on/off debugging output on the standard error port
public abstract class ADebuggableConfig {
    // A flag that is set to true when debugging output is desired on the standard error port
    private boolean quiet = true;

    public ADebuggableConfig() {}

    public ADebuggableConfig(boolean quiet) {
        this.quiet = quiet;
    }

    public boolean isQuiet() {
        return this.quiet;
    }
}
