package de.mwvb.blockpuzzle.gamestate;

/**
 * Number of trophies
 *
 * ID: "C" + cluster number
 */
public class Trophies {
    private int bronze = 0;
    private int silver = 0;
    private int golden = 0;

    public int getBronze() {
        return bronze;
    }

    public void setBronze(int bronze) {
        this.bronze = bronze;
    }

    public int getSilver() {
        return silver;
    }

    public void setSilver(int silver) {
        this.silver = silver;
    }

    public int getGolden() {
        return golden;
    }

    public void setGolden(int golden) {
        this.golden = golden;
    }

    public void incBronze() {
        bronze++;
    }

    /** bronze medal becomes silver trophy */
    public void incSilver() {
        silver++;
        if (bronze > 0) {
            bronze--;
        }
    }

    /** silver trophy becomes golden trophy */
    public void incGolden() {
        golden++;
        if (silver > 0) {
            silver--;
        }
    }
}
