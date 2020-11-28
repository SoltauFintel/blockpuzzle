package de.mwvb.blockpuzzle.gamepiece;

import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;

import de.mwvb.blockpuzzle.block.BlockTypes;
import de.mwvb.blockpuzzle.game.TestGameBuilder;

public class GamePieceSetGenerator {
    private static final int MAX_GPS_FILES = 40;
    private static final int MAX_RUNDEN = 1600;
    private static final String PATH = "D:\\dev\\generates\\";
    private final BlockTypes blockTypes = new BlockTypes(null);
    private int bigBlocks;

//    @org.junit.Test
    public void generate() {
        StringBuilder all = generateHeader();
        for (int cl = 1; cl <= MAX_GPS_FILES; cl++) {
            generateGpsFile(cl);

            all.append("\t\tnew GamePieceSet");
            all.append(format(cl));
            all.append("(),\n");
        }
        all.append("\t};\n\n}\n");
        saveFile(all, "AllGamePieceSets");
        System.out.println("fertig");
    }

    private void generateGpsFile(int cl) {
        INextGamePiece gen = new RandomGamePiece();
        StringBuilder s = new StringBuilder();
        addHeader(cl, s);
        for (int i = 0; i < MAX_RUNDEN; i++) {
            // generate one round
            StringBuilder runde = new StringBuilder();
            bigBlocks = 0;
            for (int j = 1; j <= 3; j++) { // 3 game pieces per round
                GamePiece p = next(gen);
                if (p.hasSpecialBlock()) {
                    runde.append(":");
                    runde.append(TestGameBuilder.getStringPresentation(p, blockTypes, false)); // long presentation
                } else {
                    runde.append("#");
                    runde.append(p.getName()); // short presentation
                }
            }
            // add one round
            s.append("\t\tr[");
            s.append(i);
            s.append("] = \"");
            s.append(runde.toString());
            s.append("\";\n");
        }
        s.append("\t\treturn r;\n\t}\n}\n");
        saveFile(s, "GamePieceSet" + format(cl));
    }

    @NotNull
    private GamePiece next(INextGamePiece gen) {
        GamePiece p = gen.next(blockTypes);
        if (p.isBigBlock() && ++bigBlocks == 3) { // Prevent 3 times a big block (like 3x3) because this would be the ultimate game killer.
            int loop = 0;
            while (p.isBigBlock()) {
                if (++loop > 300) throw new RuntimeException("killer loop");
                p = gen.getOther(blockTypes);
            }
        }
        return p;
    }

    private StringBuilder generateHeader() {
        StringBuilder all = new StringBuilder();
        all.append("package de.mwvb.blockpuzzle.gamepiece.sets;\n" +
                "\n" +
                "import de.mwvb.blockpuzzle.gamepiece.IGamePieceSet;\n" +
                "\n/** GENERATED */\n" +
                "public class AllGamePieceSets {\n" +
                "\n" +
                "    public static final IGamePieceSet[] sets = new IGamePieceSet[] {\n");
        return all;
    }

    private void addHeader(int cl, StringBuilder s) {
        s.append("package de.mwvb.blockpuzzle.gamepiece.sets;\n\n");
        s.append("import de.mwvb.blockpuzzle.gamepiece.IGamePieceSet;\n\n");
        s.append("/** GENERATED */\npublic class GamePieceSet");
        s.append(format(cl));
        s.append(" implements IGamePieceSet {\n\n");
        s.append("\t@Override\n");
        s.append("\tpublic String[] getGamePieceSet() {\n");
        s.append("\t\tString[] r = new String[" + MAX_RUNDEN + "];\n");
    }

    private String format(int cl) {
        String a = "" + cl;
        while (a.length() < 4) {
            a = "0" + a;
        }
        return a;
    }

    private void saveFile(StringBuilder content, String filename) {
        try (FileWriter w = new FileWriter(PATH + filename + ".java")) {
            w.write(content.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

