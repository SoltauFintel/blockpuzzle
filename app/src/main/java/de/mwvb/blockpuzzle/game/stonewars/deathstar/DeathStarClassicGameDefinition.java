package de.mwvb.blockpuzzle.game.stonewars.deathstar;

import java.util.Random;

import de.mwvb.blockpuzzle.game.GameEngineBuilder;
import de.mwvb.blockpuzzle.gamedefinition.ClassicGameDefinition;
import de.mwvb.blockpuzzle.global.Features;
import de.mwvb.blockpuzzle.global.messages.MessageFactory;
import de.mwvb.blockpuzzle.global.messages.MessageObjectWithGameState;
import de.mwvb.blockpuzzle.playingfield.PlayingField;

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

    public void setWon(boolean pWon) {
        won = pWon;
    }

    @Override
    public void fillStartPlayingField(PlayingField p) {
        Random random = new Random();
        int max = 20;
        if (Features.developerMode) {
            max = 3;
        }
        for (int i = 0; i < max; i++) {
            int x = random.nextInt(GameEngineBuilder.blocks);
            int y = random.nextInt(GameEngineBuilder.blocks);
            int c = random.nextInt(7) + 1;
            p.set(x, y, c);
        }
    }
}
