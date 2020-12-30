package de.mwvb.blockpuzzle.gamedefinition

import de.mwvb.blockpuzzle.R
import de.mwvb.blockpuzzle.persistence.GamePersistence
import de.mwvb.blockpuzzle.persistence.IPersistence
import de.mwvb.blockpuzzle.planet.DailyPlanet
import de.mwvb.blockpuzzle.planet.IPlanet
import de.mwvb.blockpuzzle.playingfield.PlayingField
import java.util.*

class DailyClassicGameDefinition(private val day: Int) : ClassicGameDefinition(1) {

    override fun getMinimumLiberationScore(): Int {
        return day * 1000
    }

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

    override fun scoreChanged(score: Int, moves: Int, planet: IPlanet?, won: Boolean, persistence: GamePersistence?, resources: ResourceAccess?): String? {
        if (won || score < minimumLiberationScore) return null

        // Game gewonnen!
        // Gegnerdaten spielen hier keine Rolle.

        // Siegstatus speichern
        val per = persistence!!.persistenceOK
        per.saveDailyDate(planet!!, day - 1, DailyPlanet.getToday(per) + DailyPlanet.WON_GAME)

        // Haken setzen
        planet.isOwner = true
        per.savePlanet(planet)

        return resources!!.getString(R.string.planetLiberated)
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
__________
__________
______66__
_____666__
____6_66__
______66__
______66__
______66__
_____6666_
__________
        """;
    }

    private fun getDay2(): String {
        return """
__________
__oooo____
_o___oo___
_____oo___
____oo____
___oo_____
__oo______
_oo_______
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
_____o____
____oo____
___o_o____
__o__o____
_ooooSooo_
_____o____
_____o____
_____o____
__________
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
__oooo____
_o____o___
_o________
_Loooo____
_o____o___
_o____o___
__oooo__L_
__________
__________
        """;
    }

    private fun getDay7(): String {
        return """
__LLLSLL__
__________
__oooooo__
__LLLLoL__
__LLLoLL__
__LLoLLL__
__LoLLLL__
__o66666__
__________
__333333__
        """;
    }
}