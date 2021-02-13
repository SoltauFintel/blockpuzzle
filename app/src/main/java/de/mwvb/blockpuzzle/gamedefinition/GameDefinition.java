package de.mwvb.blockpuzzle.gamedefinition;

import androidx.annotation.NonNull;

import de.mwvb.blockpuzzle.gamepiece.INextGamePiece;
import de.mwvb.blockpuzzle.gamepiece.NextGamePieceFromSet;
import de.mwvb.blockpuzzle.gamestate.GameState;
import de.mwvb.blockpuzzle.gamestate.ScoreChangeInfo;
import de.mwvb.blockpuzzle.gamestate.Spielstand;
import de.mwvb.blockpuzzle.global.messages.MessageObjectWithGameState;
import de.mwvb.blockpuzzle.planet.IPlanet;
import de.mwvb.blockpuzzle.playingfield.PlayingField;

/**
 * Stone Wars game definition
 */
public abstract class GameDefinition extends OldGameDefinition {
    private final int gamePieceSetNumber;
    /** R.string constant */
    private int territoryName;
    private LiberatedFeature libf = null;

    public GameDefinition(int gamePieceSetNumber) {
        this.gamePieceSetNumber = gamePieceSetNumber;
    }

    // GAME DEFINITION ----

    @Override
    public INextGamePiece createNextGamePieceGenerator(GameState gs) {
        return new NextGamePieceFromSet(gamePieceSetNumber, gs);
    }

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
    public boolean onEmptyPlayingField() { // TODO bescheuerter Name
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
    public void fillStartPlayingField(PlayingField p) { // Template method
    }


    // DISPLAY ----

    /**
     * Returns short game description
     * @param longDisplay true: normal length, false: a bit shorter
     * @return e.g. "CLassic Game MLS8k"
     */
    public abstract String getDescription(boolean longDisplay);


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
