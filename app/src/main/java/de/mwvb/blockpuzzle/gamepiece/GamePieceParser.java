package de.mwvb.blockpuzzle.gamepiece;

import java.util.ArrayList;
import java.util.List;

import de.mwvb.blockpuzzle.block.BlockTypes;

public class GamePieceParser {
    private final BlockTypes blockTypes = new BlockTypes(null);
    // TO-DO Denkbar wäre auch noch eine Maximalpunktzahl.

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
        //noinspection StatementWithEmptyBody
        if (line.isEmpty() || line.startsWith("//")) {
            // ignore
        } else if ("/*".equals(line)) {
            data.read = false;
        } else if (line.startsWith("#")) { // name
            check(data);
            addNewGamePiece(line, data);
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
            data.current.setMinimumMoves(min);

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

    private void addNewGamePiece(String line, GPParseData data) {
        data.reset();
        GamePiece gamePiece = new GamePiece();

        String title = line.substring(1);
        int o = title.indexOf(":");
        if (o >= 0) {
            String copyFrom = title.substring(o + 1).trim();
            for (GamePiece p : data.allGamePieces) {
                if (p.getName().equals(copyFrom)) {
                    copyMatrix(p, gamePiece);
                    data.y = 4;
                    break;
                }
            }
            title = title.substring(0, o).trim();
        }
        gamePiece.setName(title);

        data.allGamePieces.add(gamePiece);
        data.current = gamePiece;
    }

    private void copyMatrix(GamePiece from, GamePiece to) {
        for (int y = 0; y < GamePiece.max; y++) {
            for (int x = 0; x < GamePiece.max; x++) {
                to.setBlockType(x, y, from.getBlockType(x, y));
            }
        }
    }

    private void fill(final String definition, final int y, final GamePiece gamePiece) {
        for (int x = 0; x < GamePiece.max; x++) {
            char c = definition.charAt(x);
            int v = 0;
            if (c != '.') {
                v = blockTypes.toBlockType(c, definition);
            }
            gamePiece.setBlockType(x, y, v);
        }
    }

    /**
     * Game Piece Parse Data
     */
    static class GPParseData {
        public final List<GamePiece> allGamePieces = new ArrayList<>();
        /** false if comment active */
        public boolean read = true;
        public GamePiece current = null;
        /** row */
        public int y;
        /** save game piece n times (default 1) */
        public int n;
        /** copy right-rotated GP n times */
        public int r;
        /** copy twice right-rotated GP n times */
        public int rr;
        /** copy left-rotated GP n times */
        public int l;

        GPParseData() {
            reset();
        }

        public void reset() {
            y = -1;
            n = 1;
            r = 0;
            rr = 0;
            l = 0;
        }
    }
}
