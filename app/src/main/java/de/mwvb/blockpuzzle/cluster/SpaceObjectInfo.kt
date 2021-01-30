package de.mwvb.blockpuzzle.cluster

data class SpaceObjectInfo(val infoText1: String, val infoText2: String, val infoText3: String) {

    fun getInfoText(i: Int): String {
        return when (i) {
            1 -> infoText1
            2 -> infoText2
            3 -> infoText3
            else -> ""
        }
    }
}
