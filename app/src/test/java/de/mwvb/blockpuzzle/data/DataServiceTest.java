package de.mwvb.blockpuzzle.data;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import de.mwvb.blockpuzzle.cluster.Cluster;
import de.mwvb.blockpuzzle.global.messages.MessageFactory;
import de.mwvb.blockpuzzle.global.messages.MessageObject;
import de.mwvb.blockpuzzle.planet.IPlanet;
import de.mwvb.blockpuzzle.planet.Planet;

public class DataServiceTest {
    private final MessageFactory messages = new MessageFactory(null);

    @Test
    public void pasteDataWithNoPlanets() {
        MessageObject result = ds().put("BP1/C98c/70yilx//enemy", messages);
        Assert.assertEquals(messages.getPutData_okay(), result);
    }

    private DataService ds() {
        Cluster cluster = new Cluster(98);
        Planet planet = new Planet(98, 1, 1);
        planet.setCluster(cluster);
        return new DataService() {
            @NotNull
            @Override
            protected IPlanet getPlanet() {
                return planet;
            }

            @NotNull
            @Override
            protected String getPlayerName() {
                return "test";
            }
        };
    }
}
//        if (result == messages.getNothingToInsert()) System.out.println("ist 1");
//        if (result == messages.getPutData_checksumMismatch()) System.out.println("ist 2");
//        if (result == messages.getPutData_formatError1()) System.out.println("ist 3");
//        if (result == messages.getPutData_formatError2()) System.out.println("ist 4");
//        if (result == messages.getPutData_makesNoSense()) System.out.println("ist 5");
//        if (result == messages.getPutData_okay()) System.out.println("ist 6");
//        if (result == messages.getPutData_success()) System.out.println("ist 7");
//        if (result == messages.getPutData_unknownCluster()) System.out.println("ist 8");
//        if (result == messages.getPutData_wrongPlanetData()) System.out.println("ist 9");
