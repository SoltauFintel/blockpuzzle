package de.mwvb.blockpuzzle.game.stonewars.deathstar;

import de.mwvb.blockpuzzle.gamedefinition.ClassicGameDefinition;
import de.mwvb.blockpuzzle.messages.MessageFactory;
import de.mwvb.blockpuzzle.messages.MessageObjectWithGameState;

public class DeathStarClassicGameDefinition extends ClassicGameDefinition {
    private boolean won = false; // TO-DO persistieren

    public DeathStarClassicGameDefinition(int gamePieceSetNumber, int minimumLiberationScore, int name) {
        super(gamePieceSetNumber, minimumLiberationScore);
        setTerritoryName(name);
    }

    @Override
    protected MessageObjectWithGameState getPlanetLiberatedText(MessageFactory messages) {
        won = true;
        return messages.getDeathStarDestroyed();
    }

    @Override
    protected MessageObjectWithGameState getTerritoryLiberatedText(MessageFactory messages) {
        won = true;
        return messages.getReactorDestroyed();
    }

    public boolean isWon() {
        return won;
    }
}
