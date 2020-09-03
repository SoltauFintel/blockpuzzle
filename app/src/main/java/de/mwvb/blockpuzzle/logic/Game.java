package de.mwvb.blockpuzzle.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.mwvb.blockpuzzle.MainActivity;
import de.mwvb.blockpuzzle.logic.spielstein.Spielstein;
import de.mwvb.blockpuzzle.logic.spielstein.Spielstein2x3;
import de.mwvb.blockpuzzle.logic.spielstein.SpielsteinJ;
import de.mwvb.blockpuzzle.logic.spielstein.SpielsteinL;
import de.mwvb.blockpuzzle.logic.spielstein.SpielsteinS;
import de.mwvb.blockpuzzle.logic.spielstein.SpielsteinT;
import de.mwvb.blockpuzzle.logic.spielstein.Spielstein1;
import de.mwvb.blockpuzzle.logic.spielstein.Spielstein2;
import de.mwvb.blockpuzzle.logic.spielstein.Spielstein2x2;
import de.mwvb.blockpuzzle.logic.spielstein.Spielstein3;
import de.mwvb.blockpuzzle.logic.spielstein.Spielstein3x3;
import de.mwvb.blockpuzzle.logic.spielstein.Spielstein4;
import de.mwvb.blockpuzzle.logic.spielstein.Spielstein5;
import de.mwvb.blockpuzzle.logic.spielstein.SpielsteinEcke2;
import de.mwvb.blockpuzzle.logic.spielstein.SpielsteinEcke3;
import de.mwvb.blockpuzzle.logic.spielstein.SpielsteinZ;

public class Game {
    public static final int blocks = 10;
    private static final Random rand = new Random(System.currentTimeMillis());
    private final MainActivity view;
    private final Spielfeld spielfeld = new Spielfeld(blocks);
    private final List<Spielstein> teile = new ArrayList<>();
    private int punkte;
    private boolean gameOver = false;
    private boolean drehen = false;
    // TODO Bisher höchste Punktzahl persistieren.
    // TODO Drag Schatten anzeigen
    // TODO anderer Sound: Game over

    // Spielaufbau ----

