package de.mwvb.blockpuzzle.gamepiece;

import de.mwvb.blockpuzzle.gamestate.Spielstand;

public class GamePieceHolder {
    // Stammdaten
    /** 1, 2, 3, -1 */
    private final int index;

    // Zustand
    private GamePiece gamePiece;

    // Services
    private IGamePieceView view;

    public GamePieceHolder(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setView(IGamePieceView view) {
        this.view = view;
    }

    public void load(Spielstand ss) {
        gamePiece = null;
        String d = ss.getGamePieceView(index);
        if (d != null && !d.isEmpty()) {
            gamePiece = new GamePiece();
            String[] w = d.split(",");
            int i = 0;
            for (int x = 0; x < GamePiece.max; x++) {
                for (int y = 0; y < GamePiece.max; y++) {
                    gamePiece.setBlockType(x, y, Integer.parseInt(w[i++]));
                }
            }
        }
        view.setGamePiece(gamePiece);
    }

    public void save(Spielstand ss) {
        StringBuilder d = new StringBuilder();
        if (gamePiece != null) {
            String k = "";
            for (int x = 0; x < GamePiece.max; x++) {
                for (int y = 0; y < GamePiece.max; y++) {
                    d.append(k);
                    k = ",";
                    d.append(gamePiece.getBlockType(x, y));
                }
            }
        }
        ss.setGamePieceView(index, d.toString());
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
