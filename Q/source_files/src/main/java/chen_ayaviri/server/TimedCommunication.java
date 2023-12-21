package chen_ayaviri.server;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Supplier;

// Represents a Callable task with a given punishment to be completed in _timeout_ seconds
// after the call to the _attempt_ method
// Executes the task in a new thread
public class TimedCommunication<T> {
    private final Callable<T> callable;
    private final int timeoutInSeconds;
    private final Supplier<Void> failureCallback;
    private final Supplier<Void> successCallback;

    // Constructs a TimedCommunication object from the builder, defaulting to absent failure and success callbacks
    private TimedCommunication(Builder builder) {
        if (builder.timeoutInSeconds < 1) {
            throw new IllegalArgumentException("Bad timeout value given in construction");
        }

        Supplier<Void> callbackAbsence = () -> { return null; };
        this.callable = builder.callable;
        this.timeoutInSeconds = builder.timeoutInSeconds;
        // Java recognises the return values of the Optional fields as of type Object as 
        // opposed to Supplier<Void>, so an explicit cast resolves compilation errors...
        this.failureCallback = (Supplier<Void>) builder.failureCallback.orElse(callbackAbsence);
        this.successCallback = (Supplier<Void>) builder.successCallback.orElse(callbackAbsence);
    }

    // Attempts to execute this object's callable within the given timeout. Then
    // executes the failure callback if the callable throws an exception or exceeds this
    // object's timeout limit and returns a failure CommunicationResult. Executes the
    // success callback otherwise, returning a success CommunicationResult with the 
    // callable's return value
    public CommunicationResult<T> attempt() {
        T returnValue;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<T> future = executor.submit(this.callable);

        try {
            returnValue = future.get((long) this.timeoutInSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            return this.inCaseOfFailure(executor, future);
        }

        return inCaseOfSuccess(executor, returnValue);
    }

    // Shuts down the given executor, executes this object's success callback, and returns a
    // success CommunicationResult with the given return value
    protected CommunicationResult<T> inCaseOfSuccess(ExecutorService executor, T returnValue) {
        executor.shutdown();
        this.successCallback.get();

        return new CommunicationResult<T>(returnValue);
    }

    // Cancels + interrupts the task of the given future, shuts down the given executor, 
    // executes this object's failure callback, and returns a failure CommunicationResult
    protected CommunicationResult<T> inCaseOfFailure(ExecutorService executor, Future<T> future) {
        future.cancel(true);
        executor.shutdown();
        this.failureCallback.get();

        return new CommunicationResult<>();
    }

    public static class Builder<T> {
        private final Callable<T> callable;
        private final int timeoutInSeconds;
        private Optional<Supplier<Void>> failureCallback;
        private Optional<Supplier<Void>> successCallback;

        // Constructs a new Builder with a callable and a timeout, as those two fields
        // are not optional
        public Builder(Callable<T> callable, int timeoutInSeconds) {
            this.callable = callable;
            this.timeoutInSeconds = timeoutInSeconds;
            this.failureCallback = Optional.empty();
            this.successCallback = Optional.empty();
        }

        public Builder failureCallback(Supplier<Void> callback) {
            this.failureCallback = Optional.of(callback);
            return this;
        }

        public Builder successCallback(Supplier<Void> callback) {
            this.successCallback = Optional.of(callback);
            return this;
        }

        public TimedCommunication<T> build() {
            return new TimedCommunication<T>(this);
        }
    }
}
