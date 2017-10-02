package info.fingo.urlopia.acceptance.events;

import info.fingo.urlopia.acceptance.Acceptance;

public class AcceptanceRejected {

    private final Acceptance acceptance;

    public AcceptanceRejected(Acceptance acceptance) {
        this.acceptance = acceptance;
    }

    public Acceptance getAcceptance() {
        return acceptance;
    }
}
