package de.mwvb.blockpuzzle.gamedefinition

import de.mwvb.blockpuzzle.Features
import de.mwvb.blockpuzzle.block.BlockTypes
import de.mwvb.blockpuzzle.game.Game
import de.mwvb.blockpuzzle.persistence.IPersistence
import de.mwvb.blockpuzzle.planet.IPlanet
import de.mwvb.blockpuzzle.playingfield.PlayingField

/**
 * Spielfeld leeren
 * Hier kommt es auf möglichst wenige Moves an.
 *
 * Spielende:
 * - Spielfeld voll -> verloren
 * - keine vordefinierten Spielsteine mehr -> verloren
 * - Spielfeld leer -> Wertung der Moves (möglichst wenige) -> Wertung der Punkte
 */
class CleanerGameDefinition @JvmOverloads constructor(
    gamePieceSetNumber: Int,
    /** L: Aus dem Level ergibt sich die initiale Spielfeldbelegung  */
    val level: Int,
    /** XLM: 0: no limit  */
    val maximumLiberationMoves: Int = 0
) : GameDefinition(gamePieceSetNumber) {

    // GAME DEFINITION ----

    override fun showMoves(): Boolean {
        return true
    }

    override fun toString(): String {
        return "CleanerGame(GPSN=$gamePieceSetNumber,L=$level,XLM=$maximumLiberationMoves)"
    }

    override fun onEmptyPlayingField(): Boolean {
        return true // Sieg
    }

    override fun gameCanBeWon(): Boolean {
        return true
    }

    override fun offerNewGamePiecesAfterGameOver(): Boolean {
        return false
    }


    // INIT PHASE ----

    override fun fillStartPlayingField(p0: PlayingField?) {
        val p = p0!!
        val blockTypes = BlockTypes(null)
        var def = getStartPlayingField(level)
        while (def.startsWith("\n")) {
            def = def.substring(1)
        }
        val lines = def.split("\n")
        for (y in 0..Game.blocks - 1) {
            for (x in 0..Game.blocks - 1) {
                val c = lines[y][x]
                if (c != '_') {
                    val blockType = blockTypes.getBlockTypeNumber(c)
                    p.set(x, y, blockType)
                }
            }
        }
        p.draw()
    }


    // DISPLAY ----

    override fun getInfo(): String {
        var ret = ""
        if (Features.developerMode) {
            ret = "Z$gamePieceSetNumber "
        }
        ret += "Cleaner Game L$level"
        if (maximumLiberationMoves > 0) {
            ret += " XLM$maximumLiberationMoves"
        }
        return ret
    }

    override fun getClusterViewInfo(): String {
        var ret = "Z" + getGamePieceSetNumber() + " Cleaner L$level"
        if (maximumLiberationMoves > 0) {
            ret += " XLM" + maximumLiberationMoves
        }
        return ret
    }


    // QUESTIONS AND EVENTS ----

    override fun isLiberated(player1Score: Int, player1Moves: Int, player2Score: Int, player2Moves: Int): Boolean {
        return player1Moves > 0 &&
                (maximumLiberationMoves <= 0 || player1Moves <= maximumLiberationMoves) && // entweder kein MAX oder ich bin nicht über MAX
                (player2Moves <= 0 || // entweder kein Gegner
                        player1Moves < player2Moves || // oder ich bin besser (d.h. weniger Moves)
                        (player1Moves == player2Moves && player1Score > player2Score)) // oder ich habe eine höhere Score bei gleicher Movesanzahl.
    }

    override fun scoreChanged(score: Int, moves: Int, planet: IPlanet?, won: Boolean, persistence: IPersistence?, resouces: ResourceAccess?): String? {
        return null
    }

    // PLAYING FIELDS ----

    private fun getStartPlayingField(level: Int): String {
        when (level) {
            1 -> return getLevel1()
            2 -> return getLevel2()
            3 -> return getLevel3()
            4 -> return getLevel4()
            5 -> return getLevel5()
            6 -> return getLevel6()
            7 -> return getLevel7()
            8 -> return getLevel8()
            9 -> return getLevel9()
            else -> throw RuntimeException("Unknown level $level")
        }
    }

    private fun getLevel1(): String {
        return """
_5______5_
__________
__________
__________
__________
__________
__________
__________
__________
Soooooooo_ 
        """
    }

    private fun getLevel2(): String {
        return """
_5______5_
__________
__________
__________
__________
__________
__________
__________
oooooooo_o
Soooooooo_
        """
    }

    private fun getLevel3(): String {
        return """
_5______5_
__________
__________
__________
__________
__________
__________
ooooooo_oo
oooooooo_o
3oooooooo_
        """
    }

    private fun getLevel4(): String {
        return """
_5______5_
__________
__________
__________
__________
__________
oooooo_ooo
ooooooo_oo
3ooooooo_o
L3ooooooo_
        """
    }

    private fun getLevel5(): String {
        return """
_L______L_
__________
__________
__________
__________
oooLo_oooo
oooooo_ooo
ooooooo_oo
Looooooo_o
LLooooooo_
        """
    }

    private fun getLevel6(): String {
        return """
_L______L_
__________
__________
__________
ooLo_ooooo
oooLo_oooo
oooooo_ooo
o_ooooo_oo
oooooooo_o
oLLLLLLoo_
        """
    }

    private fun getLevel7(): String {
        return """
_L______L_
__________
oLo_oo66oo
ooLo_ooooo
oooLo_oooo
oooooo_ooo
5_ooooo_oo
oooooooo_o
oLLLLLLoo_
__________
        """
    }

    private fun getLevel8(): String {
        return """
_L_____LL_
___L______
Lo_ooLoLoo
oLo_oo66_o
ooLo_ooLoo
oooLo_oooo
_5oo3o_ooo
5_ooo3o_oo
oooooo3o_o
oLLLLLL3o_
        """
    }

    private fun getLevel9(): String {
        return """
__________
__________
__3____3__
__________
__________
___LLLL___
__L____L__
_L______L_
__________
__________
        """
    }
}