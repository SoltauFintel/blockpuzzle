package de.mwvb.blockpuzzle.gamedefinition;

import androidx.annotation.NonNull;

import de.mwvb.blockpuzzle.gamestate.ScoreChangeInfo;
import de.mwvb.blockpuzzle.gamestate.Spielstand;
import de.mwvb.blockpuzzle.global.messages.MessageObjectWithGameState;
import de.mwvb.blockpuzzle.planet.IPlanet;
import de.mwvb.blockpuzzle.playingfield.PlayingField;

public abstract class GameDefinition {
    private final int gamePieceSetNumber;
    /** R.string constant, -1=use standard names */
    private int territoryName = -1;
    private LiberatedFeature libf = null;

    public GameDefinition(int gamePieceSetNumber) {
        this.gamePieceSetNumber = gamePieceSetNumber;
    }

    // GAME DEFINITION ----

    public int getGamePieceSetNumber() {
        return gamePieceSetNumber;
    }

    public int getTerritoryName() {
        return territoryName;
    }

    public void setTerritoryName(int territoryName) {
        this.territoryName = territoryName;
    }

    /**
     * @return true: Sieg, false: nicht relevant oder kein Sieg
     */
    public boolean onEmptyPlayingField() {
        return false;
    }

    public boolean offerNewGamePiecesAfterGameOver() {
        return true;
    }

    public boolean gameCanBeWon() {
        return false;
    }

    public int getGamePieceBlocksScoreFactor() {
        return 1;
    }

    public int getHitsScoreFactor() {
        return 10;
    }

    public boolean isRowsAdditionalBonusEnabled() {
        return true;
    }

    /**
     * @return true: Die Moves-Anzeige der Score-Anzeige bevorzugen. false: andersrum
     */
    public boolean showMoves() {
        return false;
    }


    // GAME INIT PHASE ----

    /**
     * call p.draw() after filling something in
     */
    public void fillStartPlayingField(PlayingField p) { // Template method
    }


    // DISPLAY ----
    // TODO Die Ausgabe von diesen beiden Methoden ist zu ähnlich. Was kann man da machen?

    /** Game definition info for select-territory and planet activity */
    public abstract String getInfo();

    /** info for cluster view */
    public abstract String getClusterViewInfo();


    // QUESTIONS AND EVENTS ----

    // TODO Diese Methode ist zu überdenken! (Parameterübergabe vs. Daten laden)
    //      Statt die Werte zu übergeben, könnte man Supplier (Provider?) übergeben, die just-in-time liefern - und zwar dann entweder statisch oder die Daten laden.
    /**
     * @return true if planet or territory was liberated by player 1
     */
    public abstract boolean isLiberated(int player1Score, int player1Moves, int player2Score, int player2Moves, boolean playerIsPlayer1, IPlanet planet, int gameDefinitionIndex);

    /**
     * @param info data and services
     * @return MessageObjectWithGameState; info.messages.noMessage if there's no message to be displayed; not null
     */
    @NonNull
    public abstract MessageObjectWithGameState scoreChanged(ScoreChangeInfo info);

    public LiberatedFeature getLiberatedFeature() {
        return libf;
    }

    public void setLiberatedFeature(LiberatedFeature v) {
        libf = v;
    }

    /**
     * If there are no game pieces anymore the player has lost the game by default.
     * If you return true here you can let the player win the game.
     */
    public boolean isWonAfterNoGamePieces(Spielstand ss) {
        return false;
    }
}
