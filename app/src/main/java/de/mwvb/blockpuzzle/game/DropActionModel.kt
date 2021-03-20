package de.mwvb.blockpuzzle.game

import de.mwvb.blockpuzzle.gamepiece.GamePiece
import de.mwvb.blockpuzzle.playingfield.QPosition

/**
 * Player drops a game piece. This class holds the needed data.
 */
data class DropActionModel(
    /** game piece holder index (1, 2, 3, -1) */
    val index: Int,
    /** the game piece to move */
    val gamePiece: GamePiece,
    /** target position in playing field, null if targetIsParking is true */
    val xy: QPosition
)
