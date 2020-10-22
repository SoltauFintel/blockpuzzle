package de.mwvb.blockpuzzle.gamepiece;

import de.mwvb.blockpuzzle.gamepiece.GamePiece;

public interface IGamePieceView {

    void setGamePiece(GamePiece v);

    GamePiece getGamePiece();

    int getIndex();

    // Methode nicht löschen! Die wird als isGrey in MainActivty verwendet.
    void setGrey(boolean v);

    // Methode nicht löschen!
//    boolean isGrey();

    void draw();

    void startDragMode();

    void endDragMode();

    void setDrehmodus(boolean d);

    void rotate();

    void write(); // TODO save

    void read(); // TODO load
}
