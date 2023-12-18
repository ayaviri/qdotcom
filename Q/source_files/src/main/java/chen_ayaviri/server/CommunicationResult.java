package chen_ayaviri.server;

import java.util.Optional;

// Represents the result of a communication between a local and remote
// object, which encapsulates the success of the communication and the 
// possible return value of the communication
// 
// IMPORTANT: This class separates these two concerns as the represented 
// communication can succeed but not return anything, and Java does not
// allow present Optional<Void> objects. As such, presence of an Optional
// cannot be used to determine the success of a communication for callables
// that don't return anything
public class CommunicationResult<T> {
    private final boolean hasSucceeded;
    private final Optional<T> returnValue;
   
    // Creates a result with a return value that indicates 
    // communication SUCCESS
    public CommunicationResult(T returnValue) {
        this.hasSucceeded = true;
        // TODO: ask how this can be avoided, if it should
        this.returnValue = Optional.ofNullable(returnValue);
    }

    // Creates a result that indicates communication FAILURE
    public CommunicationResult() {
        this.hasSucceeded = false;
        this.returnValue = Optional.empty();
    }

    public boolean hasSucceeded() {
        return this.hasSucceeded;
    }

    // Gets the result of the communication (if there is any) wrapped 
    // around an Optional object
    // NOTE: The return value WILL BE PRESENT if:
    //  - if the communication has succeeded (should not be called
    //  otherwise and return value will have no meaning case of communication failure)
    //  - the generic type of this class is NOT Void (see class interpretation statement)
    public Optional<T> returnValue(){
        return this.returnValue;
    }
}
