package de.mwvb.blockpuzzle.playingfield.gravitation;

import de.mwvb.blockpuzzle.game.GameEngine;
import de.mwvb.blockpuzzle.game.GameEngineInterface;
import de.mwvb.blockpuzzle.playingfield.Action;
import de.mwvb.blockpuzzle.playingfield.PlayingField;
import de.mwvb.blockpuzzle.playingfield.QPosition;

public class GravitationAction implements Action {
    private final GravitationData data;
    private final GameEngineInterface possibleMovesChecker;
    private final PlayingField playingField;
    private final int startRow;

    public GravitationAction(GravitationData gravitationData, GameEngineInterface possibleMovesChecker, PlayingField playingField, int startRow) {
        this.data = gravitationData;
        this.possibleMovesChecker = possibleMovesChecker;
        this.playingField = playingField;
        this.startRow = startRow;
    }

    @Override
    public void execute() {
        for (int i = startRow; i >= 1; i--) {
            if (hasToBeRemoved(i) && data.getRows().contains(GameEngine.blocks - i)) {
                // Row war voll und wurde geleert -> Gravitation auslösen
                playingField.gravitation(GameEngine.blocks - i, !data.isFirstGravitationPlayed());
                data.setFirstGravitationPlayed(true);
            }
        }
        data.clear(); // clear after use
        possibleMovesChecker.checkPossibleMoves();
    }

    private boolean hasToBeRemoved(int i) {
        for (QPosition k : data.getExclusions()) {
            if (k.getY() == GameEngine.blocks - i) {
                return false;
            }
        }
        return true;
    }
}
