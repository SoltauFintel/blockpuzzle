package de.mwvb.blockpuzzle.persistence;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

import java.util.Random;

import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.gravitation.GravitationData;
import de.mwvb.blockpuzzle.planet.IPlanet;
import de.mwvb.blockpuzzle.playingfield.PlayingField;
import de.mwvb.blockpuzzle.playingfield.QPosition;

// Ich möchte das Laden und Speichern an _einer_ Stelle haben, damit ich es schneller finden kann.
// Ordner: /data/data/YOUR_PACKAGE_NAME/shared_prefs/YOUR_PREFS_NAME.xml
public class Persistence implements IPersistence {
    // Global data ----
    private static final String GLOBAL_OLD_GAME = "/oldGame";
    private static final String GLOBAL_PLAYERNAME = "/playername";
    private static final String GLOBAL_PLAYERNAME_ENTERED = "/playernameEntered";
    private static final String GLOBAL_CURRENT_PLANET = "/currentPlanet";
    // Planet specific data ----
    private static final String PLANET_VERSION = "version";
    private static final String PLANET_VISIBLE = "visible";
    private static final String PLANET_OWNER = "owner";
    // Game specific data ----
    private static final String NAME = "GAMEDATA_2";
    private static final String SCORE = "score";
    private static final String DELTA = "delta";
    private static final String MOVES = "moves";
    private static final String GAME_OVER = "gameOver";
    private static final String HIGHSCORE_SCORE = "highscore";
    private static final String HIGHSCORE_MOVES = "highscoreMoves";
    private static final String GAMEPIECEVIEW = "gamePieceView";
    private static final String PLAYINGFIELD = "playingField";
    private static final String GRAVITATION_ROWS = "gravitationRows";
    private static final String GRAVITATION_EXCLUSIONS = "gravitationExclusions";
    private static final String GRAVITATION_PLAYED_SOUND = "gravitationPlayedSound";
    private static final String OWNER_NAME = "owner_name";
    private static final String OWNER_SCORE = "owner_score"; // enemy score
    private static final String OWNER_MOVES = "owner_moves";
    private static final String NEXT_ROUND = "nextRound"; // NextGamePieceFromSet

    private final ContextWrapper owner;
    private SharedPreferences __pref; // only access by pref() !
    private String prefix = "";

    // Die Kommentare sind mglw. tlw. überholt (22.10.20)

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
    public void setGameID_oldGame() {
        prefix = "";
    }

    @Override
    public void setGameID(IPlanet planet, int gameDefinitionIndex) {
        // e.g. "C1_16_0"
        prefix = "C" + planet.getClusterNumber() + "_" + planet.getNumber() + "_" + gameDefinitionIndex;
    }

    @Override
    public void setGameID(IPlanet planet) {
        setGameID(planet, planet.getGameDefinitions().indexOf(planet.getSelectedGame()));
    }

    private String getPlanetKey(IPlanet planet) {
        // e.g. "/1_16_"
        return "/" + planet.getClusterNumber() + "_" + planet.getNumber() + "_";
    }

