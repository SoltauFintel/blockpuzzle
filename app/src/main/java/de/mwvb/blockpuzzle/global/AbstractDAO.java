package de.mwvb.blockpuzzle.global;

import android.app.Activity;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public abstract class AbstractDAO<T> {
    private static String dir;

    /**
     * Each activity.onCreate() method should call this method.
     * @param activity -
     */
    public static void init(Activity activity) {
        dir = activity.getFilesDir().getAbsolutePath();
    }

    protected abstract Class<T> getTClass();

    protected void save(String id, T o) {
        try (FileWriter w = new FileWriter(file(id))) {
            new Gson().toJson(o, w);
        } catch (IOException e) {
            throw new RuntimeException("Error saving " + getTClass().getSimpleName() + " object. ID: " + id, e);
        }
    }

    protected T load(String id) {
        File file = file(id);
        if (!file.exists()) {
            try {
                return getTClass().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("load: error creating empty " + getTClass().getSimpleName() + " object", e);
            }
        }
        try (FileReader r = new FileReader(file)) {
            return new Gson().fromJson(r, getTClass());
        } catch (IOException e) {
            throw new RuntimeException("Error loading " + getTClass().getSimpleName() + " object with ID " + id, e);
        }
    }

    protected boolean delete(String id) {
        return file(id).delete();
    }

    @SuppressWarnings("SameParameterValue")
    protected boolean exists(String id) {
        return file(id).exists();
    }

    /**
     * @param id file name valid String, without '/' or '\', not null, not empty
     */
    private File file(String id) {
        if (dir == null) {
            throw new RuntimeException("AbstractDAO.init was not called by activity!");
        }
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("id must not be null or empty in " + this.getClass().getSimpleName() + ".file()!");
        }
        return new File(dir + "/" + getTClass().getSimpleName() + "-" + id + ".json");
    }
}
