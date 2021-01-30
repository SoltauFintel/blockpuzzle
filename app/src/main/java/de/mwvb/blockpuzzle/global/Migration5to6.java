package de.mwvb.blockpuzzle.global;

import android.content.Context;
import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;

import de.mwvb.blockpuzzle.cluster.Cluster1;
import de.mwvb.blockpuzzle.gamestate.GamePlayState;
import de.mwvb.blockpuzzle.gamestate.Spielstand;
import de.mwvb.blockpuzzle.gamestate.SpielstandDAO;
import de.mwvb.blockpuzzle.gamestate.Trophies;
import de.mwvb.blockpuzzle.gamestate.TrophiesDAO;
import de.mwvb.blockpuzzle.planet.IPlanet;
import de.mwvb.blockpuzzle.planet.ISpaceObject;
import de.mwvb.blockpuzzle.planet.SpaceObjectState;
import de.mwvb.blockpuzzle.planet.SpaceObjectStateDAO;

/**
 * Data migration from V5 to V6. From SharedPrefs to JSON files. From IPersistence to AbstractDAO. From API controlled to object oriented.
 */
public class Migration5to6 {
    // Global data ----
    private static final String GLOBAL_OLD_GAME = "/oldGame";
    private static final String GLOBAL_PLAYERNAME = "/playername";
    private static final String GLOBAL_PLAYERNAME_ENTERED = "/playernameEntered";
    private static final String GLOBAL_GAME_SOUNDS = "/gameSounds";
    private static final String GLOBAL_CURRENT_PLANET = "/currentPlanet";
    private static final String GLOBAL_BRONZE_TROPHY = "/trophyBronze_C";   // cluster wide
    private static final String GLOBAL_SILVER_TROPHY = "/trophySilver_C";   // cluster wide
    private static final String GLOBAL_GOLDEN_TROPHY = "/trophyGolden_C";   // cluster wide
    private static final String GLOBAL_PLATINUM_TROPHY = "/trophyPlatinum"; // galaxy wide
    private static final String GLOBAL_LAST_TROPHY_DATE = "/trophyLastDate";// galaxy wide
    private static final String GLOBAL_DEATH_STAR = "/todesstern";
    private static final String GLOBAL_DEATH_STAR_REACTOR = "/reaktor";
    // Planet specific data ----
    private static final String PLANET_VERSION = "version";
    private static final String PLANET_VISIBLE = "visible";
    private static final String PLANET_OWNER = "owner";
    // Game specific data ----
    private static final String SCORE = "score";
    private static final String DELTA = "delta";
    private static final String MOVES = "moves";
    private static final String EMPTY_SCREEN_BONUS_ACTIVE = "emptyScreenBonusActive";
    private static final String DAILY_DATE = "dailyDate";
    private static final String GAME_OVER = "gameOver";
    private static final String HIGHSCORE_SCORE = "highscore";
    private static final String HIGHSCORE_MOVES = "highscoreMoves";
    private static final String GAMEPIECEVIEW = "gamePieceView";
    private static final String PLAYINGFIELD = "playingField";
    private static final String GRAVITATION_ROWS = "gravitationRows";
    private static final String GRAVITATION_EXCLUSIONS = "gravitationExclusions";
    private static final String GRAVITATION_PLAYED_SOUND = "gravitationPlayedSound";
    private static final String OWNER_NAME = "owner_name";
    private static final String OWNER_SCORE = "owner_score";
    private static final String OWNER_MOVES = "owner_moves";
    private static final String NEXT_ROUND = "nextRound";

    private SharedPreferences pref;
    private String prefix = "";
    private final GlobalDataDAO globalDataDAO = new GlobalDataDAO();
    private final TrophiesDAO trophiesDAO = new TrophiesDAO();
    private final SpaceObjectStateDAO planetDAO = new SpaceObjectStateDAO();
    private final SpielstandDAO spielstandDAO = new SpielstandDAO();

    public boolean isNecessary() {
        return !globalDataDAO.exists();
    }

