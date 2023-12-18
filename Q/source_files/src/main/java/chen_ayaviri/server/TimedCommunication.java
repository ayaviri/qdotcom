package chen_ayaviri.server;

import java.util.concurrent.*;
import java.util.function.Supplier;

// Represents a Callable task with a given punishment to be completed in _timeout_ seconds
// after the call to the _attempt_ method
// Executes the task in a new thread
public class TimedCommunication<T> {
    private final Callable<T> callable;
    private final int timeoutInSeconds;
    private final Supplier<Void> punishment;

    // TODO: perhaps update to have the punishment return a separate generic type ?
    public TimedCommunication(Callable<T> callable, int timeoutInSeconds, Supplier<Void> punishment) {
        this.callable = callable;
        if (timeoutInSeconds < 1) {
            throw new IllegalArgumentException("Bad timeout value given in construction");
        }
        this.timeoutInSeconds = timeoutInSeconds;
        this.punishment = punishment;
    }

    public CommunicationResult<T> attempt() {
        CommunicationResult<T> communicationResult = new CommunicationResult<>();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<T> future = executor.submit(this.callable);

        try {
            T returnValue = future.get((long) this.timeoutInSeconds, TimeUnit.SECONDS);
            communicationResult = new CommunicationResult<T>(returnValue);
        } catch (Exception e) {
            this.dishOutPunishment(future);
        }

        executor.shutdown();
        return communicationResult;
    }

    protected void dishOutPunishment(Future<T> future) {
        this.punishment.get();
        future.cancel(true);
    }
}
