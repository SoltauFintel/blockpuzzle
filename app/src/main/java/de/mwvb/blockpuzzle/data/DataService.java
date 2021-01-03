package de.mwvb.blockpuzzle.data;

import java.util.List;
import java.util.zip.CRC32;

import de.mwvb.blockpuzzle.Features;
import de.mwvb.blockpuzzle.R;
import de.mwvb.blockpuzzle.cluster.Cluster;
import de.mwvb.blockpuzzle.gamedefinition.GameDefinition;
import de.mwvb.blockpuzzle.gamedefinition.ResourceAccess;
import de.mwvb.blockpuzzle.persistence.IPersistence;
import de.mwvb.blockpuzzle.persistence.PlanetAccess;
import de.mwvb.blockpuzzle.persistence.PlanetAccessFactory;
import de.mwvb.blockpuzzle.planet.DailyPlanet;
import de.mwvb.blockpuzzle.planet.IPlanet;
import de.mwvb.blockpuzzle.planet.ISpaceObject;

/**
 * Multi player game state exchange
 *
 * The game state isn't exchanged via Internet. A data string is created that have to be exchanged manually using other software (e.g. WhatsApp).
 */
public class DataService {
    public static final String VERSION = "1";

    public String get(IPersistence persistence) {
        PlanetAccess pa = PlanetAccessFactory.getPlanetAccess(persistence);
        return get(pa.getClusterNumber(), pa.getSpaceObjects(), pa.getPlanet(), persistence);
    }

    private String get(int clusterNumber, List<ISpaceObject> spaceObjects, IPlanet currentPlanet, IPersistence persistence) {
        String quadrant = Cluster.getQuadrant(currentPlanet.getX(), currentPlanet.getY());
        String ret = "BP" + VERSION + "/C" + clusterNumber + ("ß".equals(quadrant) ? "b" : quadrant);
        for (ISpaceObject so : spaceObjects) {
            if (!so.isDataExchangeRelevant() || !so.isVisibleOnMap() || !so.isOwner()) {
                continue;
            }
            String q = Cluster.getQuadrant(so);
            if (!q.equals(quadrant)) {
                continue;
            }
            if (so instanceof IPlanet) { // Only IPlanets know about game definitions.
                IPlanet p = (IPlanet) so;
                for (int gi = 0; gi < p.getGameDefinitions().size(); gi++) {
                    persistence.setGameID(p, gi);
                    int score = persistence.loadScore();
                    int moves = persistence.loadMoves();
                    if (moves > 0) {
                        ret += buildString(p.getNumber(), gi, score, moves);
                    }
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

    public static String buildString(int planetNumber, int gi, int score, int moves) {
        return "/" + Integer.toHexString(planetNumber).toUpperCase() + "/" + gi + "/" + Integer.toHexString(score) + "/" + Integer.toHexString(moves);
    }

    public String put(String data, IPersistence persistence, ResourceAccess resources) {
        PlanetAccess pa = PlanetAccessFactory.getPlanetAccess(persistence);
        return put(data, pa.getClusterNumber(), pa.getSpaceObjects(), pa.getPlanet(), persistence, resources);
    }

    private String put(String data, int clusterNumber, List<ISpaceObject> spaceObjects, IPlanet currentPlanet, IPersistence persistence, ResourceAccess resources) {
        if (data != null && data.equals(get(clusterNumber, spaceObjects, currentPlanet, persistence))) {
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
            newOwnerCount += parse(w[i], w[i + 1], w[i + 2], w[i + 3], name, spaceObjects, persistence);
        }
        return resources.getString(newOwnerCount == 0 ? R.string.putData_okay : R.string.putData_success);
    }

    private int parse(String p0, String gi0, String s, String m, String name, List<ISpaceObject> spaceObjects, IPersistence persistence) {
        int newOwnerCount = 0;
        int pl = Integer.parseInt(p0, 16);
        int gi = Integer.parseInt(gi0);
        int otherScore = Integer.parseInt(s, 16);
        int otherMoves = Integer.parseInt(m, 16);
        for (int i = 0; i < spaceObjects.size(); i++) {
            ISpaceObject so = spaceObjects.get(i);

            if (so.isDataExchangeRelevant() && so.getNumber() == pl && so instanceof IPlanet) {
                IPlanet p = (IPlanet) so;
                persistence.setGameID(p, gi);
                int meineScore = persistence.loadScore();
                int meineMoves = persistence.loadMoves();
                GameDefinition gd = p.getGameDefinitions().get(gi);
                if (gd.isLiberated(otherScore, otherMoves, meineScore, meineMoves, persistence, false)) {
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

    public static String code6(String str) {
        CRC32 crc = new CRC32();
        crc.update(str.getBytes());
        String ret = "000000" + Integer.toString((int) crc.getValue(), 36).toLowerCase().replace("-", "");
        return ret.substring(ret.length() - 6);
    }
}
