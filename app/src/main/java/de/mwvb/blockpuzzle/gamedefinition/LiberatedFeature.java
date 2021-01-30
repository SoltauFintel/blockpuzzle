package de.mwvb.blockpuzzle.gamedefinition;

/**
 * Hängt optional an der 1. GameDefinition eines Planeten und wird ausgeführt, wenn alle Territorien befreit sind.
 */
public interface LiberatedFeature {

    void start();
}
