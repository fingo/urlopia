package info.fingo.urlopia.acceptance.events;

import info.fingo.urlopia.acceptance.Acceptance;

public class AcceptanceCreated {

    private final Acceptance acceptance;

    public AcceptanceCreated(Acceptance acceptance) {
        this.acceptance = acceptance;
    }

    public Acceptance getAcceptance() {
        return acceptance;
    }
}
