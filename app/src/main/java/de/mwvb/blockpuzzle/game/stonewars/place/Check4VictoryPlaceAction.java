package de.mwvb.blockpuzzle.game.stonewars.place;

import de.mwvb.blockpuzzle.game.GameEngineInterface;
import de.mwvb.blockpuzzle.game.GameInfoService;
import de.mwvb.blockpuzzle.game.place.IPlaceAction;
import de.mwvb.blockpuzzle.game.place.PlaceInfo;
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;
import de.mwvb.blockpuzzle.gamestate.GamePlayState;
import de.mwvb.blockpuzzle.gamestate.ScoreChangeInfo;
import de.mwvb.blockpuzzle.gamestate.Spielstand;
import de.mwvb.blockpuzzle.gamestate.SpielstandService;
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
    public void perform(PlaceInfo info) {
        // check4Victory: // Spielsiegprüfung (showScore erst danach)
        StoneWarsGameState swgs = (StoneWarsGameState) info.getGs();
        ScoreChangeInfo scInfo = new ScoreChangeInfo(swgs, info.getMessages());
        MessageObjectWithGameState msg = swgs.getDefinition().scoreChanged(scInfo);
        if (!msg.isLostGame()) {
            msg.show();
            if (msg.isWonGame()) {
                new SpielstandService().setSpielstandState(info.getGs().get(), GamePlayState.WON_GAME, swgs.getDefinition());
                check4Liberation(info.getGameEngineInterface(), swgs);
            }
        }
        if (info.getPlayingField().getFilled() == 0 && swgs.getDefinition().onEmptyPlayingField()) {
            gameOverOnEmptyPlayingField(info);
        } else if (msg.isLostGame()) { // Game over?
            msg.show();
            info.getGameEngineInterface().onGameOver();
        }
    }

    public void handleNoGamePieces(StoneWarsGameState gs, GameEngineInterface gameEngineInterface) {
        Spielstand ss = gs.get();
        GameDefinition definition = gs.getDefinition();
        if (definition.isWonAfterNoGamePieces(ss)) {
            new SpielstandService().setSpielstandState(ss, GamePlayState.WON_GAME, definition);
        }
        if (ss.getState() == GamePlayState.WON_GAME && gs.getPlanet().getGameDefinitions().size() == 1) {
            // Player has liberated planet.
            gs.setOwnerToMe();
            new Check4VictoryPlaceAction().check4Liberation(gameEngineInterface, (StoneWarsGameState) gs);
        } // TODO Muss der else Zweig behandelt werden? also gewonnen bei MultiTerritoriumPlanet?
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
        new SpielstandService().setSpielstandState(info.getGs().get(), GamePlayState.WON_GAME, swgs.getDefinition());
        info.getPlayingField().gameOver();
        Spielstand ss = info.getGs().get();
        if (swgs.getDefinition().isLiberated(ss.getScore(), ss.getMoves(), ss.getOwnerScore(), ss.getOwnerMoves(),
                false/*playing field is really empty*/, swgs.getPlanet(), swgs.getIndex())) {
            // Folgende Aktionen dürfen nur bei einem 1-Game-Planet gemacht werden! Ein Cleaner Game wird aber auch nur bei 1-Game-Planets angeboten.
            // Daher passt das.
            swgs.setOwnerToMe();
            check4Liberation(info.getGameEngineInterface(), swgs);
        }
        info.getMessages().getPlanetLiberated().show();
    }
}
