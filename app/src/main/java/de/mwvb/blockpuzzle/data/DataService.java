package de.mwvb.blockpuzzle.data;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.zip.CRC32;

import de.mwvb.blockpuzzle.cluster.Cluster;
import de.mwvb.blockpuzzle.game.GameEngineFactory;
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;
import de.mwvb.blockpuzzle.gamedefinition.ILiberatedInfo;
import de.mwvb.blockpuzzle.gamestate.GamePlayState;
import de.mwvb.blockpuzzle.gamestate.Spielstand;
import de.mwvb.blockpuzzle.gamestate.SpielstandDAO;
import de.mwvb.blockpuzzle.global.Features;
import de.mwvb.blockpuzzle.global.GlobalData;
import de.mwvb.blockpuzzle.global.messages.MessageFactory;
import de.mwvb.blockpuzzle.global.messages.MessageObject;
import de.mwvb.blockpuzzle.planet.IPlanet;
import de.mwvb.blockpuzzle.planet.ISpaceObject;
import de.mwvb.blockpuzzle.planet.SpaceObjectState;
import de.mwvb.blockpuzzle.planet.SpaceObjectStateDAO;

/**
 * Multi player game state exchange
 *
 * The game state isn't exchanged via Internet. A data string is created that have to be exchanged manually using other software (e.g. WhatsApp).
 */
public class DataService {
    public static final String VERSION = "1";
    private final SpaceObjectStateDAO planetDAO = new SpaceObjectStateDAO();
    private final SpielstandDAO spielstandDAO = new SpielstandDAO();
    private String quadrant;

    public String get() {
        IPlanet planet = getPlanet();
        return get(planet.getClusterNumber(), planet.getCluster().getSpaceObjects(), planet);
    }

    private String get(int clusterNumber, List<ISpaceObject> spaceObjects, IPlanet currentPlanet) {
        quadrant = Cluster.getQuadrant(currentPlanet.getX(), currentPlanet.getY());
        StringBuilder sb = new StringBuilder();
        sb.append("BP");
        sb.append(VERSION);
        sb.append("/C");
        sb.append(clusterNumber);
        sb.append(("ß".equals(quadrant) ? "b" : quadrant));
        for (ISpaceObject so : spaceObjects) {
            if (ok(so) && so instanceof IPlanet) {
                IPlanet planet = (IPlanet) so;
                for (int gi = 0; gi < planet.getGameDefinitions().size(); gi++) {
                    Spielstand ss = spielstandDAO.load(planet, gi);
                    int moves = ss.getMoves();
                    if (moves > 0) {
                        sb.append(buildString(planet.getNumber(), gi, ss.getScore(), moves));
                    }
                }
            }
        }
        String code = code6(sb.toString());
        sb.append("/");
        sb.append(code);
        sb.append("//");
        String pn = getPlayerName();
        sb.append(pn.replace(" ", "_"));
        return splitLines(sb);
    }

    @NotNull
    protected String getPlayerName() {
        String pn = GlobalData.get().getPlayername();
        return pn == null ? "" : pn;
    }

    private boolean ok(ISpaceObject so) {
        if (!so.isDataExchangeRelevant()) {
            return false;
        }
        SpaceObjectState sos = planetDAO.load(so);
        return sos.isVisibleOnMap() && sos.isOwner() && Cluster.getQuadrant(so).equals(quadrant);
    }

    @NotNull
    private String splitLines(StringBuilder sb) {
        String ret = sb.toString();
        StringBuilder k = new StringBuilder();
        final int maxLineLen = 70;
        while (ret.length() > maxLineLen) {
            k.append(ret.substring(0, maxLineLen));
            k.append("\n");
            ret = ret.substring(maxLineLen);
        }
        k.append(ret);
        return k.toString();
    }

    public static String buildString(int planetNumber, int gi, int score, int moves) {
        return "/" + Integer.toHexString(planetNumber).toUpperCase() + "/" + gi + "/" + Integer.toHexString(score) + "/" + Integer.toHexString(moves);
    }

    public MessageObject put(String data, MessageFactory messages) {
        IPlanet planet = getPlanet();
        return put(data, planet.getClusterNumber(), planet.getCluster().getSpaceObjects(), planet, messages);
    }

