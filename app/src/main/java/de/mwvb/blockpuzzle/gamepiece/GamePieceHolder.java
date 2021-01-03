package de.mwvb.blockpuzzle.gamepiece;

import de.mwvb.blockpuzzle.persistence.GamePersistence;

public class GamePieceHolder {
    // Stammdaten
    /** 1, 2, 3, -1 */
    private final int index;

    // Zustand
    private GamePiece gamePiece;

    // Services
    private IGamePieceView view;
    private GamePersistence persistence;

    public GamePieceHolder(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setView(IGamePieceView view) {
        this.view = view;
    }

    public void setPersistence(GamePersistence persistence) {
        this.persistence = persistence;
    }

    public void load() {
        gamePiece = persistence.get().load(index);
        view.setGamePiece(gamePiece);
    }

    public void save() {
        persistence.get().save(index, gamePiece);
    }

    public void setGamePiece(GamePiece gamePiece) {
        this.gamePiece = gamePiece;
        view.setGamePiece(gamePiece);
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
        }
    }
}
