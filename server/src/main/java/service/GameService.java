package service;

import chess.ChessGame;
import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import dataaccess.ServerException;
import dataaccess.UserDataAccess;
import models.GameData;

import java.security.SecureRandom;
import java.util.Collection;

/**
 * Provides functionality for game-related operations such as listing games,
 * creating new games, or joining existing games.
 * <p>
 * Some ideas for random ID generation taken from:
 * <a href="https://stackoverflow.com/questions/13992972/how-to-create-an-authentication-token-using-java">
 *     how-to-create-an-authentication-token-using-java (Stack Overflow)</a>
 */
public class GameService {

    private final UserDataAccess userDataAccess;
    private final GameDataAccess gameDataAccess;
    private final AuthDataAccess authDataAccess;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Constructs the GameService with the required DAOs.
     *
     * @param userDAO a UserDataAccess implementation to handle user data
     * @param gameDAO a GameDataAccess implementation for game data retrieval
     * @param authDAO an AuthDataAccess implementation for auth token management
     */
    public GameService(UserDataAccess userDAO,
                       GameDataAccess gameDAO,
                       AuthDataAccess authDAO) {
        this.userDataAccess = userDAO;
        this.authDataAccess = authDAO;
        this.gameDataAccess = gameDAO;
    }

    /**
     * Verifies if the auth token is valid in the datasource.
     *
     * @param authToken the user's authentication token
     * @throws DataAccessException if token is invalid or a server error occurs
     */
    public void verifyAuthToken(String authToken) throws DataAccessException {
        try {
            if (authDataAccess.getAuthData(authToken) == null) {
                throw new DataAccessException("Invalid Auth Token");
            }
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Lists all games in the database if the provided auth token is valid.
     *
     * @param authToken the user's authentication token
     * @return a collection of existing games
     * @throws DataAccessException if token is invalid or a server error occurs
     */
    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        try {
            if (authDataAccess.getAuthData(authToken) == null) {
                throw new DataAccessException("Invalid Auth Token");
            }
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }
        try {
            return gameDataAccess.getGameList();
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Creates a new chess game provided a valid name that doesn't yet exist in the system.
     * <p>
     * Random ID generation logic partially inspired by:
     * <a href="https://stackoverflow.com/questions/13992972/how-to-create-an-authentication-token-using-java">
     *     Stack Overflow discussion</a>
     *
     * @param gameName the name of the new game
     * @return an integer ID representing the newly created game
     * @throws DataAccessException if the gameName is invalid or already taken, or if a server error occurs
     */
    public Integer createGame(String gameName) throws DataAccessException {
        if (gameName == null || gameName.trim().isEmpty()) {
            throw new DataAccessException("Invalid parameter");
        }

        try {
            if (gameDataAccess.getGameByName(gameName) == null) {
                ChessGame newGame = new ChessGame();
                int gameID = generateGameID();
                while (gameDataAccess.getGameByID(gameID) != null) {
                    gameID = generateGameID();
                }
                GameData newGameData = new GameData(gameID, null, null, gameName, newGame);
                gameDataAccess.createGame(newGameData);
                return gameID;
            }
            throw new DataAccessException("already taken");
        } catch (ServerException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
    }

    /**
     * Allows an authenticated user to join an existing game by specifying a team color.
     *
     * @param authToken   a valid auth token
     * @param gameID      the ID of the game to join
     * @param playerColor which side (WHITE or BLACK) the user intends to play on
     * @throws DataAccessException if the game doesn't exist, color is invalid, or color is already taken
     */
    public void joinGame(String authToken, Integer gameID, ChessGame.TeamColor playerColor) throws DataAccessException {
        String username;
        try {
            username = authDataAccess.getAuthData(authToken).username();
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }

        if (gameID == null) {
            throw new DataAccessException("Invalid gameID");
        }
        try {
            GameData existingGame = gameDataAccess.getGameByID(gameID);
            if (existingGame == null) {
                throw new DataAccessException("Error: bad request");
            }
            // Validate color
            if ((playerColor != ChessGame.TeamColor.WHITE) && (playerColor != ChessGame.TeamColor.BLACK)) {
                throw new IllegalArgumentException("Error: Invalid team color");
            }
            // Check if color is already taken
            if ((playerColor == ChessGame.TeamColor.WHITE && existingGame.whiteUsername() != null)
                    || (playerColor == ChessGame.TeamColor.BLACK && existingGame.blackUsername() != null)) {
                throw new DataAccessException("Error: Player color already taken.");
            }
            // Assign user to that color
            gameDataAccess.updateGame(playerColor, gameID, username);

        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Generates a random positive integer to serve as a game ID.
     * Uses the built-in {@link SecureRandom#nextBytes(byte[])} method to produce randomness.
     *
     * @return a positive integer ID
     */
    private int generateGameID() {
        byte[] randomBytes = new byte[4];
        SECURE_RANDOM.nextBytes(randomBytes);
        return Math.abs(java.nio.ByteBuffer.wrap(randomBytes).getInt());
    }
}