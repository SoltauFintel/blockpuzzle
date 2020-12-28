package de.mwvb.blockpuzzle.gamedefinition;

import de.mwvb.blockpuzzle.persistence.GamePersistence;
import de.mwvb.blockpuzzle.persistence.IPersistence;
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
    public void fillStartPlayingField(PlayingField p) {
    }


    // DISPLAY ----

    /** Game definition info for select-territory and planet activity */
    public abstract String getInfo();

    /** info for cluster view */
    public abstract String getClusterViewInfo();


    // QUESTIONS AND EVENTS ----

    /**
     * @param persistence fertig eingestellt für das Game
     * @return true if planet or territory was liberated by player 1
     */
    public abstract boolean isLiberated(int player1Score, int player1Moves, int player2Score, int player2Moves, IPersistence persistence, boolean playerIsPlayer1);

    /**
     * @param planet must be persistence.getPlanet()
     * @return null, or message text for Toast, prefix "+" if victory (play applause sound), prefix "-" for game over (play laughing)
     */
    public abstract String scoreChanged(int score, int moves, IPlanet planet, boolean won, GamePersistence persistence, ResourceAccess resouces);

    public LiberatedFeature getFeatureOnLiberation() {
        return libf;
    }

    public void setLiberatedFeature(LiberatedFeature v) {
        libf = v;
    }

    /**
     * If there are no game pieces anymore the player has lost the game by default.
     * If you return true here you can let the player win the game.
     */
    public boolean isWonAfterNoGamePieces(int punkte, int moves, GamePersistence gape) {
        return false;
    }
}