    private MessageObject put(String data, int clusterNumber, List<ISpaceObject> spaceObjects, IPlanet currentPlanet, MessageFactory messages) {
        final String origData = data; // just for debugging
        if (data != null && data.equals(get(clusterNumber, spaceObjects, currentPlanet))) {
            return messages.getPutData_makesNoSense();
        } else if (data == null || !data.startsWith("BP")) {
            return messages.getPutData_formatError1(); // unknown data
        } else if (!data.startsWith("BP" + VERSION + "/")) {
            return messages.getPutData_formatError2(); // version mismatch
        }
        String rhead = "BP" + VERSION + "/C" + clusterNumber;
        int headLength = rhead.length() + "a/".length();
        if (!data.startsWith(rhead + "a/") && !data.startsWith(rhead + "b/") && !data.startsWith(rhead + "c/") && !data.startsWith(rhead + "d/")) {
            return messages.getPutData_unknownCluster(); // or quadrant
        }
        data = data.replace("\n", "");
        int loo = data.lastIndexOf("//");
        String name = "";
        if (loo >= 0) {
            name = data.substring(loo + "//".length()).replace("_", " ");
            data = data.substring(0, loo);
        }
        int lo = data.lastIndexOf("/");
        String code = data.substring(lo + 1);
        data = data.substring(0, lo);
        if (!Features.developerMode) {
            String c6 = code6(data);
            if (!c6.equals(code)) {
                return messages.getPutData_checksumMismatch();
            }
        }
        if (headLength > data.length()) { // no planets
            return messages.getPutData_okay();
        } else {
            data = data.substring(headLength);
        }
        String[] w = data.split("/");
        if (w.length % 4 != 0) {
            return messages.getPutData_wrongPlanetData();
        }
        int newOwnerCount = 0;
        for (int i = 0; i < w.length; i += 4) {
            newOwnerCount += parse(w[i], w[i + 1], w[i + 2], w[i + 3], name, spaceObjects);
        }
        return newOwnerCount == 0 ? messages.getPutData_okay() : messages.getPutData_success();
    }

    private int parse(String p0, String gi0, String s, String m, String name, List<ISpaceObject> spaceObjects) {
        int newOwnerCount = 0;
        int pl = Integer.parseInt(p0, 16);
        int gi = Integer.parseInt(gi0);
        int otherScore = Integer.parseInt(s, 16);
        int otherMoves = Integer.parseInt(m, 16);
        for (int i = 0; i < spaceObjects.size(); i++) {
            ISpaceObject so = spaceObjects.get(i);

            if (so.isDataExchangeRelevant() && so.getNumber() == pl && so instanceof IPlanet) {
                IPlanet p = (IPlanet) so;
                Spielstand ss = spielstandDAO.load(p, gi);
                int meineScore = ss.getScore();
                int meineMoves = ss.getMoves();
                GameDefinition gd = p.getGameDefinitions().get(gi);
                if (gd.isLiberated(new MyLiberatedInfo(otherScore, otherMoves, meineScore, meineMoves))) {
                    System.out.println("NEW OWNER for planet #" + p.getNumber() + "/" + gi + ": " + name + " with score: " + otherScore + ", moves: " + otherMoves);
                    if (ss.getState() == GamePlayState.WON_GAME) {
                        if (gd.gameGoesOnAfterWonGame()) {
                            ss.setState(GamePlayState.PLAYING);
                            System.out.println("  set state from WON_GAME to PLAYING");
                        } else {
                            ss.setState(GamePlayState.LOST_GAME);
                            System.out.println("  set state from WON_GAME to LOST_GAME");
                        }
                    } // else: state stays the same
                    ss.setOwnerScore(otherScore);
                    ss.setOwnerMoves(otherMoves);
                    ss.setOwnerName(name);
                    spielstandDAO.save(p, gi, ss);

                    SpaceObjectState sos = planetDAO.load(so);
                    sos.setOwner(false);
                    planetDAO.save(so, sos);

                    newOwnerCount++;
                }
                return newOwnerCount;
            }
        }
        // PLanet nicht gefunden
        return 0;
    }

    @NotNull
    protected IPlanet getPlanet() {
        return new GameEngineFactory().getPlanet();
    }

    private static class MyLiberatedInfo implements ILiberatedInfo {
        private final int player1Score;
        private final int player1Moves;
        private final int player2Score;
        private final int player2Moves;

        public MyLiberatedInfo(int player1Score, int player1Moves, int player2Score, int player2Moves) {
            this.player1Score = player1Score;
            this.player1Moves = player1Moves;
            this.player2Score = player2Score;
            this.player2Moves = player2Moves;
        }

        @Override
        public int getPlayer1Score() {
            return player1Score;
        }

        @Override
        public int getPlayer1Moves() {
            return player1Moves;
        }

        @Override
        public int getPlayer2Score() {
            return player2Score;
        }

        @Override
        public int getPlayer2Moves() {
            return player2Moves;
        }

        @Override
        public boolean isPlayerIsPlayer1() {
            return false;
        }

        @Override
        public boolean isPlayingFieldEmpty() {
            return false;
        }
    }

    public static String code6(String str) {
        CRC32 crc = new CRC32();
        crc.update(str.getBytes());
        String ret = "000000" + Integer.toString((int) crc.getValue(), 36).toLowerCase().replace("-", "");
        return ret.substring(ret.length() - 6);
    }
}
