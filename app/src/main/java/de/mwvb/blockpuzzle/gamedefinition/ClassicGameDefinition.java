package de.mwvb.blockpuzzle.gamedefinition;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import de.mwvb.blockpuzzle.game.GameInfoService;
import de.mwvb.blockpuzzle.gamestate.ScoreChangeInfo;
import de.mwvb.blockpuzzle.gamestate.Spielstand;
import de.mwvb.blockpuzzle.global.Features;
import de.mwvb.blockpuzzle.global.messages.MessageFactory;
import de.mwvb.blockpuzzle.global.messages.MessageObjectWithGameState;
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

    @Override
    public boolean gameGoesOnAfterWonGame() {
        return true;
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
    public boolean isLiberated(int player1Score, int player1Moves, int player2Score, int player2Moves, boolean playerIsPlayer1, IPlanet planet, int index) {
        return player1Score > 0 && player1Score >= getMinimumLiberationScore() &&
                (player1Score > player2Score || (player1Score == player2Score && player1Moves < player2Moves));
    }

    @NonNull
    @Override
    public MessageObjectWithGameState scoreChanged(ScoreChangeInfo info) {
        int score = info.getScore();
        if (score < getMinimumLiberationScore()) {
            return info.getMessages().getNoMessage();
        }

        int ownerScore = info.getOwnerScore();
        if (ownerScore > 0 && score > ownerScore) { // Planet war von Gegner besetzt
            info.clearOwner(); // Gegner geschlagen!
            info.saveOwner(true); // Spiel gewonnen! Territorium befreit!
            return info.getMessages().getDefeatedEnemy();

        } else if (ownerScore <= 0) { // Planet war von Orange Union besetzt

            if (info.getPlanet().getGameDefinitions().size() == 1) {
                info.saveOwner(true); // Spiel gewonnen! Planet befreit!
                return getPlanetLiberatedText(info.getMessages());
            } else {
                // Sicherstellen, dass score+moves gespeichert sind, da isPlanetFullyLiberated() das braucht. (TO-DO Das ist noch so eine Designschwäche, die ich bereinigen muss.)
                info.saveScoreAndMoves();
                if (new GameInfoService().isPlanetFullyLiberated(info.getPlanet())) {
                    info.saveOwner(true); // Spiel gewonnen! Planet befreit!
                    return getPlanetLiberatedText(info.getMessages());
                }
                return getTerritoryLiberatedText(info.getMessages());
            }
        }
        return info.getMessages().getNoMessage();
    }

    protected MessageObjectWithGameState getPlanetLiberatedText(MessageFactory messages) {
        return messages.getPlanetLiberated();
    }

    protected MessageObjectWithGameState getTerritoryLiberatedText(MessageFactory messages) {
        return messages.getTerritoryLiberated();
    }

    @Override
    public boolean isWonAfterNoGamePieces(Spielstand ss) {
        if (ss.getScore() <= 0) { // Sicherstellen, dass ein Sieg ohne Punkte nicht möglich ist.
            return false;
        }
        // Entweder hat man's geschafft mehr Punkte als der Gegner zu bekommen oder nicht.
        // MEHR (ODER FALLS KEIN GEGNER): Planet befreit. Das müsste schon zuvor bekannt gewesen sein. Spielsieg.
        // WENIGER:                       Planet nicht befreit. Spiel verloren.
        if (getMinimumLiberationScore() <= 0 || ss.getScore() >= getMinimumLiberationScore()) {
            int ownerScore = ss.getOwnerScore();
            return ownerScore <= 0 // Es gibt kein Gegner
                    || ss.getScore() > ownerScore // oder man ist besser als der Gegner
                    || (ss.getScore() == ownerScore && ss.getMoves() <= ss.getOwnerMoves()); // oder man ist gleich gut wie der Gegner, hat aber nicht mehr Moves
        }
        return false;
    }
}
