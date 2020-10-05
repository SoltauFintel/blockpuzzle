package de.mwvb.blockpuzzle.logic;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

import de.mwvb.blockpuzzle.logic.spielstein.GamePiece;
import de.mwvb.blockpuzzle.view.GamePieceView;

// Ich möchte das Laden und Speichern an _einer_ Stelle haben, damit ich es schneller finden kann.
// Ordner: /data/data/YOUR_PACKAGE_NAME/shared_prefs/YOUR_PREFS_NAME.xml
public class Persistence {
    private static final String NAME = "GAMEDATA_2";
    private static final String GAMEPIECEVIEW = "gamePieceView";
    private static final String PLAYINGFIELD = "playingField";
    private static final String SCORE = "score";
    private static final String MOVES = "moves";
    private final SharedPreferences pref;
    // TODO wenn beim Laden etwas schief geht, muss ich gescheit reagieren. Das Spiel darf dann nicht bei jedem AppStart abkacken.

    public Persistence(ContextWrapper owner) {
        pref = owner.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    /**
     * Es wird gespeichert wenn:
     * neues Spiel (empty parking), offer (set), place (empty), parken (move)
     */
    public void save(GamePieceView v) {
        StringBuilder d = new StringBuilder();
        String k = "";
        GamePiece p = v.getGamePiece(); // TODO kann das null sein?
        if (p != null) {
            for (int x = 0; x < GamePiece.max; x++) {
                for (int y = 0; y < GamePiece.max; y++) {
                    d.append(k);
                    k = ",";
                    d.append(p.getBlockType(x, y));
                }
            }
        }
        save(GAMEPIECEVIEW + v.getIndex(), d);
    }

    /**
     * Es wird geladen wenn:
     * MainActivity.onCreate
     */
    public void load(GamePieceView v) {
        GamePiece p = null;
        String d = pref.getString(GAMEPIECEVIEW + v.getIndex(), null);
        if (d != null && !d.isEmpty()) {
            p = new GamePiece();
            String[] w = d.split(",");
            int i = 0;
            for (int x = 0; x < GamePiece.max; x++) {
                for (int y = 0; y < GamePiece.max; y++) {
                    p.setBlockType(x, y, Integer.parseInt(w[i++]));
                }
            }
        }
        v.setGamePiece(p);
    }

    /**
     * Es wird gespeichert wenn:
     * clear (newGame), place, gravitation (place), clearRows (place)
     */
    public void save(PlayingField f) {
        StringBuilder d = new StringBuilder();
        String k = "";
        final int blocks = f.getBlocks();
        for (int x = 0; x < blocks; x++) {
            for (int y = 0; y < blocks; y++) {
                d.append(k);
                k = ",";
                d.append(f.get(x, y));
            }
        }
        save(PLAYINGFIELD, d);
    }

    /**
     * Es wird geladen wenn:
     * MainActivity.onCreate
     */
    public void load(PlayingField f) {
        String d = pref.getString(PLAYINGFIELD, null);
        final int blocks = f.getBlocks();
        if (d == null || d.isEmpty()) {
            for (int x = 0; x < blocks; x++) {
                for (int y = 0; y < blocks; y++) {
                    f.set(x, y, 0);
                }
            }
        } else {
            String[] w = d.split(",");
            int i = 0;
            for (int x = 0; x < blocks; x++) {
                for (int y = 0; y < blocks; y++) {
                    f.set(x, y, Integer.parseInt(w[i++]));
                }
            }
        }
    }

    /**
     * Es wird geladen wenn:
     * MainActivity.onCreate
     * @return negative value means that there is no GAMEDATA
     */
    public int loadScore() {
        return pref.getInt(SCORE, -9999);
    }

    /**
     * Es wird gespeichert wenn:
     * place (punkte erhöht), newGame (punkte=0)
     */
    public void saveScore(int punkte) {
        if (pref != null) {
            SharedPreferences.Editor edit = pref.edit();
            edit.putInt(SCORE, punkte);
            edit.apply();
        }
    }

    public int loadMoves() {
        return pref.getInt(MOVES, 0);
    }

    public void saveMoves(int moves) {
        if (pref != null) {
            SharedPreferences.Editor edit = pref.edit();
            edit.putInt(MOVES, moves);
            edit.apply();
        }
    }

    private void save(String name, StringBuilder s) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(name, s.toString());
        edit.apply();
    }
}
