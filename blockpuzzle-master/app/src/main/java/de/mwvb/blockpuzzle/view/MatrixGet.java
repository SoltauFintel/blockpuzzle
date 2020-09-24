package de.mwvb.blockpuzzle.view;

import android.graphics.Paint;

/**
 * Kästchen ausfüllen Strategie
 */
public interface MatrixGet { // TODO nicht der beste Name

    /**
     * @return Füllung für ein nicht leeres Kästchen
     */
    Paint get(int x, int y);
}
