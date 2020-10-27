package de.mwvb.blockpuzzle.gamepiece;

import java.io.FileWriter;

import de.mwvb.blockpuzzle.block.BlockTypes;
import de.mwvb.blockpuzzle.playingfield.QPosition;

public class GamePieceSetGenerator {
    private final BlockTypes blockTypes = new BlockTypes(null);
    private static final int MAX = 30;

    @org.junit.Test
    public void generate() {
        StringBuilder all = new StringBuilder();
        all.append("package de.mwvb.blockpuzzle.gamepiece.sets;\n" +
                "\n" +
                "import de.mwvb.blockpuzzle.gamepiece.IGamePieceSet;\n" +
                "\n/** GENERATED */\n" +
                "public class AllGamePieceSets {\n" +
                "\n" +
                "    public static final IGamePieceSet[] sets = new IGamePieceSet[] {\n");
        for (int cl = 1; cl <= MAX; cl++) {
            INextGamePiece gen = new RandomGamePiece();
            int punkte = 0;
            int maxRunden = 1600;
            StringBuilder s = new StringBuilder();
            s.append("package de.mwvb.blockpuzzle.gamepiece.sets;\n\n");
            s.append("import de.mwvb.blockpuzzle.gamepiece.IGamePieceSet;\n\n");
            String a = "" + cl;
            while (a.length() < 4) a = "0" + a;
            s.append("/** GENERATED */\npublic class GamePieceSet" + a + " implements IGamePieceSet {\n\n");
            s.append("\t@Override\n");
            s.append("\tpublic String[] getGamePieceSet() {\n");
            s.append("\t\tString[] r = new String[" + maxRunden + "];\n");
            for (int i = 0; i < maxRunden; i++) {
                String runde = "";
                for (int j = 1; j <= 3; j++) {
                    GamePiece p = gen.next(punkte, blockTypes);
                    boolean special = false;
                    for (QPosition k : p.getAllFilledBlocks()) {
                        int blockType = p.getBlockType(k.getX(), k.getY());
                        if (blockType >= BlockTypes.MIN_SPECIAL && blockType <= BlockTypes.MAX_SPECIAL) {
                            special = true;
                            break;
                        }
                    }
                    if (special) {
                        runde += ":" + getStringPresentation(p);
                    } else {
                        runde += "#" + p.getName();
                    }
                    punkte += p.getPunkte();
                    // TODO -> Die Bonuspunkte fehlen dann (10 für full row, viele Rows abräumen, leerer Bereich)
                    // TODO wenn kein Special Block enthalten ist, könnte man für den Spielstein auch den GamePiece-Namen (ggf. eine kurze Kennung A-Z) verwenden.
                }
                s.append("\t\tr[" + i + "] = \"" + runde + "\";\n");
            }
            s.append("\treturn r;\n\t}\n");
            s.append("}\n");
            try (FileWriter w = new FileWriter("D:\\dev\\generates\\GamePieceSet" + a + ".java")) {
                w.write(s.toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            all.append("\t\tnew GamePieceSet" + a + "(),\n");
        }
        all.append("\t};\n\n}\n");
        try (FileWriter w = new FileWriter("D:\\dev\\generates\\AllGamePieceSets.java")) {
            w.write(all.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // only for test, not for Persistence
    private String getStringPresentation(GamePiece p) {
        StringBuilder ret = new StringBuilder();
        for (int y = 0; y < GamePiece.max; y++) {
            for (int x = 0; x < GamePiece.max; x++) {
                final int blockType = p.getBlockType(x, y);
                if (blockType == 0) {
                    ret.append('.');
                } else {
                    char blockTypeChar = blockTypes.getBlockTypeChar(blockType);
                    ret.append(blockTypeChar);
                }
            }
        }
        return ret.toString();
    }
}

