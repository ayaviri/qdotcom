package chen_ayaviri.server;

import java.util.function.Supplier;

// Represents the absence of a punishment. Is used for instances of
// TimedCommunication objects that don't have a punishment to dish out
public class PunishmentAbsence implements Supplier<Void> {

    public Void get() {
        return null;
    }
}
