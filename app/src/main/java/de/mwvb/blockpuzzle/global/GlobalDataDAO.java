package de.mwvb.blockpuzzle.global;

public final class GlobalDataDAO extends AbstractDAO<GlobalData> {
    private static final String ID = "1";

    public boolean exists() {
        return exists(ID);
    }

    public GlobalData load() {
        return load(ID);
    }

    public void save(GlobalData d) {
        save(ID, d);
    }

    /** DANGER! */
    public void delete() {
        delete(ID);
    }

    @Override
    protected Class<GlobalData> getTClass() {
        return GlobalData.class;
    }
}
