package de.mwvb.blockpuzzle.game

import de.mwvb.blockpuzzle.block.BlockTypes
import de.mwvb.blockpuzzle.game.place.IPlaceAction
import de.mwvb.blockpuzzle.gamedefinition.OldGameDefinition
import de.mwvb.blockpuzzle.gamepiece.Holders
import de.mwvb.blockpuzzle.gamepiece.INextGamePiece
import de.mwvb.blockpuzzle.gamestate.GameState
import de.mwvb.blockpuzzle.playingfield.PlayingField
import de.mwvb.blockpuzzle.playingfield.gravitation.GravitationData

/**
 * Game engine model
 */
data class GameEngineModel(
    // Immutable properties only!
    val blocks: Int,
    val blockTypes: BlockTypes,
    val view: IGameView,
    val gs: GameState,
    val definition: OldGameDefinition,
    val playingField: PlayingField,
    val holders: Holders,
    val placeActions: List<IPlaceAction>,
    val gravitation: GravitationData,
    val nextGamePiece: INextGamePiece
) {

    fun save() {
        val ss = gs.get()
        playingField.save(ss)
        gravitation.save(ss)
        holders.save(ss)
        gs.save()
    }
}
