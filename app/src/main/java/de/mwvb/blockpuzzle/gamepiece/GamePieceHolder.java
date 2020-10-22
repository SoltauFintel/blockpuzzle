package de.mwvb.blockpuzzle.gamepiece;

import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.gamepiece.IGamePieceView;
import de.mwvb.blockpuzzle.persistence.IPersistence;

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

    public void rotate() {
        if (gamePiece != null) {
            gamePiece.rotateToRight();
            view.draw();
            persistence.save(index, gamePiece);
        }
    }
}
