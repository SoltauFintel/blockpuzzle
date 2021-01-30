package de.mwvb.blockpuzzle.planet;

import de.mwvb.blockpuzzle.game.GameEngineFactory;

/**
 * Variable space object data entity
 *
 * ID: "C" + cluster number + "_" + planet number
 */
public class SpaceObjectState {
    private int version; // 1
    private boolean visibleOnMap = false;
    private boolean owner = false; // only needed for IPlanet

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isVisibleOnMap() {
        return visibleOnMap;
    }

    public void setVisibleOnMap(boolean visibleOnMap) {
        this.visibleOnMap = visibleOnMap;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    /**
     * @return SpaceObjectState (with visibleOnMap and owner) for current planet
     */
    public static SpaceObjectState get() {
        return new SpaceObjectStateDAO().load(new GameEngineFactory().getPlanet());
    }
}
