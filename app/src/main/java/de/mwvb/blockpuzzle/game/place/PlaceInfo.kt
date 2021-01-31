package de.mwvb.blockpuzzle.game.place

import de.mwvb.blockpuzzle.game.GameEngineInterface
import de.mwvb.blockpuzzle.game.GameEngineModel
import de.mwvb.blockpuzzle.gamepiece.GamePiece
import de.mwvb.blockpuzzle.playingfield.FilledRows
import de.mwvb.blockpuzzle.playingfield.QPosition

/**
 * All data needed by an IPlaceAction
 */
data class PlaceInfo(
    val index: Int,
    val gamePiece: GamePiece,
    val pos: QPosition,
    // static data:
    private val model: GameEngineModel, // view is private
    val gameEngineInterface: GameEngineInterface
) {
    fun getFilledRows(): FilledRows = model.playingField.filledRows
    fun getGs() = model.gs
    fun getBlockTypes() = model.blockTypes
    fun getPlayingField() = model.playingField
    fun getGravitation() = model.gravitation
    fun getBlocks() = model.blocks
    fun getMessages() = model.view.getMessages()
    fun getDefinition() = model.definition
    fun playSound(number: Int) = model.view.playSound(number)
}
