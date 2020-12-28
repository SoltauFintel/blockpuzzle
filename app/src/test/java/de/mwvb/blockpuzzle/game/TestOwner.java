package de.mwvb.blockpuzzle.game;

import org.junit.Assert;

import java.util.List;

import de.mwvb.blockpuzzle.gamedefinition.ClassicGameDefinition;
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;
import de.mwvb.blockpuzzle.gamedefinition.ResourceAccess;
import de.mwvb.blockpuzzle.persistence.GamePersistence;
import de.mwvb.blockpuzzle.persistence.IPersistence;
import de.mwvb.blockpuzzle.planet.GiantPlanet;
import de.mwvb.blockpuzzle.planet.IPlanet;

public class TestOwner {

    @org.junit.Test
    public void liberateMultiClassicGamePlanet() {
        // Prepare
        ClassicGameDefinition g1 = new ClassicGameDefinition(1, 50);
        ClassicGameDefinition g2 = new ClassicGameDefinition(2, 1000);
        ClassicGameDefinition g3 = new ClassicGameDefinition(2, 50);
        IPlanet planet = new GiantPlanet(17, 1, 1, g1, g2, g3);
        IPersistence per = new TestPersistence();

        planet.setSelectedGame(planet.getGameDefinitions().get(0));
        per.setGameID(planet);
        per.saveScore(50);
        per.saveMoves(10);

        planet.setSelectedGame(planet.getGameDefinitions().get(1));
        per.setGameID(planet);
        per.saveScore(51);
        per.saveMoves(11);

        planet.setSelectedGame(planet.getGameDefinitions().get(2));
        per.setGameID(planet);
        per.saveScore(52);
        per.saveMoves(12);

        // Test
        // Wenn man speichert klappt's.
        planet.setSelectedGame(planet.getGameDefinitions().get(1));
        per.setGameID(planet);
        per.saveScore(1000);
        per.saveMoves(100);

        String ret = planet.getGameDefinitions().get(1).scoreChanged(1000, 100, planet, false,
                new GamePersistence(per, new TestGameView()),
                resId -> "RES" + resId);

        // Verify
        Assert.assertEquals("Planet must be liberated!", "RES2131624024", ret);
//        per.loadPlanet(planet);
        Assert.assertTrue("Planet owner flag not set!", planet.isOwner());
    }
}
