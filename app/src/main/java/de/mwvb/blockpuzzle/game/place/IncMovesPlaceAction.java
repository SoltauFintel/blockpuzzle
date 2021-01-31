package de.mwvb.blockpuzzle.game.place;

public class IncMovesPlaceAction implements IPlaceAction {

    @Override
    public void perform(PlaceInfo info) {
        info.getGs().incMoves();
    }
}
