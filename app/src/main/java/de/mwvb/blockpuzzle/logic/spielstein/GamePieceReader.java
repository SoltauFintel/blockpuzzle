package de.mwvb.blockpuzzle.logic.spielstein;

import java.util.ArrayList;
import java.util.List;

public class GamePieceReader {
    private static List<GamePiece> allGamePieces;

    public synchronized List<GamePiece> read() {
        if (allGamePieces == null) {
            allGamePieces = new ArrayList<>();
            String g = new GamePieces().getGamePieces();
            String lines[] = g.replace("\r", "").split("\n");
            GamePiece current = null;
            int y = 0;
            boolean read = true;
            for (String line : lines) {
                line = line.trim();
                if (read) {
                    if (line.isEmpty() || line.startsWith("//")) { // ignore
                    } else if ("/*".equals(line)) {
                        read = false;
                    } else if (line.startsWith("#")) { // name
                        if (current != null) {
                            if (y != 4) {
                                throw new RuntimeException("Wrong game piece definition!\n" +
                                        "There must be 5 rows for a game piece!");
                            }
                        }
                        current = new GamePiece();
                        current.setName(line.substring(1));
                        allGamePieces.add(current);
                        y = -1;
                    } else if (line.length() == 5) {
                        y++;
                        if (current == null) {
                            throw new RuntimeException("Wrong game piece definition!\n" +
                                    "Missing name for game piece.\nLine: " + line);
                        } else if (y > 4) {
                            throw new RuntimeException("Wrong game piece definition!\n" +
                                    "There must be 5 rows for a game piece!\nLine: " + line);
                        }
                        fill(line, y, current);
                    } else { // unknown
                        throw new RuntimeException("Wrong game pieces definition!\nUnknown line: " + line);
                    }
                } else {
                    if ("*/".equals(line)) {
                        read = true;
                    }
                }
            }
            if (current == null) {
                throw new RuntimeException("No game pieces defined!");
            } else if (y != 4) {
                throw new RuntimeException("Wrong game piece definition!\n" +
                        "There must be 5 rows for a game piece!");
            }
        }
        return allGamePieces;
    }

    private void fill(final String definition, final int y, final GamePiece gamePiece) {
        for (int x = 0; x < 5; x++) {
            char c = definition.charAt(x);
            switch (c) {
                case '1': gamePiece.setBlockType(x, y, 1); break;
                case '.': gamePiece.setBlockType(x, y, 0); break;
                default:
                    throw new RuntimeException("Wrong game piece definition!\n" +
                            "Unsupported char: " + c + "\nline: " + definition);
            }
        }
    }

    public GamePiece read(String name) {
        List<GamePiece> ret = read();
        for (GamePiece p : ret) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        throw new RuntimeException("Game piece '" + name + "' doesn't exist!");
    }
}
