package de.mwvb.blockpuzzle.gamedefinition;

import org.jetbrains.annotations.NotNull;

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
    /** MLS | use getter to access it! */
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

    @NotNull
    @Override
    public String toString() {
        return "ClassicGame(GPSN=" + getGamePieceSetNumber() + ",MLS=" + getMinimumLiberationScore() + ")";
    }


    // DISPLAY ----

    @Override
    public String getInfo() {
        String info = "";
        if (Features.developerMode) {
            info = "Z" + getGamePieceSetNumber() + " ";
        }
        info += getShortGameName() + " Game";
        if (getMinimumLiberationScore() > 0) {
            info += " MLS" + (getMinimumLiberationScore() / 1000) + "k";
        }
        return info;
    }

    protected String getShortGameName() {
        return "Classic";
    }

    @Override
    public String getClusterViewInfo() {
        if (Features.developerMode) {
            return "Z" + getGamePieceSetNumber() + " " + getShortGameName() + " MLS" + (getMinimumLiberationScore() / 1000) + "k";
        } else {
            return getShortGameName() + " MLS" + (getMinimumLiberationScore() / 1000) + "k";
        }
    }


    // QUESTIONS AND EVENTS ----

    @Override
    public boolean isLiberated(int player1Score, int player1Moves, int player2Score, int player2Moves, IPersistence persistence, boolean playerIsPlayer1) {
        return player1Score > 0 && player1Score >= getMinimumLiberationScore() &&
                (player1Score > player2Score || (player1Score == player2Score && player1Moves < player2Moves));
    }

    @Override
    public String scoreChanged(int score, int moves, IPlanet planet, boolean won, GamePersistence persistence, ResourceAccess resources) {
        if (won || score < getMinimumLiberationScore()) return null;

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
                return getPlanetLiberatedText(resources);
            } else {
                persistence.saveScore(score);
                persistence.saveMoves(moves);
                if (new GameInfoService().isPlanetFullyLiberated(planet, persistence.getPersistenceOK())) {
                    planet.setOwner(true); // Spiel gewonnen! Planet befreit!
                    persistence.get().savePlanet(planet);
                    return getPlanetLiberatedText(resources);
                }
                return getTerritoryLiberatedText(resources);
            }
        }
        return null;
    }

    protected String getPlanetLiberatedText(ResourceAccess resources) {
        return resources.getString(R.string.planetLiberated);
    }

    protected String getTerritoryLiberatedText(ResourceAccess resources) {
        return resources.getString(R.string.territoryLiberated);
    }

    @Override
    public boolean isWonAfterNoGamePieces(int punkte, int moves, GamePersistence gape) {
        if (punkte <= 0) { // Sicherstellen, dass ein Sieg ohne Punkte nicht möglich ist.
            return false;
        }
        // Entweder hat man's geschafft mehr Punkte als der Gegner zu bekommen oder nicht.
        // MEHR (ODER FALLS KEIN GEGNER): Planet befreit. Das müsste schon zuvor bekannt gewesen sein. Spielsieg.
        // WENIGER:                       Planet nicht befreit. Spiel verloren.
        if (getMinimumLiberationScore() <= 0 || punkte >= getMinimumLiberationScore()) {
            int ownerScore = gape.loadOwnerScore();
            return ownerScore <= 0 // Es gibt kein Gegner
                    || punkte > ownerScore // oder man ist besser als der Gegner
                    || (punkte == ownerScore && moves <= gape.loadOwnerMoves()); // oder man ist gleich gut wie der Gegner, hat aber nicht mehr Moves
        }
        return false;
    }
}
