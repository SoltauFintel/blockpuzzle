package de.mwvb.blockpuzzle.game.stonewars.place;

import de.mwvb.blockpuzzle.game.GameEngineInterface;
import de.mwvb.blockpuzzle.game.GameInfoService;
import de.mwvb.blockpuzzle.game.place.IPlaceAction;
import de.mwvb.blockpuzzle.game.place.PlaceActionModel;
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;
import de.mwvb.blockpuzzle.gamestate.GamePlayState;
import de.mwvb.blockpuzzle.gamestate.ScoreChangeInfo;
import de.mwvb.blockpuzzle.gamestate.Spielstand;
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
            if (msg.isWonGame()) {
                info.getGs().get().setState(GamePlayState.WON_GAME);
                check4Liberation(info.getGameEngineInterface(), swgs);
                info.playSound(3); // ggf. einmalig beim Statuswechsel einen Sound abspielen
            } else if (msg.isLostGame()) {
                info.getGameEngineInterface().onLostGame();
                info.playSound(4); // ggf. einmalig beim Statuswechsel einen Sound abspielen
                return;
            }
        }

        // 2nd verification ----
        if (info.getPlayingField().getFilled() == 0 && swgs.getDefinition().onEmptyPlayingField()) {
            gameOverOnEmptyPlayingField(info);
        }
    }

    public void handleNoGamePieces(StoneWarsGameState gs, GameEngineInterface gameEngineInterface) {
        Spielstand ss = gs.get();
        GameDefinition definition = gs.getDefinition();
        if (definition.isWonAfterNoGamePieces(ss)) { // TODO Das testen, und zwar im Zusammenhang mit gameGoesOnAfterWonGame=true
            ss.setState(GamePlayState.WON_GAME);
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

    private void gameOverOnEmptyPlayingField(PlaceActionModel info) {
        StoneWarsGameState swgs = (StoneWarsGameState) info.getGs();
        info.getGameEngineInterface().clearAllHolders();
        swgs.get().setState(GamePlayState.WON_GAME);
        info.getPlayingField().gameOver();
        Spielstand ss = info.getGs().get();
        if (swgs.getDefinition().isLiberated(ss.getScore(), ss.getMoves(), ss.getOwnerScore(), ss.getOwnerMoves(),
                false/*playing field is really empty*/, swgs.getPlanet(), swgs.getIndex())) {
            // Folgende Aktionen dürfen nur bei einem 1-Game-Planet gemacht werden! Ein Cleaner Game wird aber auch nur bei 1-Game-Planets angeboten.
            // Daher passt das.
            swgs.setOwnerToMe();
            check4Liberation(info.getGameEngineInterface(), swgs);
        }
        info.playSound(3);
        info.getMessages().getPlanetLiberated().show();
    }
}
