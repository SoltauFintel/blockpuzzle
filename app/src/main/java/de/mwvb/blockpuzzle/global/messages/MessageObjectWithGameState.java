package de.mwvb.blockpuzzle.global.messages;

import de.mwvb.blockpuzzle.gamestate.GamePlayState;

/**
 * This message also holds info if game play is lost or won.
 */
public class MessageObjectWithGameState extends MessageObject {
    private final GamePlayState state;

    public MessageObjectWithGameState(MessageFactory factory, int id, GamePlayState state) {
        super(factory, id);
        this.state = state;
    }

    public GamePlayState getState() {
        return state;
    }

    /**
     * @return true if victory (play applause sound), false if keep playing or lost game
     */
    public boolean isWonGame() {
        return state == GamePlayState.WON_GAME;
    }

    /**
     * @return true if lost game (play laughing), false if keep playing or won game
     */
    public boolean isLostGame() {
        return state == GamePlayState.LOST_GAME;
    }
}
