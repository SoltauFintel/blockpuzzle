package de.mwvb.blockpuzzle.planet;

import de.mwvb.blockpuzzle.global.AbstractDAO;

public final class SpaceObjectStateDAO extends AbstractDAO<SpaceObjectState> {

    public SpaceObjectState load(ISpaceObject spaceObject) {
        return load(spaceObject.getId());
    }

    public void save(ISpaceObject spaceObject, SpaceObjectState data) {
        save(spaceObject.getId(), data);
    }

    public void delete(ISpaceObject spaceObject) {
        delete(spaceObject.getId());
    }

    @Override
    protected Class<SpaceObjectState> getTClass() {
        return SpaceObjectState.class;
    }
}
