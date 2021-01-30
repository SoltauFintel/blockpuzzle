package de.mwvb.blockpuzzle.game.stonewars.place;

import de.mwvb.blockpuzzle.game.GameEngineInterface;
import de.mwvb.blockpuzzle.game.GameInfoService;
import de.mwvb.blockpuzzle.game.place.IPlaceAction;
import de.mwvb.blockpuzzle.game.place.PlaceInfo;
import de.mwvb.blockpuzzle.gamestate.GamePlayState;
import de.mwvb.blockpuzzle.gamestate.ScoreChangeInfo;
import de.mwvb.blockpuzzle.gamestate.Spielstand;
import de.mwvb.blockpuzzle.gamestate.StoneWarsGameState;
import de.mwvb.blockpuzzle.global.messages.MessageObjectWithGameState;
import de.mwvb.blockpuzzle.planet.IPlanet;

/**
 * Stone Wars: Prüfungen ob Spiel gewonnen oder verloren
 */
public class Check4VictoryPlaceAction implements IPlaceAction {
    // TODO Prüfen, ob ich Code von hier in die XXXGameDefinition verschieben kann. Classic/Cleaner-spezifisches soll hier ja nicht stehen.

    @Override
    public void perform(PlaceInfo info) {
        // check4Victory: // Spielsiegprüfung (showScore erst danach)
        StoneWarsGameState swgs = (StoneWarsGameState) info.getGs();
        ScoreChangeInfo scInfo = new ScoreChangeInfo(swgs, info.getMessages());
        MessageObjectWithGameState msg = info.getDefinition().scoreChanged(scInfo);
        if (!msg.isLostGame()) {
            msg.show();
            if (msg.isWonGame()) {
                info.getGs().get().setState(GamePlayState.WON_GAME);
                check4Liberation(info.getGameEngineInterface(), swgs);
            }
        }
        if (info.getPlayingField().getFilled() == 0 && info.getDefinition().onEmptyPlayingField()) {
            gameOverOnEmptyPlayingField(info);
        } else if (msg.isLostGame()) { // Game over?
            msg.show();
            info.getGameEngineInterface().onGameOver();
        }
    }

    public void handleNoGamePieces(StoneWarsGameState gs, GameEngineInterface gei) {
        if (!gs.isGameOver()) {
            if (gs.getDefinition().isWonAfterNoGamePieces(gs.get())) {
                gs.get().setState(GamePlayState.WON_GAME);
            }
            if (gs.get().getState() == GamePlayState.WON_GAME && gs.getPlanet().getGameDefinitions().size() == 1) {
                // Player has liberated planet.
                gs.setOwnerToMe();
                new Check4VictoryPlaceAction().check4Liberation(gei, (StoneWarsGameState) gs);
            }
        }
    }

    private void check4Liberation(GameEngineInterface gei, StoneWarsGameState gs) {
        gei.save();
        IPlanet planet = gs.getPlanet();
        if (new GameInfoService().isPlanetFullyLiberated(planet)) {
            new GameInfoService().executeLiberationFeature(planet);
        }
    }

    private void gameOverOnEmptyPlayingField(PlaceInfo info) {
        StoneWarsGameState swgs = (StoneWarsGameState) info.getGs();
        info.getGameEngineInterface().clearAllHolders();
        info.getGs().get().setState(GamePlayState.WON_GAME); // old code:; won=true, gameOver=true
        info.getPlayingField().gameOver();
        Spielstand ss = info.getGs().get();
        if (info.getDefinition().isLiberated(ss.getScore(), ss.getMoves(), ss.getOwnerScore(), ss.getOwnerMoves(),
                false/*playing field is really empty*/, swgs.getPlanet(), swgs.getIndex())) {
            // Folgende Aktionen dürfen nur bei einem 1-Game-Planet gemacht werden! Ein Cleaner Game wird aber auch nur bei 1-Game-Planets angeboten.
            // Daher passt das.
            swgs.setOwnerToMe();
            check4Liberation(info.getGameEngineInterface(), swgs);
        }
        info.getMessages().getPlanetLiberated().show();
    }
}
