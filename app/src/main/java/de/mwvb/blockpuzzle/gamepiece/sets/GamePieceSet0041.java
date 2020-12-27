package de.mwvb.blockpuzzle.gamepiece.sets;

import de.mwvb.blockpuzzle.gamepiece.IGamePieceSet;

/** Developer Mode */
public class GamePieceSet0041 implements IGamePieceSet {

	@Override
	public String[] getGamePieceSet() {
		String[] r = new String[9];
		r[0] = "#1#2x2#3x3";
		r[1] = "#2#3#4";
		r[2] = "#2x3#Ecke2#Ecke3";
		r[3] = "#5#J#L";
		r[4] = "#S#Z#T";
		r[5] = "#X#2Dots#Slash";
		r[6] = "#DT#BigSlash#X";
		r[7] = "#1#1#1";
		r[8] = "#1#1#1";
		return r;
	}
}
