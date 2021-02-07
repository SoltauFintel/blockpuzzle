package de.mwvb.blockpuzzle.game;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.mwvb.blockpuzzle.block.BlockTypes;
import de.mwvb.blockpuzzle.game.place.ClearRowsPlaceAction;
import de.mwvb.blockpuzzle.game.place.DetectOneColorAreaAction;
import de.mwvb.blockpuzzle.game.place.EmptyScreenBonusPlaceAction;
import de.mwvb.blockpuzzle.game.place.GamePieceScorePlaceAction;
import de.mwvb.blockpuzzle.game.place.IPlaceAction;
import de.mwvb.blockpuzzle.game.place.IncMovesPlaceAction;
import de.mwvb.blockpuzzle.game.place.SendPlacedEventAction;
import de.mwvb.blockpuzzle.game.place.SpecialBlockBonusPlaceAction;
import de.mwvb.blockpuzzle.gamedefinition.OldGameDefinition;
import de.mwvb.blockpuzzle.gamepiece.Holders;
import de.mwvb.blockpuzzle.gamepiece.INextGamePiece;
import de.mwvb.blockpuzzle.gamepiece.RandomGamePiece;
import de.mwvb.blockpuzzle.gamestate.GameState;
import de.mwvb.blockpuzzle.gamestate.Spielstand;
import de.mwvb.blockpuzzle.playingfield.PlayingField;
import de.mwvb.blockpuzzle.playingfield.gravitation.GravitationData;

/**
 * Initialization of GameEngine
 */
public class GameEngineBuilder {
    public static final int blocks = 10; // TO-DO später mal die Zugriffe hier drauf prüfen
    protected final BlockTypes blockTypes = new BlockTypes(null);
    protected IGameView view;
    protected GameState gs;
    protected OldGameDefinition definition;
    protected GravitationData gravitation;
    protected PlayingField playingField;
    protected Holders holders;
    protected INextGamePiece nextGamePiece;

    protected GameEngine gameEngine;

    public final GameEngine build(final IGameView pView) {
        view = pView;

        // Create data ----
        gs = createGameState();
        definition = provideDefinition();

        playingField = new PlayingField(blocks);
        playingField.setView(view.getPlayingFieldView());

        holders = new Holders(view);
        gravitation = new GravitationData();

        nextGamePiece = getNextGamePieceGenerator();

        // Create game engine ----
        GameEngineModel model = new GameEngineModel(blocks, blockTypes, view, gs, definition, playingField, holders, createPlaceActions(), gravitation, nextGamePiece);
        gameEngine = createGameEngine(model);

        // init game ----
        // Gibt es einen Spielstand?
        if (gs.get().getScore() < 0) { // Nein
            newGame(); // Neues Spiel starten!
        } else {
            loadGame(true, true); // Spielstand laden
        }
        return gameEngine;
    }

    // create data ----

    @NotNull
    protected GameState createGameState() {
        return GameState.create();
    }

    @NotNull
    protected OldGameDefinition provideDefinition() {
        // TODO Oder muss der GameState das hier bereitstellen??
        return new OldGameDefinition();
    }

    protected List<IPlaceAction> createPlaceActions() {
        List<IPlaceAction> ret = new ArrayList<>();
        ret.add(new SendPlacedEventAction());
        ret.add(getDetectOneColorAreaAction());
        ret.add(new IncMovesPlaceAction());
        ret.add(new GamePieceScorePlaceAction());
        ret.add(new SpecialBlockBonusPlaceAction());
        ret.add(new ClearRowsPlaceAction());
        ret.add(new EmptyScreenBonusPlaceAction());
        return ret;
    }

    @NotNull
    protected IPlaceAction getDetectOneColorAreaAction() {
        return new DetectOneColorAreaAction();
    }

    protected INextGamePiece getNextGamePieceGenerator() {
        return new RandomGamePiece();
    }

    // create game engine ----

    @NotNull
    protected GameEngine createGameEngine(GameEngineModel model) {
        return new GameEngine(model);
    }

    // new game ----

    /** Benutzer startet freiwillig oder nach GameOver neues Spiel. */
    protected void newGame() {
        doNewGame();
        gameEngine.offer(true);
        gameEngine.save(); // TO-DO gs.save() in doNewGame() und save() hier; schauen ob das anders geht
    }
    protected void doNewGame() {
        gs.newGame();
        gravitation.init();
        initNextGamePieceForNewGame();

        initPlayingField(playingField);
        view.showScoreAndMoves(gs.get());
        holders.clearParking();

        gs.save();
    }

    protected void initNextGamePieceForNewGame() {
        nextGamePiece.reset();
    }

    protected void initPlayingField(PlayingField playingField) {
        playingField.clear();
    }

    // load game ----

    // TODO später (wenn DeathStar Game funktioniert) mal prüfen, ob loadNextGamePiece wirklich benötigt wird, d.h. irgendwo false ist
    protected void loadGame(boolean loadNextGamePiece, boolean checkGame) {
        Spielstand ss = gs.get();
        view.showScoreAndMoves(ss);

        if (loadNextGamePiece) {
            nextGamePiece.load();
        }
        gravitation.load(ss);
        playingField.load(ss);
        holders.load(ss);

        if (checkGame) {
            gameEngine.checkGame();
        }
    }
}
