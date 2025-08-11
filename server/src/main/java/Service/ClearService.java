package Service;

import dataaccess.*;
import dataaccess.AuthDataAccess;
import dataaccess.GameDataAccess;
import dataaccess.UserDataAccess;

public class ClearService {
    private static GameDataAccess gameDAO;
    private static AuthDataAccess authDAO;
    private static UserDataAccess userDAO;

    public ClearService(GameDataAccess gameDAO, AuthDataAccess authDAO, UserDataAccess userDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public void clear() throws DataAccessException {
        try {
            gameDAO.clearGames();
            authDAO.clearAuthTokens();
            userDAO.clearUsers();
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
