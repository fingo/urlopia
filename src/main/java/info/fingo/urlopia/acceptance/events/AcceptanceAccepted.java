package info.fingo.urlopia.acceptance.events;

import info.fingo.urlopia.acceptance.Acceptance;

public class AcceptanceAccepted {

    private final Acceptance acceptance;

    public AcceptanceAccepted(Acceptance acceptance) {
        this.acceptance = acceptance;
    }

    public Acceptance getAcceptance() {
        return acceptance;
    }
}