    public Game(MainActivity activity) {
        view = activity;

        // Jede Spielsteinart standardmäßig 4x dabei.
        // Je nach Schwierigkeitsgrad wird das zum Teil abhängig von der Punktzahl variiert.

        // TODO Das mit der Mindestpunktzahl ist nicht so gut lesbar. Denkbar wäre auch noch eine Maximalpunktzahl.

//        teile.add(new Spielstein1());
//        teile.add(new Spielstein1());
//        teile.add(new Spielstein1());
//
//        teile.add(new Spielstein2());
//        teile.add(new Spielstein2().rotateToRight());
//        teile.add(new Spielstein2());
//
//        teile.add(new Spielstein3());
//        teile.add(new Spielstein3().rotateToRight());
//        teile.add(new Spielstein3());
//        teile.add(new Spielstein3().rotateToRight());
//
//        teile.add(new Spielstein4());
//        teile.add(new Spielstein4().rotateToRight());
//        teile.add(new Spielstein4());
//        teile.add(new Spielstein4().rotateToRight());

        teile.add(new Spielstein5());
//        teile.add(new Spielstein5().rotateToRight());
//        teile.add(new Spielstein5());
//        teile.add(new Spielstein5().rotateToRight());
//
//        teile.add(new SpielsteinEcke2());
//        teile.add(new SpielsteinEcke2().rotateToRight());
//        teile.add(new SpielsteinEcke2().rotateToRight().rotateToRight());
//        teile.add(new SpielsteinEcke2().rotateToLeft());
//
//        teile.add(new SpielsteinEcke3());
//        teile.add(new SpielsteinEcke3().rotateToRight());
//        teile.add(new SpielsteinEcke3().rotateToRight().rotateToRight());
//        teile.add(new SpielsteinEcke3().rotateToLeft());
//
//        // Bonus-Stein, seltener
//        teile.add(new SpielsteinJ().withMindestpunktzahl(1000));
//        // Bonus-Stein, seltener
//        teile.add(new SpielsteinL().withMindestpunktzahl(1000));
//
//        // schwieriger Stein, seltener
//        teile.add(new Spielstein2x2());
//        teile.add(new Spielstein2x2().withMindestpunktzahl(2000));
//
//        // schwieriger Stein, Bonus Stein, seltener, erst ab 3000 P.
//        teile.add(new Spielstein2x3().withMindestpunktzahl(2500));
//        teile.add(new Spielstein2x3().withMindestpunktzahl(3500).rotateToRight()); // ab 6000 P. kommt der Spielstein doppelt so oft => höherer Schwierigkeitsgrad
//
//        // Tetris S ab 4000 P.
//        teile.add(new SpielsteinS().withMindestpunktzahl(4000));
//        teile.add(new SpielsteinZ().withMindestpunktzahl(4000));
//
//        // schwieriger Stein, seltener
//        teile.add(new Spielstein3x3());
//        teile.add(new Spielstein3x3().withMindestpunktzahl(5000)); // ab 5000 P. kommt der Spielstein doppelt so oft => höherer Schwierigkeitsgrad
//        teile.add(new Spielstein3x3().withMindestpunktzahl(7000)); // ab 7000 P. kommt der Spielstein doppelt so oft => höherer Schwierigkeitsgrad
//
//        // Bonus Spielstein Mr. T ab 8000 P.
//        teile.add(new SpielsteinT().withMindestpunktzahl(8000));
//        teile.add(new SpielsteinT().withMindestpunktzahl(8000).rotateToRight());
//        teile.add(new SpielsteinT().withMindestpunktzahl(8000).rotateToRight().rotateToRight());
//        teile.add(new SpielsteinT().withMindestpunktzahl(8000).rotateToLeft());
    }

    // Neues Spiel ----

    public void newGame() {
        gameOver = false;
        punkte = 0;
        spielfeld.clear();

        view.updatePunkte(0);
        view.drawSpielfeld();
        vorschlag();
        view.setSpielstein(-1, null);
        // TODO Drehmodus deaktivieren
    }

    /** 3 neue zufällige Spielsteine anzeigen */
    private void vorschlag() {
        view.setSpielstein(1, createZufallsteil(teile));
        view.setSpielstein(2, createZufallsteil(teile));
        view.setSpielstein(3, createZufallsteil(teile));
    }

    private Spielstein createZufallsteil(List<Spielstein> teile) {
        int loop = 0;
        int index = rand.nextInt(teile.size());
        Spielstein spielstein = teile.get(index);
        while (punkte < spielstein.getMindestpunktzahl()) {
            if (++loop > 1000) { // Notausgang
                return teile.get(0);
            }
            index = rand.nextInt(teile.size());
            spielstein = teile.get(index);
        }
        return spielstein.copy();
    }

    // Spielaktionen ----

    /** Drop Aktion für Spielfeld oder Parking */
    public void dispatch(boolean targetIsParking, int index, Spielstein teil, float x, float y) {
        if (gameOver) {
            return;
        }
        boolean ret;
        if (targetIsParking) {
            ret = parke(index, teil);
        } else {
            ret = platziere(index, teil, (int) x, (int) y);
        }
        if (ret) {
            if (view.getSpielstein(1) == null && view.getSpielstein(2) == null && view.getSpielstein(3) == null) {
                vorschlag();
            }
            checkGame();
        } else {
            view.gehtNicht();
        }
    }

    /** Drop Aktion für Parking Area */
    private boolean parke(int index, Spielstein teil) {
        if (index != -1 && view.getSpielstein(-1) == null) { // es geht wenn Source 1,2,3 und Parking frei
            view.setSpielstein(-1, view.getSpielstein(index)); // Parking belegen
            view.setSpielstein(index, null); // Source leeren
            return true;
        }
        return false;
    }

