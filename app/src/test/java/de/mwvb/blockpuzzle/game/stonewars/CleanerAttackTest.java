package de.mwvb.blockpuzzle.game.stonewars;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.block.BlockTypes;
import de.mwvb.blockpuzzle.data.DataService;
import de.mwvb.blockpuzzle.game.PersistenceNoOp;
import de.mwvb.blockpuzzle.game.TestGameBuilder;
import de.mwvb.blockpuzzle.game.TestGameState;
import de.mwvb.blockpuzzle.game.TestPersistence;
import de.mwvb.blockpuzzle.gamepiece.GamePieceHolder;
import de.mwvb.blockpuzzle.persistence.IPersistence;
import de.mwvb.blockpuzzle.planet.IPlanet;
import de.mwvb.blockpuzzle.playingfield.QPosition;

/**
 * Spieler und Gegner haben beim Cleaner Game die gleiche Anzahl Punkte und die gleiche Anzahl Moves.
 */
// TODO DataService muss testfähig aufgebaut werden. Viele Sachen wegkapseln. Und dann einen reinen Unit Test schreiben.
public class CleanerAttackTest {
    private BlockTypes blockTypes = new BlockTypes(null);
    private TestStoneWarsGame game;
    private boolean display = false;

    /**
     * Schritt 1: Spieler gewinnt Cleaner Planet.
     * Schritt 2: Gegnerdaten werden eingefügt. Gegner hat gleiche Score&Moves.
     * SOLL: Spieler gewinnt. (Gegner übernimmt den Planeten nicht.)
     */
    @Test
    public void test2() {
        // Neues Stone Wars starten und Planet 17 bis zum Sieg bespielen.
        initNewGame();
        playGame();

        // Jetzt kommen Daten vom Gegner rein, der exakt gleich gut ist.
        String data = "BP1/C1c" + DataService.buildString(17, 0, game.getScore(), game.getMoves());
        data += "/" + DataService.code6(data) + "//Enemy1";

        // Verification for the following test: Gegner hat nicht gewonnen. Player ist weiterhin der Liberator des Planeten.
        game.getExpectedResources().add(R.string.putData_okay); // Gegner konnte keine Planeten befreien

        // Test
        new DataService().put(data, game.getGape().get(), game.getResourceAccess());

        // Further verifications
        Assert.assertEquals("No enemy score must be saved", 0, game.getGape().get().loadOwnerScore());
        Assert.assertTrue("Player must still be the liberator of the planet", game.getPlanet().isOwner());
    }

    private void initNewGame() {
        IPersistence persistence = new TestPersistence();
        game = TestGameBuilder.createStoneWarsGame(persistence);
        Assert.assertEquals("Wrong planet!", 17, game.getPlanet().getNumber());
        Assert.assertEquals("Wrong GamePieceSet!", 4, game.getDefinition().getGamePieceSetNumber());
        Assert.assertTrue("Not the expected game!", game.getDefinition().getInfo().contains("Cleaner Game L1"));
        Assert.assertTrue("Playing field not ok!", TestGameBuilder.getPlayingFieldAsString(game).startsWith(".5......5."));
        Assert.assertFalse("GamePieces not ready!", game.getHolders().is123Empty());
        display();
    }

    private void playGame() {
        /*r[0] = "#5#3#4";
        r[1] = "#3#2x2#2";
        r[2] = "#2#3#2";
        r[3] = "#1#1#4";*/

        // Runde 1
        play(2, 0, 2, 0);
        play(1, 1, 9, 5);
        play(3, 1, 9, 2);

        // Runde 2
        play(3, 1, 9, 0);
        parke(2);
        Assert.assertEquals("Parken zählt nicht als Move!", 4, game.getMoves());
        Assert.assertFalse(game.getHolders().isParkingFree());
        play(1, 0, 5, 1);

        // Runde 3
        play(1, 1, 0, 1);
        play(3, 1, 9, 1);
        play(2, 0, 1, 2);

        // Runde 4
        play(3, 0, 4, 2);
        Assert.assertFalse(game.isGameOver());
        Assert.assertFalse(game.isWon());
        Assert.assertFalse(game.getPlanet().isOwner());

        game.getExpectedResources().add(R.string.planetLiberated);
        play(1, 0, 8, 2); // Mit diesem Move siegt man.

        // Spiel gewonnen?
        Assert.assertTrue(game.isGameOver());
        Assert.assertTrue(game.isWon());
        Assert.assertTrue(game.getPlanet().isOwner());
        Assert.assertEquals(10, game.getMoves());
    }

    private void play(int index, int rotate, int x, int y) {
        GamePieceHolder h = game.getHolders().get(index);
        for (int i = 1; i <= rotate; i++) {
            h.rotate();
        }
        if (display) {
            System.out.println("PLAY H" + index + " at " + x + "," + y + (rotate > 0 ? (", rotate " + rotate + "x") : "") + "\n");
        }
        game.dispatch(false, index, h.getGamePiece(), new QPosition(x, y));
        display();
    }

    private void parke(int index) {
        if (display) {
            System.out.println("PARKE H" + index + "\n");
        }
        game.dispatch(true, index, null, null);
        display();
    }

    private void display() {
        if (!display) return;
        System.out.println(TestGameBuilder.getPlayingFieldAsString(game));
        char[][] matrix = new char[5][80];
        for (int i = 1; i <= 3; i++) {
            if (game.getHolders().get(i).getGamePiece() != null) {
                String u = TestGameBuilder.getStringPresentation(game.getHolders().get(i).getGamePiece(), blockTypes, true);
                int startXX = (i - 1) * 10;
                int xx = startXX, yy = 0;
                for (int j = 0; j < u.length(); j++) {
                    char c = u.charAt(j);
                    if (c == '\n') {
                        xx = startXX;
                        yy++;
                    } else {
                        matrix[yy][xx] = c;
                        xx++;
                    }
                }
            }
        }
        String a = "";
        for (int yy = 0; yy < 5; yy++) {
            for (int xx = 0; xx < 80; xx++) {
                a += matrix[yy][xx];
            }
            a += "\n";
        }
        System.out.println("H1        H2        H3        " + (game.getHolders().isParkingFree() ? "" : "P=belegt"));
        System.out.println(a + "-----------------------------------------------------------");
    }

    /**
     * Schritt 1: Gegnerdaten werden eingespielt. Gegner ist Liberator vom Cleaner Planet.
     * Schritt 2: Spieler spielt diesen Cleaner Planet und hat gleiche Score&Moves.
     * SOLL: Spieler gewinnt.
     */
//    @Test
    public void test1() {
    }
}
