package de.mwvb.blockpuzzle.global.messages;

import android.app.Activity;
import android.widget.Toast;

import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.gamestate.GamePlayState;

public class MessageFactory {
    private final MessageObjectWithGameState noMessage = new MessageObjectWithGameState(this, 0, GamePlayState.PLAYING) {
        @Override
        public void show() { // do nothing
        }
    };

    // normal messages ----
    private final MessageObject putData_makesNoSense = new MessageObject(this, R.string.putData_makesNoSense);
    private final MessageObject putData_formatError1 = new MessageObject(this, R.string.putData_formatError1);
    private final MessageObject putData_formatError2 = new MessageObject(this, R.string.putData_formatError2);
    private final MessageObject putData_unknownCluster = new MessageObject(this, R.string.putData_unknownCluster);
    private final MessageObject putData_checksumMismatch = new MessageObject(this, R.string.putData_checksumMismatch);
    private final MessageObject putData_wrongPlanetData = new MessageObject(this, R.string.putData_wrongPlanetData);
    private final MessageObject putData_okay = new MessageObject(this, R.string.putData_okay);
    private final MessageObject putData_success = new MessageObject(this, R.string.putData_success);
    private final MessageObject nothingToInsert = new MessageObject(this, R.string.nothingToInsert);

    private final MessageObject move = new MessageObject(this, R.string.move);
    private final MessageObject moves = new MessageObject(this, R.string.moves);
    private final MessageObject score1 = new MessageObject(this, R.string.score1);
    private final MessageObject score2 = new MessageObject(this, R.string.score2);
    private final MessageObject winScore1 = new MessageObject(this, R.string.winScore1);
    private final MessageObject winScore2 = new MessageObject(this, R.string.winScore2);
    private final MessageObject gameOverScore1 = new MessageObject(this, R.string.gameOverScore1);
    private final MessageObject gameOverScore2 = new MessageObject(this, R.string.gameOverScore2);

    // game logic decides whether it's WON or LOST game ----
    private final MessageObject noMoreGamePieces = new MessageObject(this, R.string.noMoreGamePieces);

    // WON ----
    private final MessageObjectWithGameState territoryLiberated = new MessageObjectWithGameState(this, R.string.territoryLiberated, GamePlayState.WON_GAME);
    private final MessageObjectWithGameState planetLiberated = new MessageObjectWithGameState(this, R.string.planetLiberated, GamePlayState.WON_GAME);
    private final MessageObjectWithGameState receivedTrophy = new MessageObjectWithGameState(this, R.string.receivedTrophy, GamePlayState.WON_GAME);
    private final MessageObjectWithGameState defeatedEnemy = new MessageObjectWithGameState(this, R.string.defeatedEnemy, GamePlayState.WON_GAME);
    private final MessageObjectWithGameState defeatedEnemyAndPlanetLiberated = new MessageObjectWithGameState(this, R.string.defeatedEnemyAndPlanetLiberated, GamePlayState.WON_GAME);
    private final MessageObjectWithGameState reactorDestroyed = new MessageObjectWithGameState(this, R.string.reactorDestroyed, GamePlayState.WON_GAME);
    private final MessageObjectWithGameState deathStarDestroyed = new MessageObjectWithGameState(this, R.string.deathStarDestroyed, GamePlayState.WON_GAME);

    // LOST ----
    private final MessageObjectWithGameState tooManyMoves = new MessageObjectWithGameState(this, R.string.tooManyMoves, GamePlayState.LOST_GAME);

    private final Activity activity;

    // Die MainActivity instantiiert eine Klasse und behält diese bei.
    public MessageFactory(Activity activity) {
        this.activity = activity;
    }

    /**
     * Should only be called by MessageObject class.
     */
    public void show(int id) {
        Toast.makeText(activity, activity.getResources().getString(id), Toast.LENGTH_LONG).show();
    }

    public String getText(int id) {
        if (activity == null) return "#" + id;
        return activity.getResources().getString(id);
    }

    public MessageObjectWithGameState getNoMessage() {
        return noMessage;
    }

    public MessageObject getPutData_makesNoSense() {
        return putData_makesNoSense;
    }

    public MessageObject getPutData_formatError1() {
        return putData_formatError1;
    }

    public MessageObject getPutData_formatError2() {
        return putData_formatError2;
    }

    public MessageObject getPutData_unknownCluster() {
        return putData_unknownCluster;
    }

    public MessageObject getPutData_checksumMismatch() {
        return putData_checksumMismatch;
    }

    public MessageObject getPutData_wrongPlanetData() {
        return putData_wrongPlanetData;
    }

    public MessageObject getPutData_okay() {
        return putData_okay;
    }

    public MessageObject getPutData_success() {
        return putData_success;
    }

    public MessageObject getNothingToInsert() {
        return nothingToInsert;
    }

    public MessageObject getNoMoreGamePieces() {
        return noMoreGamePieces;
    }

    public MessageObject getMove() {
        return move;
    }

    public MessageObject getMoves() {
        return moves;
    }

    public MessageObject getScore1() {
        return score1;
    }

    public MessageObject getScore2() {
        return score2;
    }

    public MessageObject getWinScore1() {
        return winScore1;
    }

    public MessageObject getWinScore2() {
        return winScore2;
    }

    public MessageObject getGameOverScore1() {
        return gameOverScore1;
    }

    public MessageObject getGameOverScore2() {
        return gameOverScore2;
    }

    public MessageObjectWithGameState getTerritoryLiberated() {
        return territoryLiberated;
    }

    public MessageObjectWithGameState getPlanetLiberated() {
        return planetLiberated;
    }

    public MessageObjectWithGameState getReceivedTrophy() {
        return receivedTrophy;
    }

    public MessageObjectWithGameState getDefeatedEnemy() {
        return defeatedEnemy;
    }

    public MessageObjectWithGameState getDefeatedEnemyAndPlanetLiberated() {
        return defeatedEnemyAndPlanetLiberated;
    }

    public MessageObjectWithGameState getReactorDestroyed() {
        return reactorDestroyed;
    }

    public MessageObjectWithGameState getDeathStarDestroyed() {
        return deathStarDestroyed;
    }

    public MessageObjectWithGameState getTooManyMoves() {
        return tooManyMoves;
    }
}
