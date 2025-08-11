package dataaccess;

import models.AuthTokenData;
import models.GameData;
import chess.ChessGame;

import java.util.Collection;
import java.util.HashSet;

public class MemoryGameDataAccess implements GameDataAccess {
    Collection<GameData> gameDatabase;

    public MemoryGameDataAccess() {
        gameDatabase = new HashSet<GameData>();
    }

    @Override
    public Collection<GameData> getGameList() {
//        Collection<String> gameList = new HashSet<String>();
//        for (GameData game : gameDatabase) {
//            gameList.add(game.gameName());
//        }
        return gameDatabase;
    }

    @Override
    public GameData getGameByName(String gameName) {
        for (GameData game : gameDatabase) {
            if (game.gameName().equals(gameName)) {
                return game;
            }
        }
        return null;
    }

    @Override
    public GameData getGameByID(int gameID) {
        for (GameData game : gameDatabase) {
            if (game.gameID() == (gameID)) {
                return game;
            }
        }
        return null;
    }

    @Override
    public void createGame(GameData gameData) {
        gameDatabase.add(gameData);
    }

    @Override
    public void joinGame(AuthTokenData authData, ChessGame.TeamColor team, int gameID) {
        GameData savedGame = getGameByID(gameID);
        GameData updateGame = null;

        if (team == ChessGame.TeamColor.WHITE) {
            updateGame = savedGame.setWhiteUsername(authData.username());
        } else if (team == ChessGame.TeamColor.BLACK) {
            updateGame = savedGame.setBlackUsername(authData.username());
        }

        gameDatabase.remove(savedGame);
        gameDatabase.add(updateGame);
    }

    @Override
    public void clearGames() {
        gameDatabase.clear();
    }

    @Override
    public void updateChessGame(ChessGame game, Integer gameID) {
            GameData gameData = this.getGameByID(gameID);
            gameDatabase.remove(gameData);
            gameDatabase.add(new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));
    }

    @Override
    public void updateGame(ChessGame.TeamColor Color, Integer gameID, String username) {
        if (Color == ChessGame.TeamColor.BLACK) {
            GameData gameData = this.getGameByID(gameID);
            String whiteUsername = gameData.whiteUsername();
            String gameName = gameData.gameName();
            ChessGame game = gameData.game();
            gameDatabase.remove(gameData);
            gameDatabase.add(new GameData(gameID, whiteUsername, username, gameName, game));
        } else {
            GameData gameData = this.getGameByID(gameID);
            String blackUsername = gameData.blackUsername();
            String gameName = gameData.gameName();
            ChessGame game = gameData.game();
            gameDatabase.remove(gameData);
            gameDatabase.add(new GameData(gameID, blackUsername, username, gameName, game));
        }
    }
}