package de.mwvb.blockpuzzle.gamestate;

import de.mwvb.blockpuzzle.global.messages.MessageFactory;
import de.mwvb.blockpuzzle.planet.IPlanet;
import de.mwvb.blockpuzzle.planet.SpaceObjectStateService;

/**
 * Das ist ein Objekt, welches alle Daten und Services für die GameDefinition.scoreChanged() Methode anbietet.
 * Evtl. nur vorläufig, bis ich den GameDefinition-Klassen mehr GameEngine-Macht gegeben habe.
 */
public class ScoreChangeInfo {
    private final StoneWarsGameState gs;
    private final MessageFactory messages;
    private final boolean won;

    public ScoreChangeInfo(StoneWarsGameState gs, MessageFactory messages) {
        this.gs = gs;
        this.messages = messages;
        won = gs.get().getState() == GamePlayState.WON_GAME;
    }

    // READ-ONLY DATA ACCESS ----

    public int getScore() {
        return gs.get().getScore();
    }

    public int getMoves() {
        return gs.get().getMoves();
    }

    public IPlanet getPlanet() {
        return gs.getPlanet();
    }

    public boolean isWon() {
        return won;
    }

    public MessageFactory getMessages() {
        return messages;
    }

    // EXTRA DATA ----

    public int getOwnerScore() {
        return gs.get().getOwnerScore();
    }

    // SERVICES ----

    /** Gegner geschlagen */
    public void clearOwner() {
        Spielstand ss = gs.get();
        ss.setOwnerName("");
        ss.setOwnerScore(0);
        ss.setOwnerMoves(0);
        gs.save();
    }

    /** wenn true: Planet komplett befreit */
    public void saveOwner(boolean owner) {
        new SpaceObjectStateService().saveOwner(gs.getPlanet(), owner);
    }

    public void saveScoreAndMoves() {
        gs.save();
    }

    public void saveDailyDate(String date) {
        gs.get().setDailyDate(date);
        gs.save();
    }
}
