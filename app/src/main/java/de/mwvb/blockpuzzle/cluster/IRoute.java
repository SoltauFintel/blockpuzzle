package de.mwvb.blockpuzzle.cluster;

/**
 * Route between two space objects
 */
public interface IRoute {

    /**
     * @return start space object number
     */
    int getFrom();

    /**
     * @return target space object number
     */
    int getTo();

    /**
     * @return false to abort journey, true to reach target
     */
    boolean travel();
}
