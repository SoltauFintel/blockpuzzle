package de.mwvb.blockpuzzle.data;

import android.content.res.Resources;

import java.util.List;
import java.util.zip.CRC32;

import de.mwvb.blockpuzzle.Features;
import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.cluster.Cluster;
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;
import de.mwvb.blockpuzzle.persistence.IPersistence;
import de.mwvb.blockpuzzle.persistence.PlanetAccess;
import de.mwvb.blockpuzzle.planet.IPlanet;

/**
 * Multi player game state exchange
 *
 * The game state isn't exchanged via Internet. A data string is created that have to be exchanged manually using other software (e.g. WhatsApp).
 */
public class DataService {
    public static final String VERSION = "1";

    public String get(IPersistence persistence) {
        PlanetAccess pa = new PlanetAccess(persistence);
        return get(pa.getClusterNumber(), pa.getPlanets(), pa.getPlanet(), persistence);
    }

    private String get(int clusterNumber, List<IPlanet> planets, IPlanet currentPlanet, IPersistence persistence) {
        String quadrant = Cluster.getQuadrant(currentPlanet.getX(), currentPlanet.getY());
        String ret = "BP" + VERSION + "/C" + clusterNumber + ("ß".equals(quadrant) ? "b" : quadrant);
        for (IPlanet p : planets) {
            if (!p.isVisibleOnMap() || !p.isOwner()) {
                continue;
            }
            String q = Cluster.getQuadrant(p.getX(), p.getY());
            if (!q.equals(quadrant)) {
                continue;
            }
            for (int gi = 0; gi < p.getGameDefinitions().size(); gi++) {
                persistence.setGameID(p, gi);
                int score = persistence.loadScore();
                int moves = persistence.loadMoves();
                if (moves > 0) {
                    ret += "/" + Integer.toHexString(p.getNumber()).toUpperCase() + "/" + gi + "/" + Integer.toHexString(score) + "/" + Integer.toHexString(moves);
                }
            }
        }
        ret = ret + "/" + code6(ret) + "//" + persistence.loadPlayerName().replace(" ", "_");
        String k = "";
        int kl = 70;
        while (ret.length() > kl) {
            k += ret.substring(0, kl) + "\n";
            ret = ret.substring(kl);
        }
        return k + ret;
    }

    public String put(String data, IPersistence persistence, Resources resources) {
        PlanetAccess pa = new PlanetAccess(persistence);
        return put(data, pa.getClusterNumber(), pa.getPlanets(), pa.getPlanet(), persistence, resources);
    }

    private String put(String data, int clusterNumber, List<IPlanet> planets, IPlanet currentPlanet, IPersistence persistence, Resources resources) {
        if (data != null && data.equals(get(clusterNumber, planets, currentPlanet, persistence))) {
            return resources.getString(R.string.putData_makesNoSense);
        } else if (data == null || !data.startsWith("BP")) {
            return resources.getString(R.string.putData_formatError1); // unknown data
        } else if (!data.startsWith("BP" + VERSION + "/")) {
            return resources.getString(R.string.putData_formatError2); // version mismatch
        }
        String rhead = "BP" + VERSION + "/C" + clusterNumber;
        int headLength = rhead.length() + "a/".length();
        if (!data.startsWith(rhead + "a/") && !data.startsWith(rhead + "b/") && !data.startsWith(rhead + "c/") && !data.startsWith(rhead + "d/")) {
            return resources.getString(R.string.putData_unknownCluster); // or quadrant
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
        if (!Features.developerMode && !code6(data).equals(code)) {
            return resources.getString(R.string.putData_checksumMismatch);
        }
        data = data.substring(headLength);
        String[] w = data.split("/");
        if (w.length % 4 != 0) {
            return resources.getString(R.string.putData_wrongPlanetData);
        }
        int newOwnerCount = 0;
        for (int i = 0; i < w.length; i += 4) {
            newOwnerCount += parse(w[i], w[i + 1], w[i + 2], w[i + 3], name, planets, persistence);
        }
        return resources.getString(newOwnerCount == 0 ? R.string.putData_okay : R.string.putData_success);
    }

    private int parse(String p0, String gi0, String s, String m, String name, List<IPlanet> planets, IPersistence persistence) {
        int newOwnerCount = 0;
        int pl = Integer.parseInt(p0, 16);
        int gi = Integer.parseInt(gi0);
        int otherScore = Integer.parseInt(s, 16);
        int otherMoves = Integer.parseInt(m, 16);
        for (int i = 0; i < planets.size(); i++) {
            IPlanet p = planets.get(i);
            if (p.getNumber() == pl) {
                persistence.setGameID(p, gi);
                int meineScore = persistence.loadScore();
                int meineMoves = persistence.loadMoves();
                GameDefinition gd = p.getGameDefinitions().get(gi);
                if (gd.isLiberated(otherScore, otherMoves, meineScore, meineMoves, persistence)) {
                    setOwner(p, gi, otherScore, otherMoves, name, persistence);
                    p.setOwner(false);
                    persistence.savePlanet(p);
                    newOwnerCount++;
                }
                return newOwnerCount;
            }
        }
        // PLanet nicht gefunden
        return 0;
    }

    private void setOwner(IPlanet p, int gi, int score, int moves, String name, IPersistence persistence) {
        System.out.println("NEW OWNER for planet #" + p.getNumber() + ": gi=" + gi + ", score: " + score + ", moves: " + moves + ", name: " + name);
        persistence.setGameID(p, gi);
        persistence.saveOwner(score, moves, name);
    }

    private static String code6(String str) {
        CRC32 crc = new CRC32();
        crc.update(str.getBytes());
        String ret = "000000" + Integer.toString((int) crc.getValue(), 36).toLowerCase().replace("-", "");
        return ret.substring(ret.length() - 6);
    }
}
