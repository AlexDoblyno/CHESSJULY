package Service;

import chess.ChessGame;
import dataaccess.*;
import models.AuthTokenData;
import models.GameData;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.List;

public class GameService {
    private UserDataAccess userDAO;
    private GameDataAccess gameDAO;
    private AuthDataAccess authDAO;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public GameService(UserDataAccess userDAO, GameDataAccess gameDAO, AuthDataAccess authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void verifyAuthToken(String authToken) throws DataAccessException {
        try {
            if (authDAO.getAuthData(authToken) == null) {
                throw new DataAccessException("Invalid Auth Token");
            }
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        try {
            if (authDAO.getAuthData(authToken)==null) {
                throw new DataAccessException("Invalid Auth Token");
            }
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }
        try {
            return gameDAO.getGameList();
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public Integer createGame(String gameName) throws DataAccessException {
        if (gameName == null || gameName.trim().isEmpty()) {
            throw new DataAccessException("Invalid parameter");
        }

        try {
                if (gameDAO.getGameByName(gameName) == null) {
                    ChessGame newGame = new ChessGame();
                    int gameID = generateGameID();
                    while (gameDAO.getGameByID(gameID) != null) {
                        gameID = generateGameID();
                    }
                    GameData newGameData = new GameData(gameID, null, null, gameName, newGame);
                    gameDAO.createGame(newGameData);
                    return gameID;
                }
                throw new DataAccessException("already taken");
        } catch (dataaccess.ServerException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
    }

    public void joinGame(String authToken, Integer gameID, ChessGame.TeamColor playerColor) throws DataAccessException {
        String username = null;
        try {
            username = authDAO.getAuthData(authToken).username();
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }
        if (gameID == null) {
            throw new DataAccessException("Invalid gameID");
        }
        try {
            if (gameDAO.getGameByID(gameID) == null) {
                throw new DataAccessException("Error: bad request");
            }
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }

        GameData gameData = null;
        try {
            gameData = gameDAO.getGameByID(gameID);
        } catch (ServerException e) {
            throw new RuntimeException(e);
        }


        if ((playerColor != ChessGame.TeamColor.WHITE) && (playerColor != ChessGame.TeamColor.BLACK)) {
            throw new IllegalArgumentException("Error: Invalid team color");
        }

        if ((playerColor == ChessGame.TeamColor.WHITE && gameData.whiteUsername() != null)
                || (playerColor == ChessGame.TeamColor.BLACK && gameData.blackUsername() != null)) {
            throw new DataAccessException("Error: Player color already taken.");
        }

        try {
            gameDAO.updateGame(playerColor, gameID, username);
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private int generateGameID() {
        byte[] randomBytes = new byte[4];
        SECURE_RANDOM.nextBytes(randomBytes);
        // Turn bytes into integer
        int gameID = Math.abs(java.nio.ByteBuffer.wrap(randomBytes).getInt());

        return gameID;
    }
}
