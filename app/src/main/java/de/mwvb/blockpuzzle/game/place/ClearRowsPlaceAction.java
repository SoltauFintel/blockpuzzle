package de.mwvb.blockpuzzle.game.place;

import de.mwvb.blockpuzzle.game.GameEngineInterface;
import de.mwvb.blockpuzzle.global.Features;
import de.mwvb.blockpuzzle.playingfield.PlayingField;
import de.mwvb.blockpuzzle.playingfield.gravitation.GravitationAction;
import de.mwvb.blockpuzzle.playingfield.gravitation.GravitationData;

public class ClearRowsPlaceAction implements IPlaceAction {
    private final int gravitationStartRow;

    public ClearRowsPlaceAction(int gravitationStartRow) {
        this.gravitationStartRow = gravitationStartRow;
    }

    @Override
    public void perform(PlaceInfo info) {
        info.getGravitation().set(info.getFilledRows());

        if (Features.shakeForGravitation) { // gravity needs phone shaking
            info.getPlayingField().clearRows(info.getFilledRows(), null);
        } else { // auto-gravity
            GravitationAction gravitationAction = new GravitationAction(info.getGravitation(), info.getGameEngineInterface(), info.getPlayingField(), gravitationStartRow);
            info.getPlayingField().clearRows(info.getFilledRows(), gravitationAction);
            // Action wird erst wenige Millisekunden später fertig!
        }
    }

    public void executeGravitation(GravitationData gravitation, GameEngineInterface possibleMovesChecker, PlayingField playingField) {
        new GravitationAction(gravitation, possibleMovesChecker, playingField, gravitationStartRow).execute();
    }
}
