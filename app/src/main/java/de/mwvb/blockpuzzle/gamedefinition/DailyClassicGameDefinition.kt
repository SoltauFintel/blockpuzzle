package de.mwvb.blockpuzzle.gamedefinition

import de.mwvb.blockpuzzle.R
import de.mwvb.blockpuzzle.persistence.GamePersistence
import de.mwvb.blockpuzzle.planet.DailyPlanet
import de.mwvb.blockpuzzle.planet.IPlanet
import de.mwvb.blockpuzzle.playingfield.PlayingField
import java.util.*

/**
 * The daily planet has seven days/games.
 */
class DailyClassicGameDefinition(private val day: Int) : ClassicGameDefinition(1) {

    /** It becomes harder every day within the 7 days. */
    override fun getMinimumLiberationScore(): Int {
        return day * 1000
    }

    /** The game piece set changes every week. */
    override fun getGamePieceSetNumber(): Int {
        val cal = Calendar.getInstance()
        cal.time = Date()
        var week = cal[Calendar.WEEK_OF_YEAR]
        while (week > 40) {
            week -= 40
        }
        if (week < 1 || week > 40) { // allowed value 1 to 40
            week = 1
        }
        return week
    }

    override fun toString(): String {
        return "DailyClassicGame($day)"
    }

    /** Check for classic game victory */
    override fun scoreChanged(score: Int, moves: Int, planet: IPlanet?, won: Boolean, persistence: GamePersistence?, resources: ResourceAccess?): String? {
        if (won || score < minimumLiberationScore) return null
        val per = persistence!!.persistenceOK

        // Game gewonnen!
        // Gegnerdaten spielen hier keine Rolle.

        // Siegstatus speichern
        per.saveDailyDate(planet!!, day - 1, DailyPlanet.getToday(per) + DailyPlanet.WON_GAME)

        // Haken setzen
        planet.isOwner = true
        per.savePlanet(planet)

        // Save trophy
        var withTrophy = false;
        val today = DailyPlanet.getToday(per)
        if (!today.equals(per.loadLastTrophyDate())) { // prevents cheating: prevents that the player restart the game and gets the trophy more than once.
            per.saveLastTrophyDate(today)
            when (day) {
                5 -> per.addBronzeTrophy(planet)
                6 -> per.addSilverTrophy(planet, true)
                7 -> per.addGoldenTrophy(planet, true)
            }
            withTrophy = day >= 5
        }
        // Planet liberated!
        return resources!!.getString(if (withTrophy) /*with trophy*/ R.string.receivedTrophy else R.string.planetLiberated)
    }

    override fun fillStartPlayingField(pf: PlayingField?) {
        StartPlayingFieldFiller().fillStartPlayingField(pf!!, getStartPlayingField())
    }

    private fun getStartPlayingField(): String {
        return when (day) {
            1 -> getDay1()
            2 -> getDay2()
            3 -> getDay3()
            4 -> getDay4()
            5 -> getDay5()
            6 -> getDay6()
            7 -> getDay7()
            else -> throw RuntimeException("Unknown day $day")
        }
    }
    
    private fun getDay1(): String {
        return """
1_________
1_________
1____33333
____322222
___3226666
__32266777
__32667744
__32677455
__326745__
__326745__
        """;
    }

    private fun getDay2(): String {
        return """
__________
__________
__oooo____
_o___oo___
_____oo___
____oo____
___oo_____
__oo______
_oooooo___
__________
        """;
    }

    private fun getDay3(): String {
        return """
__________
__________
____oooo__
___o____o_
________o_
______oo__
________o_
___o____o_
____oooo__
__________
        """;
    }

    private fun getDay4(): String {
        return """
__________
__________
_____o____
____oo____
___o_o____
__o__o____
_ooooSooo_
_____o____
_____o____
_____o____
        """;
    }

    private fun getDay5(): String {
        return """
__________
__________
__oooooo__
__o_______
__o_______
___oooo___
_______o__
__o____o__
___oooo___
__________
        """;
    }

    private fun getDay6(): String {
        return """
__________
__________
__oooo____
_o____o___
_o________
_Loooo____
_o____o___
_o____o___
__oooo__L_
__________
        """;
    }

    private fun getDay7(): String {
        return """
L________1
__________
__________
__666666__
__6666L6__
__666L66__
__66L666__
__6L6666__
__L66663__
__________
        """;
    }
}