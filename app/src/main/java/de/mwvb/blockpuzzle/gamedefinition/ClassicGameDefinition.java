package de.mwvb.blockpuzzle.gamedefinition;

import de.mwvb.blockpuzzle.Features;
import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.game.GameInfoService;
import de.mwvb.blockpuzzle.persistence.GamePersistence;
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
        if (Features.developerMode) {
            return "Z" + getGamePieceSetNumber() + " Classic MLS" + (minimumLiberationScore / 1000) + "k";
        } else {
            return "Classic MLS" + (minimumLiberationScore / 1000) + "k";
        }
    }


    // QUESTIONS AND EVENTS ----

    @Override
    public boolean isLiberated(int player1Score, int player1Moves, int player2Score, int player2Moves, IPersistence persistence) {
        return player1Score > 0 && player1Score >= minimumLiberationScore &&
                (player1Score > player2Score || (player1Score == player2Score && player1Moves < player2Moves));
    }

    @Override
    public String scoreChanged(int score, int moves, IPlanet planet, boolean won, GamePersistence persistence, ResourceAccess resources) {
        if (won || score < minimumLiberationScore) return null;

        int ownerScore = persistence.loadOwnerScore();
        if (ownerScore > 0 && score > ownerScore) { // Planet war von Gegner besetzt
            persistence.get().clearOwner(); // Gegner geschlagen!
            planet.setOwner(true); // Spiel gewonnen! Territorium befreit!
            persistence.get().savePlanet(planet);
            return resources.getString(R.string.defeatedEnemy);
        } else if (ownerScore <= 0) { // Planet war von Orange Union besetzt
            if (planet.getGameDefinitions().size() == 1) {
                planet.setOwner(true); // Spiel gewonnen! Planet befreit!
                persistence.get().savePlanet(planet);
                return resources.getString(R.string.planetLiberated);
            } else {
                if (new GameInfoService().isPlanetFullyLiberated(planet, persistence.getPersistenceOK())) {
                    planet.setOwner(true); // Spiel gewonnen! Planet befreit!
                    persistence.get().savePlanet(planet);
                    return resources.getString(R.string.planetLiberated);
                }
                return resources.getString(R.string.territoryLiberated);
            }
        }
        return null;
    }

    @Override
    public boolean isWonAfterNoGamePieces(int punkte, int moves, GamePersistence gape) {
        if (punkte <= 0) { // Sicherstellen, dass ein Sieg ohne Punkte nicht möglich ist.
            return false;
        }
        // Entweder hat man's geschafft mehr Punkte als der Gegner zu bekommen oder nicht.
        // MEHR (ODER FALLS KEIN GEGNER): Planet befreit. Das müsste schon zuvor bekannt gewesen sein. Spielsieg.
        // WENIGER:                       Planet nicht befreit. Spiel verloren.
        if (minimumLiberationScore <= 0 || punkte >= minimumLiberationScore) {
            int ownerScore = gape.loadOwnerScore();
            return ownerScore <= 0 // Es gibt kein Gegner
                    || punkte > ownerScore // oder man ist besser als der Gegner
                    || (punkte == ownerScore && moves <= gape.loadOwnerMoves()); // oder man ist gleich gut wie der Gegner, hat aber nicht mehr Moves
        }
        return false;
    }
}
