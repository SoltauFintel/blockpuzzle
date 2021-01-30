package de.mwvb.blockpuzzle.gamepiece

// Kotlin supports multi line strings
object GamePiecesDefinition {
    private var allGamePieces: List<GamePiece>? = null

    @Synchronized
    fun get(): List<GamePiece> {
        if (allGamePieces == null) {
            allGamePieces = GamePieceParser().parse(
                gamePieces
            )
        }
        return allGamePieces!!
    }

    private const val gamePieces =
"""
// Jede Spielsteinart standardmäßig 4x dabei.
// Je nach Schwierigkeitsgrad wird das zum Teil abhängig von der Punktzahl variiert.

#1
n=3
.....
.....
..3..
.....
.....

#2
n=2
R=1
.....
.....
.11..
.....
.....
   
#3
n=2
R=2
.....
.....
.111.
.....
.....

#4
n=2
R=2
.....
.....
1111.
.....
.....

#5
n=2
R=1
.....
.....
22222
.....
.....

#Ecke2
R=1
RR=1
L=1
.....
.4...
.44..
.....
.....

#Ecke3
min=25
RR=1
L=1
.....
.4...
.4...
.444.
.....

#2x2
.....
.33..
.33..
.....
.....

#3x3
.....
.333.
.333.
.333.
.....


// Game pieces for more difficulty ----

#J
min=50
.....
.7...
.777.
.....
.....

#L
min=50
.....
...7.
.777.
.....
.....

#2x2_Bonus:2x2
min=100

#2x3
min=150
.....
.111.
.111.
.....
.....

#2x3_Bonus:2x3
min=200

#S
min=250
.....
.....
..55.
.55..
.....

#Z
min=250
.....
.....
.55..
..55.
.....

#3x3_Bonus1:3x3
min=300

#3x3_Bonus2:3x3
min=350

#T
R=1
RR=1
L=1
min=400
.....
..6..
.666.
.....
.....

#3x3_Bonus3:3x3
min=450

#4_Bonus:4
min=500

#Ecke3_Bonus1A:Ecke3
min=550

#Ecke3_Bonus1B:Ecke3
min=550

#2Dots
min=600
.....
.3...
..4..
.....
.....

#X
min=650
.....
.5.5.
..4..
.5.5.
.....

#1_Bonus1:1
n=2
min=750

#Ecke3_Bonus2:Ecke3
min=1000

#Slash
min=1500
.....
...5.
..5..
.5...
.....

#3x3_Bonus4:3x3
min=2000

#1_Bonus2:1
n=2
min=2500

#BigSlash
min=2500
....1
...2.
..3..
.4...
5....

#X_Bonus:X
min=3000

#1_Bonus3:1
min=3500

#X_Bonus2:X
min=4000

#DT
min=4500
.....
..6..
.636.
..6..
.....

"""

}