    private String name(String name) {
        if (name.startsWith("/")) { // global parameter -> don't prepend game-specific suffix
            return name.substring(1);
        }
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
    public int loadDelta() {
        return getInt(DELTA, 0);
    }

    @Override
    public void saveDelta(int delta) {
        putInt(DELTA, delta);
    }

    @Override
    public int loadHighScore() {
        return getInt(HIGHSCORE_SCORE, 0);
    }

    @Override
    public void saveHighScore(int punkte) {
        putInt(name(HIGHSCORE_SCORE), punkte);
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
        if (!d.isEmpty() && !d.contains("/")/*because of bug*/) {
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
        save(GRAVITATION_EXCLUSIONS, d);

        SharedPreferences.Editor edit = pref().edit();
        edit.putBoolean(name(GRAVITATION_PLAYED_SOUND), data.isFirstGravitationPlayed());
        edit.apply();
    }

    @Override
    public void savePlayerName(String playername) {
        if (playername == null || playername.trim().isEmpty()) return;
        SharedPreferences.Editor edit = pref().edit();
        edit.putString(name(GLOBAL_PLAYERNAME), playername);
        edit.apply();
    }

    @Override
    public void saveCurrentPlanet(int clusterNumber, int planetNumber) {
       putInt(GLOBAL_CURRENT_PLANET, planetNumber);
    }

    @Override
    public int loadCurrentPlanet() {
        return getInt(GLOBAL_CURRENT_PLANET, 1);
    }

    @Override
    public int loadCurrentCluster() {
        return 1; // At this time there's only cluster 1 implemented.
    }

    @Override
    public String loadPlayerName() {
        String playername = pref().getString(name(GLOBAL_PLAYERNAME), "");
        if (playername == "") {
            Random rand = new Random(System.currentTimeMillis());
            playername = "Player_" + rand.nextInt(9999);
            savePlayerName(playername);
        }
        return playername;
    }

    @Override
    public void savePlanet(IPlanet planet) {
        String key = getPlanetKey(planet);
        putInt(key + PLANET_VERSION, 1);
        putBoolean(key + PLANET_VISIBLE, planet.isVisibleOnMap());
        putBoolean(key + PLANET_OWNER, planet.isOwner());
    }

    @Override
    public void loadPlanet(IPlanet planet) {
        String key = getPlanetKey(planet);
        if (getInt(key + PLANET_VERSION, 0) != 1) {
            return; // There are no planet data.
        }
        planet.setVisibleOnMap(getBoolean(key + PLANET_VISIBLE));
        planet.setOwner(getBoolean(key + PLANET_OWNER));
    }

    @Override
    public void saveOldGame(int v) {
        putInt(GLOBAL_OLD_GAME, v);
    }

    @Override
    public int loadOldGame() {
        return getInt(GLOBAL_OLD_GAME, 0);
    }

    @Override
    public boolean isStoneWars() {
        return loadOldGame() == 2;
    }

    @Override
    public void saveNextRound(int nextRound) {
        putInt(NEXT_ROUND, nextRound);
    }

    @Override
    public int loadNextRound() {
        return getInt(NEXT_ROUND, 0);
    }

    @Override
    public boolean loadGameOver() {
        return getBoolean(GAME_OVER);
    }

    @Override
    public void saveGameOver(boolean gameOver) {
        putBoolean(GAME_OVER, gameOver);
    }

    @Override
    public boolean loadPlayernameEntered() {
        return getBoolean(GLOBAL_PLAYERNAME_ENTERED);
    }

    @Override
    public void savePlayernameEntered(boolean v) {
        putBoolean(GLOBAL_PLAYERNAME_ENTERED, v);
    }

    @Override
    public void saveOwner(int score, int moves, String name) {
        putInt(OWNER_SCORE, score);
        putInt(OWNER_MOVES, moves);
        SharedPreferences.Editor edit = pref().edit();
        edit.putString(name(OWNER_NAME), name);
        edit.apply();
    }

    @Override
    public void clearOwner() {
        saveOwner(0, 0, "");
    }

    @Override
    public String loadOwnerName() {
        return pref().getString(name(OWNER_NAME), "");
    }

    @Override
    public int loadOwnerScore() {
        return getInt(OWNER_SCORE, 0);
    }

    @Override
    public int loadOwnerMoves() {
        return getInt(OWNER_MOVES, 0);
    }

    private String getString(String name) {
        return pref().getString(name(name), "");
    }

    private int getInt(String name, int defVal) {
        return pref().getInt(name(name), defVal);
    }

    private boolean getBoolean(String name) {
        return getInt(name, 0) == 1;
    }

    private void putInt(String name, int val) {
        SharedPreferences.Editor edit = pref().edit();
        edit.putInt(name(name), val);
        edit.apply();
    }

    private void putBoolean(String name, boolean val) {
        putInt(name, val ? 1 : 0);
    }

    private void save(String name, StringBuilder s) {
        SharedPreferences.Editor edit = pref().edit();
        edit.putString(name(name), s.toString());
        edit.apply();
    }

    @Override
    public void resetAll() {
        pref().edit().clear().commit(); // scheiss auf die Warning
    }
}
