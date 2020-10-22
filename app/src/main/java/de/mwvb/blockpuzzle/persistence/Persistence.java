package de.mwvb.blockpuzzle.persistence;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.playingfield.PlayingField;

// Ich möchte das Laden und Speichern an _einer_ Stelle haben, damit ich es schneller finden kann.
// Ordner: /data/data/YOUR_PACKAGE_NAME/shared_prefs/YOUR_PREFS_NAME.xml
public class Persistence implements IPersistence {
    private static final String NAME = "GAMEDATA_2";
    private static final String GAMEPIECEVIEW = "gamePieceView";
    private static final String PLAYINGFIELD = "playingField";
    private static final String SCORE = "score";
    private static final String MOVES = "moves";

    private final ContextWrapper owner;
    private SharedPreferences __pref; // only access by pref() !

    // TODO wenn beim Laden etwas schief geht, muss ich gescheit reagieren. Das Spiel darf dann nicht bei jedem AppStart abkacken.

    public Persistence(ContextWrapper owner) {
        this.owner = owner;
        // Ich kann hier nicht sofort auf getSharedPreferences() zugreifen.
    }

    private SharedPreferences pref() {
        if (__pref == null) {
            __pref = owner.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        }
        return __pref;
    }
    
    /**
     * Es wird gespeichert wenn:
     * neues Spiel (empty parking), offer (set), place (empty), parken (move)
     */
    @Override
    public void save(int index, GamePiece p) {
        StringBuilder d = new StringBuilder();
        if (p != null) {
            String k = "";
            for (int x = 0; x < GamePiece.max; x++) {
                for (int y = 0; y < GamePiece.max; y++) {
                    d.append(k);
                    k = ",";
                    d.append(p.getBlockType(x, y));
                }
            }
        }
        save(GAMEPIECEVIEW + index, d);
    }

    /**
     * Es wird geladen wenn:
     * MainActivity.onCreate
     */
    @Override
    public GamePiece load(int index) {
        GamePiece p = null;
        String d = pref().getString(GAMEPIECEVIEW + index, null);
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
        return p;
    }

    /**
     * Es wird gespeichert wenn:
     * clear (newGame), place, gravitation (place), clearRows (place)
     */
    @Override
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
    @Override
    public void load(PlayingField f) {
        String d = pref().getString(PLAYINGFIELD, null);
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
    @Override
    public int loadScore() {
        return pref().getInt(SCORE, -9999);
    }

    /**
     * Es wird gespeichert wenn:
     * place (punkte erhöht), newGame (punkte=0)
     */
    @Override
    public void saveScore(int punkte) {
        SharedPreferences.Editor edit = pref().edit();
        edit.putInt(SCORE, punkte);
        edit.apply();
    }

    @Override
    public int loadMoves() {
        return pref().getInt(MOVES, 0);
    }

    @Override
    public void saveMoves(int moves) {
        SharedPreferences.Editor edit = pref().edit();
        edit.putInt(MOVES, moves);
        edit.apply();
    }

    private void save(String name, StringBuilder s) {
        SharedPreferences.Editor edit = pref().edit();
        edit.putString(name, s.toString());
        edit.apply();
    }
}
