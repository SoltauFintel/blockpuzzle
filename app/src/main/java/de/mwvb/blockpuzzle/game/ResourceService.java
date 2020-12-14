package de.mwvb.blockpuzzle.game;

import android.app.Activity;
import android.content.Context;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Stack;

import de.mwvb.blockpuzzle.gamedefinition.ResourceAccess;

public class ResourceService {

    /**
     * @param view null in tests
     * @param expectedResources null in production
     * @return Resources wrapper
     */
    @NotNull
    public ResourceAccess getResourceAccess(Object view, Stack<Integer> expectedResources) {
        ResourceAccess ret;
        if (view instanceof Activity) {
            ret = new ResourceAccess() {
                @Override
                public String getString(int resId) {
                    return ((Activity) view).getResources().getString(resId);
                }
            };
        } else { // testcases
            ret = new ResourceAccess() {
                @Override
                public String getString(int resId) {
                    if (expectedResources != null) {
                        if (expectedResources.isEmpty()) {
                            throw new RuntimeException("asked for resource: " + resId + " but no resource was expected! (empty stack)");
                        }
                        Integer n = expectedResources.pop();
                        if (n == resId) {
                            System.out.println("The game asked for resource " + resId + " and it was expected :-)");
                        } else {
                            throw new RuntimeException("expected resource: " + n + " but this resource is asked for: " + resId);
                        }
                    }
                    return "#" + resId;
                }
            };
        }
        return ret;
    }
}
