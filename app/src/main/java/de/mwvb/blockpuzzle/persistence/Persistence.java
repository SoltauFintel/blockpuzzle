package de.mwvb.blockpuzzle.persistence;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.mwvb.blockpuzzle.Features;
import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.gravitation.GravitationData;
import de.mwvb.blockpuzzle.playingfield.PlayingField;
import de.mwvb.blockpuzzle.playingfield.QPosition;

// Ich möchte das Laden und Speichern an _einer_ Stelle haben, damit ich es schneller finden kann.
// Ordner: /data/data/YOUR_PACKAGE_NAME/shared_prefs/YOUR_PREFS_NAME.xml
public class Persistence implements IPersistence {
    private static final String NAME = "GAMEDATA_2";
    private static final String GAMEPIECEVIEW = "gamePieceView";
    private static final String PLAYINGFIELD = "playingField";
    private static final String SCORE = "score";
    private static final String HIGHSCORE = "highscore";
    private static final String HIGHSCORE_MOVES = "highscoreMoves";
    private static final String MOVES = "moves";
    private static final String GRAVITATION_ROWS = "gravitationRows";
    private static final String GRAVITATION_EXCLUSIONS = "gravitationExclusions";
    private static final String GRAVITATION_PLAYED_SOUND = "gravitationPlayedSound";

    private final ContextWrapper owner;
    private SharedPreferences __pref; // only access by pref() !
    private String prefix = "";

    // TODO wenn beim Laden etwas schief geht, muss ich gescheit reagieren. Das Spiel darf dann nicht bei jedem AppStart abkacken.
    // TODO Die Kommentare entfernen. Die sind mtlw. überholt. (22.10.20)

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

    @Override
    public void setGameMode(String gameMode) {
        if (Features.GAME_MODE_CLASSIC.equals(gameMode)) {
            prefix = "";
        } else {
            prefix = gameMode;
        }
    }

    private String name(String name) {
        return prefix + name;
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
        String d = getString(GAMEPIECEVIEW + index);
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
        String d = getString(PLAYINGFIELD);
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
        return getInt(SCORE, -9999);
    }

    /**
     * Es wird gespeichert wenn:
     * place (punkte erhöht), newGame (punkte=0)
     */
    @Override
    public void saveScore(int punkte) {
        putInt(SCORE, punkte);
    }

    @Override
    public int loadHighScore() {
        return getInt(HIGHSCORE, 0);
    }

    @Override
    public void saveHighScore(int punkte) {
        putInt(name(HIGHSCORE), punkte);
    }

    @Override
    public int loadHighScoreMoves() {
        return getInt(HIGHSCORE_MOVES, 0);
    }

    @Override
    public void saveHighScoreMoves(int moves) {
        putInt(HIGHSCORE_MOVES, moves);
    }

    @Override
    public int loadMoves() {
        return getInt(MOVES, 0);
    }

    @Override
    public void saveMoves(int moves) {
        putInt(MOVES, moves);
    }

    @Override
    public void load(GravitationData data) {
        data.clear();

        String d = getString(GRAVITATION_ROWS);
        if (!d.isEmpty()) {
            for (String w : d.split(",")) {
                data.getRows().add(Integer.parseInt(w));
            }
        }

        d = getString(GRAVITATION_EXCLUSIONS);
        if (!d.isEmpty()) {
            for (String w : d.split(",")) {
                String[] k = w.split("/");
                data.getExclusions().add(new QPosition(Integer.parseInt(k[0]), Integer.parseInt(k[1])));
            }
        }

        data.setFirstGravitationPlayed(pref().getBoolean(name(GRAVITATION_PLAYED_SOUND), false));
    }

    @Override
    public void save(GravitationData data) {
        StringBuilder d = new StringBuilder();
        String k = "";
        for (int y : data.getRows()) {
            d.append(k);
            k = ",";
            d.append("" + y);
        }
        save(GRAVITATION_ROWS, d);

        d = new StringBuilder();
        k = "";
        for (QPosition p : data.getExclusions()) {
            d.append(k);
            k = ",";
            d.append(p.getX() + "/" + p.getY());
        }
        save(GRAVITATION_ROWS, d);

        SharedPreferences.Editor edit = pref().edit();
        edit.putBoolean(name(GRAVITATION_PLAYED_SOUND), data.isFirstGravitationPlayed());
        edit.apply();
    }

    private String getString(String name) {
        return pref().getString(name(name), "");
    }

    private int getInt(String name, int defVal) {
        return pref().getInt(name(name), defVal);
    }

    private void putInt(String name, int val) {
        SharedPreferences.Editor edit = pref().edit();
        edit.putInt(name(name), val);
        edit.apply();
    }

    private void save(String name, StringBuilder s) {
        SharedPreferences.Editor edit = pref().edit();
        edit.putString(name(name), s.toString());
        edit.apply();
    }
}
