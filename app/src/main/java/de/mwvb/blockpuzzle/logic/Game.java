package de.mwvb.blockpuzzle.logic;

import android.content.SharedPreferences;

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
    private final PlayingField playingField = new PlayingField(blocks);
    private final List<Spielstein> teile = new ArrayList<>();
    private int punkte;
    private boolean gameOver = false;
    private boolean drehen = false; // wird nicht persistiert
    private SharedPreferences pref;
    // TODO Bisher höchste Punktzahl persistieren.
    // TODO Drag Schatten anzeigen
    // TODO anderer Sound: Game over
    // TODO Denkbar wäre auch noch eine Maximalpunktzahl.

    // Spielaufbau ----

    public Game(MainActivity activity) {
        view = activity;

        // Jede Spielsteinart standardmäßig 4x dabei.
        // Je nach Schwierigkeitsgrad wird das zum Teil abhängig von der Punktzahl variiert.

        teile.add(new Spielstein1());
        teile.add(new Spielstein1());
        teile.add(new Spielstein1());

        teile.add(new Spielstein2());
        teile.add(new Spielstein2().rotateToRight());
        teile.add(new Spielstein2());

        teile.add(new Spielstein3());
        teile.add(new Spielstein3().rotateToRight());
        teile.add(new Spielstein3());
        teile.add(new Spielstein3().rotateToRight());

        teile.add(new Spielstein4());
        teile.add(new Spielstein4().rotateToRight());
        teile.add(new Spielstein4());
        teile.add(new Spielstein4().rotateToRight());
        teile.add(new Spielstein4().withMindestpunktzahl(10000));

        teile.add(new Spielstein5());
        teile.add(new Spielstein5().rotateToRight());
        teile.add(new Spielstein5());

        teile.add(new SpielsteinEcke2());
        teile.add(new SpielsteinEcke2().rotateToRight());
        teile.add(new SpielsteinEcke2().rotateToRight().rotateToRight());
        teile.add(new SpielsteinEcke2().rotateToLeft());

        teile.add(new SpielsteinEcke3());
        teile.add(new SpielsteinEcke3().rotateToRight());
        teile.add(new SpielsteinEcke3().rotateToRight().rotateToRight());
        teile.add(new SpielsteinEcke3().rotateToLeft());
        teile.add(new SpielsteinEcke3().withMindestpunktzahl(11000));
        teile.add(new SpielsteinEcke3().withMindestpunktzahl(25000).rotateToRight());

        // Bonus-Stein, seltener
        teile.add(new SpielsteinJ().withMindestpunktzahl(1000));
        // Bonus-Stein, seltener
        teile.add(new SpielsteinL().withMindestpunktzahl(1000));

        // schwieriger Stein, seltener
        teile.add(new Spielstein2x2());
        teile.add(new Spielstein2x2().withMindestpunktzahl(2000));

        // schwieriger Stein, Bonus Stein, seltener, erst ab 3000 P.
        teile.add(new Spielstein2x3().withMindestpunktzahl(2500));
        teile.add(new Spielstein2x3().withMindestpunktzahl(3500).rotateToRight()); // ab 6000 P. kommt der Spielstein doppelt so oft => höherer Schwierigkeitsgrad

        // Tetris S ab 4000 P.
        teile.add(new SpielsteinS().withMindestpunktzahl(4000));
        teile.add(new SpielsteinZ().withMindestpunktzahl(4000));

        // schwieriger Stein, seltener
        teile.add(new Spielstein3x3());
        teile.add(new Spielstein3x3().withMindestpunktzahl(5000)); // ab 5000 P. kommt der Spielstein doppelt so oft => höherer Schwierigkeitsgrad
        teile.add(new Spielstein3x3().withMindestpunktzahl(7000)); // ab 7000 P. kommt der Spielstein öfter => höherer Schwierigkeitsgrad
        teile.add(new Spielstein3x3().withMindestpunktzahl(9000)); // ab 9000 P. kommt der Spielstein öfter => höherer Schwierigkeitsgrad
        teile.add(new Spielstein3x3().withMindestpunktzahl(20000)); // ab 20k P. kommt der Spielstein öfter => höherer Schwierigkeitsgrad

        // Bonus Spielstein Mr. T ab 8000 P.
        teile.add(new SpielsteinT().withMindestpunktzahl(8000));
        teile.add(new SpielsteinT().withMindestpunktzahl(8000).rotateToRight());
        teile.add(new SpielsteinT().withMindestpunktzahl(8000).rotateToRight().rotateToRight());
        teile.add(new SpielsteinT().withMindestpunktzahl(8000).rotateToLeft());
    }

    public void setStorage(SharedPreferences pref) {
        this.pref = pref;
        playingField.setStorage(pref);
    }

    // Neues Spiel ----

    public void initGame() {
        view.setSpielstein(-1, null, false);

        // Drehmodus deaktivieren
        drehen = false;
        view.drehmodusAus();

        // Gibt es einen Spielstand?
        punkte = pref.getInt("punkte", -9999);
        if (punkte < 0) { // Nein -> Neues Spiel starten!
            newGame();
            return;
        }
        // Es gibt einen Spielstand.
        playingField.read();
        view.updatePunkte(0);
        view.drawSpielfeld();
        view.restoreSpielsteinViews();
        checkGame();
    }

    /** Benutzer startet freiwillig oder nach GameOver neues Spiel. */
    public void newGame() {
        playingField.clear(true);
        gameOver = false;
        punkte = 0;
        savePunkte();
        view.updatePunkte(0);

        view.drawSpielfeld();
        view.setSpielstein(-1, null, true);
        vorschlag();
    }

    /** 3 neue zufällige Spielsteine anzeigen */
    private void vorschlag() {
        view.setSpielstein(1, createZufallsteil(teile), true);
        view.setSpielstein(2, createZufallsteil(teile), true);
        view.setSpielstein(3, createZufallsteil(teile), true);
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
    public void dispatch(boolean targetIsParking, int index, Spielstein teil, QPosition xy) {
        if (gameOver) {
            return;
        }
        boolean ret;
        if (targetIsParking) {
            ret = parke(index, teil);
        } else {
            ret = platziere(index, teil, xy);
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
            view.setSpielstein(-1, view.getSpielstein(index), true); // Parking belegen
            view.setSpielstein(index, null, true); // Source leeren
            return true;
        }
        return false;
    }

    /**
     * Drop Aktion für Spielfeld
     * @return true wenn Spielstein platziert wurde, false wenn dies nicht möglich ist
     */
    private boolean platziere(int index, Spielstein teil, QPosition pos) {
        final int punkteVorher = punkte;
        boolean ret = playingField.match(teil, pos);
        if (ret) {
            playingField.platziere(teil, pos);
            view.drawSpielfeld();
            view.setSpielstein(index, null, true);

            // Gibt es gefüllte Rows?
            FilledRows f = playingField.getFilledRows();

            // Punktzahl erhöhen
            punkte += teil.getPunkte() + 10 * f.getTreffer();
            rowsAdditionalBonus(f.getTreffer());

            view.clearRows(f, getGravityAction(f)); // Wird erst wenige Millisekunden später fertig!
            playingField.clearRows(f);
            if (f.getTreffer() > 0) {
                wenigeSpielsteineAufSpielfeld();
            }
            view.updatePunkte(punkte - punkteVorher);
            savePunkte();
        }
        return ret;
    }

    private Action getGravityAction(FilledRows f) {
        return () -> {
            for (int i = 5; i >= 1; i--) {
                if (f.getYlist().contains(blocks - i)) {
                    // Row war voll und wurde geleert -> Gravitation auslösen
                    playingField.gravitation(blocks - i);
                    view.drawSpielfeld();
                }
            }
            moveImpossible(1);
            moveImpossible(2);
            moveImpossible(3);
            moveImpossible(-1);
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
        switch (playingField.getGefuellte()) {
            case 0: bonus = 444; break; // Wahnsinn!
            case 1: bonus = 111; break;
            case 2: bonus = 60; break;
            case 3: bonus = 30; break;
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
                if (playingField.match(teil, new QPosition(x, y))) {
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
        return playingField.get(x, y);
    }

    public boolean toggleDrehmodus() {
        drehen = !drehen;
        return drehen;
    }

    private void savePunkte() {
        if (pref != null) {
            SharedPreferences.Editor edit = pref.edit();
            edit.putInt("punkte", punkte);
            edit.putString("version", "0.1");
            edit.apply();
            System.out.println("saved punkte: " + punkte);
        }
    }
}
