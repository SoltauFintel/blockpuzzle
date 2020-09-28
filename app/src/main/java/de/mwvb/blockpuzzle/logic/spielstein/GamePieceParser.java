package de.mwvb.blockpuzzle.logic.spielstein;

import java.util.ArrayList;
import java.util.List;

public class GamePieceParser {

    public List<GamePiece> parse(String definition) {
        final GPParseData data = new GPParseData();
        for (String line : definition.replace("\r", "").split("\n")) {
            line = line.trim();
            if (data.read) {
                parseLine(line, data);
            } else {
                if ("*/".equals(line)) {
                    data.read = true;
                }
            }
        }
        check(data);
        if (data.current == null) {
            throw new RuntimeException("No game pieces defined!");
        }
        return data.allGamePieces;
    }

    private void parseLine(String line, GPParseData data) {
        if (line.isEmpty() || line.startsWith("//")) {
            // ignore
        } else if ("/*".equals(line)) {
            data.read = false;
        } else if (line.startsWith("#")) { // name
            check(data);
            data.current = addNewGamePiece(line, data.allGamePieces);
            data.reset();
        } else {
            if (data.current == null) {
                throw new RuntimeException("Wrong game piece definition!\n" +
                        "(C) Missing name for game piece.\nLine: " + line);
            }
            if (line.length() == GamePiece.max && !line.contains("=")) { // game piece layout definition line
                if (++data.y > 4) {
                    throw new RuntimeException("Wrong game piece definition!\n" +
                            "(B) There must be " + GamePiece.max + " rows for a game piece!\n" +
                            "Line: " + line);
                }
                fill(line, data.y, data.current);
            } else {
                parseParameters(line, data);
            }
        }
    }

    private void parseParameters(String line, GPParseData data) {
        if (line.toLowerCase().startsWith("min=")) { // Mindestpunktzahl
            int min = Integer.parseInt(line.substring("min=".length()).trim());
            data.current.withMindestpunktzahl(min);

        } else if (line.toLowerCase().startsWith("n=")) { // produce number of game pieces
            data.n = Integer.parseInt(line.substring("n=".length()).trim());

        } else if (line.toLowerCase().startsWith("r=")) {
            // produce extra game piece that is rotated to the right once
            data.r = Integer.parseInt(line.substring("r=".length()).trim());

        } else if (line.toLowerCase().startsWith("rr=")) {
            // produce extra game piece that is rotated to the right twice
            data.rr = Integer.parseInt(line.substring("rr=".length()).trim());

        } else if (line.toLowerCase().startsWith("l=")) {
            // produce extra game piece that is rotated to the left once
            data.l = Integer.parseInt(line.substring("l=".length()).trim());

        } else { // unknown
            throw new RuntimeException("Wrong game pieces definition!\nUnknown line: " + line);
        }
    }

    private void check(GPParseData data) {
        if (data.current != null) {
            if (data.y != 4) {
                throw new RuntimeException("Wrong game piece definition!\n" +
                        "(A) There must be " + GamePiece.max + " rows for a game piece!");
            }
            for (int i = 2; i <= data.n; i++) {
                data.allGamePieces.add(data.current.copy());
            }
            for (int i = 0; i < data.r; i++) {
                data.allGamePieces.add(data.current.copy().rotateToRight());
            }
            for (int i = 0; i < data.rr; i++) {
                data.allGamePieces.add(data.current.copy().rotateToRight().rotateToRight());
            }
            for (int i = 0; i < data.l; i++) {
                data.allGamePieces.add(data.current.copy().rotateToLeft());
            }
        }
    }

    private GamePiece addNewGamePiece(String line, List<GamePiece> allGamePieces) {
        GamePiece gamePiece = new GamePiece();
        gamePiece.setName(line.substring(1));
        allGamePieces.add(gamePiece);
        return gamePiece;
    }

    private void fill(final String definition, final int y, final GamePiece gamePiece) {
        for (int x = 0; x < GamePiece.max; x++) {
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
}
