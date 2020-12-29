package de.mwvb.blockpuzzle.gamedefinition;

public class DailyClassicGameDefinition extends ClassicGameDefinition {

    public DailyClassicGameDefinition(int gamePieceSetNumber) {
        super(gamePieceSetNumber);
    }

    @Override
    public int getMinimumLiberationScore() {
        return super.getMinimumLiberationScore();
        // TODO
    }

    @Override
    public String toString() {
        return "DailyClassicGame";
    }
}
