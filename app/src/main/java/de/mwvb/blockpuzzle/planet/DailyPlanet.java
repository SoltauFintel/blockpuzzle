package de.mwvb.blockpuzzle.planet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.mwvb.blockpuzzle.Features;
import de.mwvb.blockpuzzle.persistence.IPersistence;
import de.mwvb.blockpuzzle.persistence.Persistence;

/**
 * This is an important outpost of the Orange Union. Every day the Orange Union recaptures the planet.
 */
public class DailyPlanet extends Planet {
    public static final String ACTIVE_GAME = "_";
    public static final String WON_GAME = "W";

    public DailyPlanet(int number, int x, int y, int gravitation) {
        super(number, x, y, gravitation);
    }

    @Override
    public boolean userMustSelectTerritory() {
        return false;
    }

    @Override
    public boolean isNextGamePieceResetedForNewGame() {
        return false;
    }

    @Override
    public boolean isDataExchangeRelevant() {
        return false;
    }

    /** Rückgabewert 0 bis 6. Im Zweifelsfall 0. */
    @Override
    public int getCurrentGameDefinitionIndex(IPersistence persistence) {
        // Gibt es für heute ein Spiel?
        String today = getToday(persistence);
        int ret = findIndexForDate(persistence, today + ACTIVE_GAME, today + WON_GAME); // find any game
        if (ret == -1) { // not found
            // Gibt es für gestern ein gewonnenes Spiel?
            String dayM1 = calculateDayBefore(today);
            ret = findIndexForDate(persistence, dayM1 + WON_GAME, dayM1 + WON_GAME); // only won game counts
            if (ret >= 0 && ret < 6) {
                ret++;
            } else {
                // -1: es gibt kein Spiel -> also bei Tag 1 beginnen
                //  6: es wurde Tag 7 gewonnen -> es geht wieder bei Tag 1 los
                ret = 0;
            }
            createNewGame(persistence, ret, today);
        }

        // Das bestimmte Spiel als gewählt einstellen, damit getSelectedGame() Zugriffe korrekt funktionieren.
        setSelectedGame(getGameDefinitions().get(ret));
        return ret;
    }

    /**
     * @param persistence falls ich das für den Developer Mode brauche
     * @return Tagesdatum im Format JJJJ-MM-TT
     */
    public static String getToday(IPersistence persistence) {
        if (Features.developerMode) {
            String ret = ((Persistence) persistence).loadTodayDate();
            if (ret != null && "2020-10-10".length() == ret.length()) {
                return ret;
            }
        }
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    private int findIndexForDate(IPersistence persistence, String xdate1, String xdate2) {
        for (int i = 6; i >= 0; i--) {
            String date = persistence.loadDailyDate(this, i);
            if (xdate1.equals(date) || xdate2.equals(date)) {
                return i;
            }
        }
        return -1;
    }

    private String calculateDayBefore(String date) {
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date);
            if (d == null) {
                return "";
            }
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            c.add(Calendar.DATE, -1);
            Date dM1 = c.getTime();
            return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dM1);
        } catch (ParseException e) {
            return "";
        }
    }

    /** Neues Game anlegen (inkl. Spielstand reseten) */
    private void createNewGame(IPersistence persistence, int ret, String today) {
        int nextRound = 0;
        if (ret > 0) {
            persistence.setGameID(this, ret - 1);
            nextRound = persistence.loadNextRound() - 1;
            if (nextRound < 0) {
                nextRound = 0;
            }
        }

        persistence.setGameID(this, ret);
        persistence.saveScore(-1);
        persistence.saveMoves(0);
        persistence.saveGameOver(false);
        persistence.saveNextRound(nextRound);
        persistence.saveDailyDate(this, ret, today + ACTIVE_GAME);
        setOwner(false);
        persistence.savePlanet(this);
    }
}