    public void migrate(Context context) {
        System.out.println("-------------------MIGRATION---------------------");
        pref = context.getSharedPreferences("GAMEDATA_2", Context.MODE_PRIVATE);

        GlobalData g = new GlobalData();
        g.setCurrentPlanet(getInt(GLOBAL_CURRENT_PLANET, 1));
        g.setGameSounds(getBoolean(GLOBAL_GAME_SOUNDS, true));
        g.setPlayername(getString(GLOBAL_PLAYERNAME));
        g.setPlayernameEntered(getBoolean(GLOBAL_PLAYERNAME_ENTERED));
        g.setGameType(getGameType());
        g.setPlatinumTrophies(getInt(GLOBAL_PLATINUM_TROPHY));
        g.setLastTrophyDate(getString(GLOBAL_LAST_TROPHY_DATE));
        g.setTodesstern(getInt(GLOBAL_DEATH_STAR));
        g.setTodessternReaktor(getInt(GLOBAL_DEATH_STAR_REACTOR));
        globalDataDAO.save(g);

        Trophies t = new Trophies();
        int cn = Cluster1.INSTANCE.getNumber();
        t.setBronze(getInt(GLOBAL_BRONZE_TROPHY + cn));
        t.setSilver(getInt(GLOBAL_SILVER_TROPHY + cn));
        t.setGolden(getInt(GLOBAL_GOLDEN_TROPHY + cn));
        trophiesDAO.save(cn, t);

        Spielstand s0 = new Spielstand();
        prefix = "";
        mapSpielstand(s0);
        spielstandDAO.saveOldGame(s0);

        for (ISpaceObject so : Cluster1.INSTANCE.getSpaceObjects()) {
            if (so instanceof IPlanet) {
                IPlanet p = (IPlanet) so;
                mapPlanetState(p);
                if (p.getGameDefinitions() != null) {
                    for (int i = 0; i < p.getGameDefinitions().size(); i++) {
                        Spielstand ss = new Spielstand();
                        prefix = "C" + p.getClusterNumber() + "_" + p.getNumber() + "_" + i;
                        mapSpielstand(ss);
                        spielstandDAO.save(p, i, ss);
                        prefix = "";
                    }
                }
            }
        }

        pref = null;
        System.out.println("--------- Migration erfolgreich ---------------");
    }

    @NotNull
    private GameType getGameType() {
        switch (getInt(GLOBAL_OLD_GAME)) {
            case 1:
                return GameType.OLD_GAME;
            case 2:
                return GameType.STONE_WARS;
            default: // 0
                return GameType.NOT_SELECTED;
        }
    }

    private void mapPlanetState(IPlanet p) {
        SpaceObjectState ps = new SpaceObjectState();
        prefix = p.getClusterNumber() + "_" + p.getNumber() + "_";
        if (getInt(PLANET_VERSION) == 1) {
            ps.setVisibleOnMap(getBoolean(PLANET_VISIBLE));
            ps.setOwner(getBoolean(PLANET_OWNER));
        } else { // no planet data
            ps.setVisibleOnMap(p.getNumber() == 1); // only planet 1 must be visible
            ps.setOwner(false);
        }
        ps.setVersion(1);
        prefix = "";
        planetDAO.save(p, ps);
    }

    private void mapSpielstand(Spielstand s) {
        // TODO Spiele durchrechnen um den Status sicher zu bekommen. Eine GameDefinition müsste ja zu einem Spielstand sagen können,
        //      ob PLAYING, LOST oder WON. Und das ohne View Komponenten.
        s.setState(getBoolean(GAME_OVER) ? GamePlayState.LOST_GAME : GamePlayState.PLAYING);

        s.setScore(getInt(SCORE, -9999));
        s.setMoves(getInt(MOVES));
        s.setDelta(getInt(DELTA));
        s.setEmptyScreenBonusActive(getBoolean(EMPTY_SCREEN_BONUS_ACTIVE));
        s.setDailyDate(getString(DAILY_DATE));
        s.setHighscore(getInt(HIGHSCORE_SCORE));
        s.setHighscoreMoves(getInt(HIGHSCORE_MOVES));
        s.setGamePieceView1(getString(GAMEPIECEVIEW + 1));
        s.setGamePieceView2(getString(GAMEPIECEVIEW + 2));
        s.setGamePieceView3(getString(GAMEPIECEVIEW + 3));
        s.setGamePieceViewP(getString(GAMEPIECEVIEW + -1));
        s.setPlayingField(getString(PLAYINGFIELD));
        String rows = getString(GRAVITATION_ROWS);
        if (rows != null && rows.contains("/")) rows = ""; // alten bug fixen
        s.setGravitationRows(rows);
        s.setGravitationExclusions(getString(GRAVITATION_EXCLUSIONS));
        s.setGravitationPlayedSound(getBoolean(GRAVITATION_PLAYED_SOUND));
        s.setOwnerName(getString(OWNER_NAME));
        s.setOwnerScore(getInt(OWNER_SCORE));
        s.setOwnerMoves(getInt(OWNER_MOVES));
        s.setNextRound(getInt(NEXT_ROUND));
    }

    private String getString(String name) {
        return pref.getString(name(name), "");
    }

    private int getInt(String name) {
        return getInt(name, 0);
    }

    private int getInt(String name, int pDefault) {
        return pref.getInt(name(name), pDefault);
    }

    private boolean getBoolean(String name) {
        return getBoolean(name, false);
    }

    private boolean getBoolean(String name, boolean pDefault) {
        return getInt(name, pDefault ? 1 : 0) == 1;
    }

    private String name(String name) {
        if (name.startsWith("/")) { // global parameter -> don't prepend game-specific suffix
            return name.substring(1);
        }
        return prefix + name;
    }

}
