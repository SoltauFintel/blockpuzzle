package de.mwvb.blockpuzzle.game.place;

import de.mwvb.blockpuzzle.gamestate.GameState;
import de.mwvb.blockpuzzle.gamestate.Spielstand;
import de.mwvb.blockpuzzle.playingfield.PlayingField;

public class EmptyScreenBonusPlaceAction implements IPlaceAction {

    @Override
    public void perform(PlaceActionModel info) {
        GameState gs = info.getGs();
        checkEmptyScreenBonusUnlocked(info, gs);
        checkEmptyScreenOccurred(info, gs);
    }

    private void checkEmptyScreenBonusUnlocked(PlaceActionModel info, GameState gs) {
        Spielstand ss = gs.get();
        PlayingField playingField = info.getPlayingField();

        if (!ss.isEmptyScreenBonusActive() && playingField.getFilled() > (info.getBlocks() * info.getBlocks() * 0.40f)) { // More than 40% filled: fewGamePiecesOnThePlayingField bonus is active
            ss.setEmptyScreenBonusActive(true);
            gs.save();
            info.playMoreThan40PercentSound();
        }
    }

    private void checkEmptyScreenOccurred(PlaceActionModel info, GameState gs) {
        if (info.getFilledRows().getHits() > 0 && gs.get().isEmptyScreenBonusActive()) {
            // Es gibt einen Bonus, wenn nach dem Abräumen von Rows nur noch wenige Spielsteine auf dem Spielfeld sind.
            int bonus = 0;
            switch (info.getPlayingField().getFilled()) {
                case 0:
                    bonus = 444;
                    break;
                case 1:
                    bonus = 111;
                    break;
            }
            if (bonus > 0) {
                gs.addScore(bonus);
                gs.get().setEmptyScreenBonusActive(false);
                gs.save();
                info.playEmptyScreenBonusSound();
            }
        }
    }
}
