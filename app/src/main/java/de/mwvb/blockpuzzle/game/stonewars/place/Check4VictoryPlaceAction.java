package de.mwvb.blockpuzzle.game.stonewars.place;

import de.mwvb.blockpuzzle.game.GameEngineInterface;
import de.mwvb.blockpuzzle.game.GameInfoService;
import de.mwvb.blockpuzzle.game.place.IPlaceAction;
import de.mwvb.blockpuzzle.game.place.PlaceActionModel;
import de.mwvb.blockpuzzle.gamedefinition.SSLiberatedInfo;
import de.mwvb.blockpuzzle.gamestate.GamePlayState;
import de.mwvb.blockpuzzle.gamestate.ScoreChangeInfo;
import de.mwvb.blockpuzzle.gamestate.StoneWarsGameState;
import de.mwvb.blockpuzzle.global.messages.MessageObjectWithGameState;
import de.mwvb.blockpuzzle.planet.IPlanet;

/**
 * Stone Wars: Prüfungen ob Spiel gewonnen oder verloren.
 * Weiterhin Behandlung für den Fall, dass keine Spielsteine mehr verfügbar sind.
 */
public class Check4VictoryPlaceAction implements IPlaceAction {
    // TODO Prüfen, ob ich Code von hier in die XXXGameDefinition verschieben kann. Classic/Cleaner-spezifisches soll hier ja nicht stehen.

    @Override
    public void perform(PlaceActionModel info) {
        // check4Victory: // Spielsiegprüfung (showScore erst danach)
        StoneWarsGameState swgs = (StoneWarsGameState) info.getGs();
        final GamePlayState oldState = swgs.get().getState();
        ScoreChangeInfo scInfo = new ScoreChangeInfo(swgs, info.getMessages());

        // 1st verification ----
        MessageObjectWithGameState msg = swgs.getDefinition().scoreChanged(scInfo);
        if (oldState == GamePlayState.PLAYING) {
            msg.show();
            boolean stopGame = true;
            if (msg.isWonGame()) { // z.B. wenn Mindestscore erreicht
                check4Liberation(info.getGameEngineInterface(), swgs);
                stopGame = !swgs.getDefinition().gameGoesOnAfterWonGame();
                info.getGameEngineInterface().onEndGame(true, stopGame);
                if (stopGame) return;
            } else if (msg.isLostGame()) { // z.B. wenn zu viele Moves
                info.getGameEngineInterface().onEndGame(false, true);
                return;
            }
        }

        // 2nd verification ----
        if (info.getPlayingField().getFilled() == 0 && swgs.getDefinition().wonGameIfPlayingFieldIsEmpty()) {
            gameOverOnEmptyPlayingField(info);
        }
    }

    protected void check4Liberation(GameEngineInterface game, StoneWarsGameState gs) {
        game.save();
        IPlanet planet = gs.getPlanet();
        if (new GameInfoService().isPlanetFullyLiberated(planet)) {
            new GameInfoService().executeLiberationFeature(planet);
        }
    }

    private void gameOverOnEmptyPlayingField(PlaceActionModel info) {
        StoneWarsGameState swgs = (StoneWarsGameState) info.getGs();
        info.getGameEngineInterface().clearAllHolders();
        info.getPlayingField().gameOver();
        if (isLiberated(swgs)) {
            // Folgende Aktionen dürfen nur bei einem 1-Game-Planet gemacht werden! Ein Cleaner Game wird aber
            // auch nur bei 1-Game-Planets angeboten. Daher passt das.
            swgs.setOwnerToMe();
            check4Liberation(info.getGameEngineInterface(), swgs);
        }
        info.getGameEngineInterface().onEndGame(true, true);
        info.getMessages().getPlanetLiberated().show();
    }

    private boolean isLiberated(StoneWarsGameState swgs) {
        SSLiberatedInfo libInfo = new SSLiberatedInfo(swgs.get()) {
            @Override
            public boolean isPlayingFieldEmpty() {
                return true; // playing field is really empty
            }
        };
        return swgs.getDefinition().isLiberated(libInfo);
    }
}
