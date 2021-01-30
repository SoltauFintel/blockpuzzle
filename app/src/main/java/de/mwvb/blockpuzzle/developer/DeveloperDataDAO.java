package de.mwvb.blockpuzzle.developer;

import de.mwvb.blockpuzzle.persistence.AbstractDAO;

public class DeveloperDataDAO extends AbstractDAO<DeveloperData> {
    private static final String ID = "1";

    public DeveloperData load() {
        return load(ID);
    }

    public void save(DeveloperData d) {
        save(ID, d);
    }

    public void delete() {
        delete(ID);
    }

    @Override
    protected Class<DeveloperData> getTClass() {
        return DeveloperData.class;
    }
}
