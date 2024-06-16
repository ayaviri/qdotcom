package ayaviri.player.communication_failures;

import ayaviri.player.LocalPlayer;
import ayaviri.strategy.Strategy;
import java.util.function.Supplier;

public abstract class ACommFailurePlayer extends LocalPlayer {
    private int callCount;
    private final int maxCallCount;
    private final Supplier<Integer> explosion;

    public ACommFailurePlayer(String name, Strategy strategy, int count) {
        super(name, strategy);
        this.callCount = 0;
        this.maxCallCount = count;
        this.explosion =
                () -> {
                    return this.infiniteLoop();
                };
    }

    public ACommFailurePlayer(String name, Strategy strategy) {
        super(name, strategy);
        this.callCount = 0;
        this.maxCallCount = 1;
        this.explosion =
                () -> {
                    throw new RuntimeException("Havoc");
                };
    }

    protected void possibleExplosion() {
        this.callCount += 1;

        if (this.callCount == this.maxCallCount) {
            this.explosion.get();
        }
    }

    // Java moment
    private Integer infiniteLoop() {
        boolean x = false;

        while (!x) {
            Thread currentThread = Thread.currentThread();
            if (currentThread.isInterrupted()) {
                throw new RuntimeException("Havoc");
            }
            x = x && false;
        }

        return 1;
    }
}
