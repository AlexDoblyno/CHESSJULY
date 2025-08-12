package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import dataaccess.ServerException;
import dataaccess.UserDataAccess;

/**
 * ClearServiceRefactored: Provides functionality to clear user, auth, and game data from the application.
 * The logic is identical to the original ClearService, only the naming and structure have been slightly adjusted.
 */
public class ClearService {

    private final GameDataAccess gameDataAccess;
    private final AuthDataAccess authDataAccess;
    private final UserDataAccess userDataAccess;

    /**
     * Constructs ClearServiceRefactored with references to game, auth, and user data access objects.
     *
     * @param gameDAO the game data access object
     * @param authDAO the auth data access object
     * @param userDAO the user data access object
     */
    public ClearService(GameDataAccess gameDAO, AuthDataAccess authDAO, UserDataAccess userDAO) {
        this.gameDataAccess = gameDAO;
        this.authDataAccess = authDAO;
        this.userDataAccess = userDAO;
    }

    /**
     * Clears all stored datas: games, auth tokens, and users in that order.
     *
     * @throws DataAccessException if a ServerException occurs in any data access layer
     */
    public void clear() throws DataAccessException {
        try {
            gameDataAccess.clearGames();
            authDataAccess.clearAuthTokens();
            userDataAccess.clearUsers();
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}