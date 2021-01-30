package de.mwvb.blockpuzzle.cluster;

import java.util.HashMap;
import java.util.Map;

import de.mwvb.blockpuzzle.planet.IPlanet;
import de.mwvb.blockpuzzle.planet.ISpaceObject;
import de.mwvb.blockpuzzle.planet.SpaceObjectState;
import de.mwvb.blockpuzzle.planet.SpaceObjectStateDAO;

public class SpaceObjectStates {
    private final Map<ISpaceObject, SpaceObjectState> map = new HashMap<>();

    public SpaceObjectStates() {
        SpaceObjectStateDAO planetDAO = new SpaceObjectStateDAO();
        for (ISpaceObject spaceObject : Cluster1.INSTANCE.getSpaceObjects()) {
            map.put(spaceObject, planetDAO.load(spaceObject));
        }
    }

    public boolean isVisibleOnMap(ISpaceObject spaceObject) {
        SpaceObjectState ps = map.get(spaceObject);
        return ps != null && ps.isVisibleOnMap();
    }

    public boolean isOwner(IPlanet planet) {
        SpaceObjectState ps = map.get(planet);
        return ps != null && ps.isOwner();
    }
}
