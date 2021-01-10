package de.mwvb.blockpuzzle.gamepiece;

public interface IGamePieceView {

    void setGamePiece(GamePiece v);

    GamePiece getGamePiece();

    int getIndex();

    // Methode nicht löschen! Die wird als isGrey in MainActivty verwendet.
    void setGrey(boolean v);

    void draw();

    void startDragMode();

    void endDragMode();
}
