package de.mwvb.blockpuzzle.deathstar;

import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.gamedefinition.ClassicGameDefinition;
import de.mwvb.blockpuzzle.gamedefinition.ResourceAccess;

public class DeathStarClassicGameDefinition extends ClassicGameDefinition {
    private boolean won = false; // TODO persistieren

    public DeathStarClassicGameDefinition(int gamePieceSetNumber, int minimumLiberationScore, int name) {
        super(gamePieceSetNumber, minimumLiberationScore);
        setTerritoryName(name);
    }

    @Override
    protected String getPlanetLiberatedText(ResourceAccess resources) {
        won = true;
        return resources.getString(R.string.deathStarDestroyed);
    }

    @Override
    protected String getTerritoryLiberatedText(ResourceAccess resources) {
        won = true;
        return resources.getString(R.string.reactorDestroyed);
    }

    public boolean isWon() {
        return won;
    }
}
