package de.mwvb.blockpuzzle.gravitation;

import de.mwvb.blockpuzzle.game.Game;
import de.mwvb.blockpuzzle.playingfield.Action;
import de.mwvb.blockpuzzle.playingfield.PlayingField;
import de.mwvb.blockpuzzle.playingfield.QPosition;

public class GravitationAction implements Action {
    private final GravitationData data;
    private final Game game;
    private final PlayingField playingField;
    private final int startRow;

    public GravitationAction(GravitationData gravitationData, Game game, PlayingField playingField, int startRow) {
        this.data = gravitationData;
        this.game = game;
        this.playingField = playingField;
        this.startRow = startRow;
    }

    @Override
    public void execute() {
        for (int i = startRow; i >= 1; i--) {
            if (hasToBeRemoved(i) && data.getRows().contains(Game.blocks - i)) {
                // Row war voll und wurde geleert -> Gravitation auslösen
                playingField.gravitation(Game.blocks - i, !data.isFirstGravitationPlayed());
                data.setFirstGravitationPlayed(true);
            }
        }
        data.clear(); // clear after use
        game.checkPossibleMoves();
    }

    private boolean hasToBeRemoved(int i) {
        for (QPosition k : data.getExclusions()) {
            if (k.getY() == Game.blocks - i) {
                return false;
            }
        }
        return true;
    }
}
