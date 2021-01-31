package de.mwvb.blockpuzzle.game.place;

/**
 * Player dropped a game piece. A place action is an action that is executed after this to add a special game play feature.
 *
 * Implementations must be stateless.
 */
public interface IPlaceAction {

    void perform(PlaceActionModel info);
}
