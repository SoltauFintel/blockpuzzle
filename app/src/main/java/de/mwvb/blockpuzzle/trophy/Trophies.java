package de.mwvb.blockpuzzle.trophy;

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
}
