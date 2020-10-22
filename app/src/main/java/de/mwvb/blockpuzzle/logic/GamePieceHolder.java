package de.mwvb.blockpuzzle.logic;

import de.mwvb.blockpuzzle.logic.spielstein.GamePiece;
import de.mwvb.blockpuzzle.view.IGamePieceView;

public class GamePieceHolder {
    // Stammdaten
    private final int index;

    // Zustand
    private GamePiece gamePiece;

    // Services
    private IGamePieceView view;
    private IPersistence persistence;

    public GamePieceHolder(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setView(IGamePieceView view) {
        this.view = view;
    }

    public void setPersistence(IPersistence persistence) {
        this.persistence = persistence;
    }

    public void load() {
        gamePiece = persistence.load(index);
        view.setGamePiece(gamePiece);
    }

    public void setGamePiece(GamePiece gamePiece) {
        this.gamePiece = gamePiece;
        view.setGamePiece(gamePiece);
        persistence.save(index, gamePiece);
    }

    public GamePiece getGamePiece() {
        return gamePiece;
    }

    public void grey(boolean grey) {
        view.setGrey(grey);
    }
}
