package de.mwvb.blockpuzzle.planet;

public abstract class AbstractSpaceObject implements ISpaceObject {
    private final int number;
    private final int x;
    private final int y;
    private boolean visibleOnMap = true;

    public AbstractSpaceObject(int number, int x, int y) {
        this.number = number;
        this.x = x;
        this.y = y;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public int getClusterNumber() {
        return 1;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public boolean isVisibleOnMap() {
        return visibleOnMap;
    }

    @Override
    public void setVisibleOnMap(boolean v) {
        visibleOnMap = v;
    }
}
