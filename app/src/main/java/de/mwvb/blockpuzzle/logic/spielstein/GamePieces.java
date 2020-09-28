package de.mwvb.blockpuzzle.logic.spielstein;

import java.util.List;

public class GamePieces {
    private static List<GamePiece> allGamePieces;

    public static synchronized List<GamePiece> get() {
        if (allGamePieces == null) {
            allGamePieces = new GamePieceParser().parse(new GamePiecesDefinition().getGamePieces());
        }
        return allGamePieces;
    }

    public static GamePiece find(String name) {
        List<GamePiece> ret = get();
        for (GamePiece p : ret) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        throw new RuntimeException("Game piece '" + name + "' doesn't exist!");
    }
}

