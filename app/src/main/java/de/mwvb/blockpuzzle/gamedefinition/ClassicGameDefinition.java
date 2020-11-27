package de.mwvb.blockpuzzle.gamedefinition;

import android.app.Activity;
import android.content.res.Resources;
import android.widget.Toast;

import de.mwvb.blockpuzzle.Features;
import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.persistence.IPersistence;
import de.mwvb.blockpuzzle.planet.IPlanet;

/**
 * Spielende:
 * - Spielfeld voll -> Wertung der Score
 * - keine vordefinierten Spielsteine mehr -> Wertung der Score
 */
public class ClassicGameDefinition extends GameDefinition {
    /** MLS */
    private final int minimumLiberationScore;

    public ClassicGameDefinition(int gamePieceSetNumber) {
        this(gamePieceSetNumber, 1000);
    }

    public ClassicGameDefinition(int gamePieceSetNumber, int minimumLiberationScore) {
        super(gamePieceSetNumber);
        this.minimumLiberationScore = minimumLiberationScore;
    }

    // GAME DEFINITION ----

    public int getMinimumLiberationScore() {
        return minimumLiberationScore;
    }

    @Override
    public String toString() {
        return "ClassicGame(GPSN=" + getGamePieceSetNumber() + ",MLS=" + minimumLiberationScore + ")";
    }


    // DISPLAY ----

    @Override
    public String getInfo() {
        String info = "";
        if (Features.developerMode) {
            info = "Z" + getGamePieceSetNumber() + " ";
        }
        info += "Classic Game" ;
        if (minimumLiberationScore > 0) {
            info += " MLS" + (minimumLiberationScore / 1000) + "k";
        }
        return info;
    }

    @Override
    public String getClusterViewInfo() {
        return "Classic";
    }


    // QUESTIONS AND EVENTS ----

    @Override
    public boolean isLiberated(int player1Score, int player1Moves, int player2Score, int player2Moves) {
        return player1Score > 0 && player1Score >= minimumLiberationScore &&
                (player1Score > player2Score || (player1Score == player2Score && player1Moves < player2Moves));
    }

    @Override
    public String scoreChanged(int score, int moves, IPlanet planet, boolean won, IPersistence persistence, ResourceAccess resources) {
        if (won || score < minimumLiberationScore) return null;

        int ownerScore = persistence.loadOwnerScore();
        if (ownerScore > 0 && score > ownerScore) { // Planet war von Gegner besetzt
            persistence.clearOwner(); // Gegner geschlagen!
            planet.setOwner(true); // Spiel gewonnen! Territorium befreit!
            persistence.savePlanet(planet);
            return resources.getString(R.string.defeatedEnemy);
        } else if (ownerScore <= 0) { // Planet war von Orange Union besetzt
            planet.setOwner(true); // Spiel gewonnen! Territorium befreit!
            persistence.savePlanet(planet);
            return resources.getString(R.string.territoryLiberated);
        }
        return null;
    }
}
