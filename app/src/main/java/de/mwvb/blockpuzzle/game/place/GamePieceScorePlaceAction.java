package de.mwvb.blockpuzzle.game.place;

/**
 * Score for placing game piece on playing field
 */
public class GamePieceScorePlaceAction implements IPlaceAction {

    @Override
    public void perform(PlaceActionModel info) {
        info.getGs().addScore(info.getGamePiece().getScore() * info.getDefinition().getGamePieceBlocksScoreFactor());
    }
}