    /**
     * Drop Aktion für Spielfeld
     * @return true wenn Spielstein platziert wurde, false wenn dies nicht möglich ist
     */
    private boolean platziere(int index, Spielstein teil, int x, int y) {
        final int punkteVorher = punkte;
        QPosition pos = new QPosition(x, y);
        boolean ret = spielfeld.match(teil, pos);
        if (ret) {
            spielfeld.platziere(teil, pos);
            view.drawSpielfeld();
            view.setSpielstein(index, null);

            // Gibt es gefüllte Rows?
            FilledRows f = spielfeld.getFilledRows();

            // Punktzahl erhöhen
            punkte += teil.getPunkte() + 10 * f.getTreffer();
            rowsAdditionalBonus(f.getTreffer());

            view.clearRows(f, getGravityAction(f)); // Wird erst wenige Millisekunden später fertig!
            spielfeld.clearRows(f);
            if (f.getTreffer() > 0) {
                wenigeSpielsteineAufSpielfeld();
            }
            view.updatePunkte(punkte - punkteVorher);
        }
        return ret;
    }

    private Action getGravityAction(FilledRows f) {
        return () -> {
            for (int i = 4; i >= 1; i--) {
                if (f.getYlist().contains(blocks - i)) {
                    // Row war voll und wurde geleert -> Gravitation auslösen
                    spielfeld.gravitation(blocks - i);
                    view.drawSpielfeld();
                }
            }
        };
    }

    private void rowsAdditionalBonus(int rows) {
        switch (rows) {
            case 0:
            case 1: break; // 0-1 kein Bonus
            // Bonuspunkte wenn mehr als 2 Rows gleichzeitig abgeräumt werden.
            // Fällt mir etwas schwer zu entscheiden wieviel Punkte das jeweils wert ist.
            case 2: punkte += 12; break;
            case 3: punkte += 15; break;
            default: /* >= 4 */ punkte += 22; break;
        }
    }

    private void wenigeSpielsteineAufSpielfeld() {
        // Es gibt einen Bonus, wenn nach dem Abräumen von Rows nur noch wenige Spielsteine
        // auf dem Spielfeld sind. 1-2 ist nicht einfach, 0 fast unmöglich.
        int bonus = 0;
        switch (spielfeld.getGefuellte()) {
            case 0: bonus = 444; break; // Wahnsinn!
            case 1: bonus = 111; break;
            case 2: bonus = 30; break;
            case 3: bonus = 15; break;
            case 4: bonus = 4; break;
        }
        if (bonus > 0) {
            punkte += bonus;
        }
    }

    private void checkGame() {
        // es muss ein Spielstein noch rein gehen
        boolean a = moveImpossible(1);
        boolean b = moveImpossible(2);
        boolean c = moveImpossible(3);
        boolean d = moveImpossible(-1);
        if (a && b && c && d && view.getSpielstein(-1) != null) {
            gameOver = true;
            view.updatePunkte(0);
            view.drawSpielfeld(); // wenn parke die letzte Aktion war
        }
    }

    public boolean moveImpossible(int index) {
        Spielstein teil = view.getSpielstein(index);
        if (teil == null) {
            return true; // TeilView ist leer
        }
        for (int x = 0; x < blocks; x++) {
            for (int y = 0; y < blocks; y++) {
                if (spielfeld.match(teil, new QPosition(x, y))) {
                    view.grey(index, false);
                    return false; // Spielstein passt rein
                }
            }
        }
        view.grey(index, true);
        return true; // Spielstein passt nirgendwo rein
    }

    public int getPunkte() {
        return punkte;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int get(int x, int y) {
        return spielfeld.get(x, y);
    }

    public boolean toggleDrehmodus() {
        drehen = !drehen;
        return drehen;
    }
}
