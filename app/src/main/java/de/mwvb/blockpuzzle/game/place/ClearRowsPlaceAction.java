package de.mwvb.blockpuzzle.game.place;

import de.mwvb.blockpuzzle.game.GameEngineInterface;
import de.mwvb.blockpuzzle.gamestate.GameState;
import de.mwvb.blockpuzzle.global.Features;
import de.mwvb.blockpuzzle.playingfield.FilledRows;
import de.mwvb.blockpuzzle.playingfield.PlayingField;
import de.mwvb.blockpuzzle.playingfield.gravitation.GravitationAction;
import de.mwvb.blockpuzzle.playingfield.gravitation.GravitationData;

/**
 * Clear rows: add score and prepare or execute clearing of rows
 */
public class ClearRowsPlaceAction implements IPlaceAction {

    @Override
    public void perform(PlaceActionModel info) {
        addScoreForClearedRows(info);
        if (Features.shakeForGravitation) { // gravity needs phone shaking
            prepareClearingOfRows(info);
        } else { // auto-gravity
            executeClearingOfRows(info);
        }
    }

    protected void addScoreForClearedRows(PlaceActionModel info) {
        GameState gs = info.getGs();
        FilledRows f = info.getFilledRows();
        gs.addScore(f.getHits() * info.getDefinition().getHitsScoreFactor());
        rowsAdditionalBonus(f.getXHits(), f.getYHits(), info);
    }

    protected void rowsAdditionalBonus(int xrows, int yrows, PlaceActionModel info) {
        if (!info.getDefinition().isRowsAdditionalBonusEnabled()) {
            return;
        }
        int bonus = 0;
        switch (xrows + yrows) {
            case 0:
            case 1: break; // 0-1 kein Bonus
            // Bonuspunkte wenn mehr als 2 Rows gleichzeitig abgeräumt werden.
            // Fällt mir etwas schwer zu entscheiden wieviel Punkte das jeweils wert ist.
            case 2:  bonus = 12; break;
            case 3:  bonus = 17; break;
            case 4:  bonus = 31; break;
            case 5:  bonus = 44; break;
            default: bonus = 22; break;
        }
        if (xrows > 0 && yrows > 0) {
            bonus += 10;
        }
        info.getGs().addScore(bonus);
        // TO-DO Reihe mit gleicher Farbe (ohne oldOneColor) könnte weiteren Bonus auslösen.
    }

    protected void prepareClearingOfRows(PlaceActionModel info) {
        info.getGravitation().set(info.getFilledRows());
        info.getPlayingField().clearRows(info.getFilledRows(), null);
    }

    protected void executeClearingOfRows(PlaceActionModel info) {
        info.getGravitation().set(info.getFilledRows());
        GravitationAction gravitationAction = new GravitationAction(info.getGravitation(), info.getGameEngineInterface(), info.getPlayingField(),
                info.getDefinition().getGravitationStartRow());
        info.getPlayingField().clearRows(info.getFilledRows(), gravitationAction);
        // Action wird erst wenige Millisekunden später fertig!
    }

    public void executeGravitation(GravitationData gravitation, GameEngineInterface possibleMovesChecker, PlayingField playingField, int gravitationStartRow) {
        new GravitationAction(gravitation, possibleMovesChecker, playingField, gravitationStartRow).execute();
    }
}
