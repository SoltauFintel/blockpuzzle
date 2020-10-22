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

    fun find(name: String): GamePiece? {
        val ret = get()
        for (p in ret!!) {
            if (p.name == name) {
                return p
            }
        }
        throw RuntimeException("Game piece '$name' doesn't exist!")
    }

    val gamePieces =
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
R=1
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
min=1000
.....
.5...
.555.
.....
.....

#L
min=1000
.....
...6.
.666.
.....
.....

#2x2_Bonus:2x2
min=2000

#2x3
min=3000
.....
.111.
.111.
.....
.....

#2x3_Bonus:2x3
min=4000

#S
min=5000
.....
.....
..55.
.55..
.....

#Z
min=5000
.....
.....
.66..
..66.
.....

#3x3_Bonus1:3x3
min=6000

#3x3_Bonus2:3x3
min=7000

#T
R=1
RR=1
L=1
min=8000
.....
..5..
.555.
.....
.....

#3x3_Bonus3:3x3
min=9000

#4_Bonus:4
min=10000

#Ecke3_Bonus1:Ecke3
min=11000

#2Dots
min=12000
.....
.3...
..4..
.....
.....

#X
min=13000
.....
.5.5.
..4..
.5.5.
.....

#1_Bonus1:1
n=2
min=15000

#Ecke3_Bonus2:Ecke3
min=20000

#Slash
min=30000
.....
...5.
..5..
.5...
.....

#3x3_Bonus4:3x3
min=40000

#1_Bonus2:1
n=2
min=50000

#BigSlash
min=50000
....1
...2.
..3..
.4...
5....

#X_Bonus:X
min=60000


"""

}