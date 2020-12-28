package de.mwvb.blockpuzzle.game;

import de.mwvb.blockpuzzle.gamepiece.GamePiece;
import de.mwvb.blockpuzzle.gravitation.GravitationData;
import de.mwvb.blockpuzzle.persistence.IPersistence;
import de.mwvb.blockpuzzle.planet.IPlanet;
import de.mwvb.blockpuzzle.playingfield.PlayingField;

public class PersistenceNoOp implements IPersistence {

    @Override
    public void save(int index, GamePiece p) {
    }

    @Override
    public GamePiece load(int index) {
        return null;
    }

    @Override
    public void save(PlayingField f) {
    }

    @Override
    public void load(GravitationData data) {
    }

    @Override
    public void save(GravitationData data) {
    }

    @Override
    public void load(PlayingField f) {
    }

    @Override
    public void setGameID(IPlanet planet, int gameDefinitionIndex) {
    }

    @Override
    public void setGameID(IPlanet planet) {
        setGameID(planet, planet.getGameDefinitions().indexOf(planet.getSelectedGame()));
    }

    @Override
    public void setGameID_oldGame() {
    }

    @Override
    public int loadScore() {
        return -9999;
    }

    @Override
    public void saveScore(int punkte) {
    }

    @Override
    public int loadDelta() {
        return 0;
    }

    @Override
    public void saveDelta(int delta) {
    }

    @Override
    public int loadMoves() {
        return 0;
    }

    @Override
    public void saveMoves(int moves) {
    }

    @Override
    public boolean loadEmptyScreenBonusActive() {
        return true;
    }

    @Override
    public void saveEmptyScreenBonusActive(boolean v) {
    }

    @Override
    public int loadHighScore() {
        return 0;
    }

    @Override
    public void saveHighScore(int punkte) {
    }

    @Override
    public int loadHighScoreMoves() {
        return 0;
    }

    @Override
    public void saveHighScoreMoves(int moves) {
    }

    @Override
    public void saveOwner(int score, int moves, String name) {
    }

    @Override
    public void clearOwner() {
    }

    @Override
    public String loadOwnerName() {
        return null;
    }

    @Override
    public int loadOwnerScore() {
        return 0;
    }

    @Override
    public int loadOwnerMoves() {
        return 0;
    }

    @Override
    public void loadPlanet(IPlanet planet) {
    }

    @Override
    public String loadPlayerName() {
        return "TEST";
    }

    @Override
    public void savePlayerName(String playername) {
    }

    @Override
    public void saveCurrentPlanet(int clusterNumber, int planetNumber) {
    }

    @Override
    public int loadCurrentPlanet() {
        return 1;
    }

    @Override
    public int loadCurrentCluster() {
        return 1;
    }

    @Override
    public void saveOldGame(int v) {
    }

    @Override
    public int loadOldGame() {
        return 0;
    }

    @Override
    public boolean isStoneWars() {
        return loadOldGame() == 2;
    }

    @Override
    public void saveNextRound(int nextRound) {
    }

    @Override
    public int loadNextRound() {
        return 0;
    }

    @Override
    public boolean loadGameOver() {
        return false;
    }

    @Override
    public void saveGameOver(boolean gameOver) {
    }

    @Override
    public boolean loadPlayernameEntered() {
        return false;
    }

    @Override
    public void savePlayernameEntered(boolean v) {
    }

    @Override
    public boolean isGameSoundOn() {
        return false; // no game sounds for unit tests
    }

    @Override
    public void saveGameSound(boolean on) {
    }

    @Override
    public void resetAll() {
    }

    @Override
    public void savePlanet(IPlanet planet) {
    }
}
