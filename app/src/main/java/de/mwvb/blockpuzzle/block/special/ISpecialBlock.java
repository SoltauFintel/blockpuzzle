package de.mwvb.blockpuzzle.block.special;

import android.view.View;

import de.mwvb.blockpuzzle.block.IBlockDrawer;
import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.playingfield.PlayingField;
import de.mwvb.blockpuzzle.playingfield.QPosition;

public interface ISpecialBlock {

    /**
     * @return block type number for BlockTypes
     */
    int getBlockType();

    /**
     * @return block type char for BlockTypes
     */
    char getBlockTypeChar();

    IBlockDrawer getBlockDrawer(View view);

    /**
     * @return block type color for BlockTypes
     */
    int getColor();

    /**
     * Usually random decides whether true will be returned.
     * @param p game piece
     * @return true if process() shall be called
     */
    boolean isRelevant(GamePiece p);

    /**
     * Modify if game piece if all rules are met.
     * @param p game piece
     * @return true if no other SpecialBlock should be called, false continue
     */
    boolean process(GamePiece p);

    /**
     * Received before game piece is placed to playing field
     * @param p full game piece
     * @param gpPos game piece position in playing field
     * @param k position of special block
     */
    void placed(GamePiece p, QPosition gpPos, QPosition k);

    int CLEAR_MAX_MODE = 1;

    /**
     * @param playingField -
     * @param k playing field position of that special block type instance
     * @return 0: do nothing, 1: don't remove block at position k.
     * Eine Zahl größer CLEAR_MAX_MODE wird als Bonuspunkteaufschlag gewertet.
     */
    int cleared(PlayingField playingField, QPosition k);
}
