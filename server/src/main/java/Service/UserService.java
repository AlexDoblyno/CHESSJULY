package Service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.ServerException;
import dataaccess.UserDataAccess;
import models.AuthTokenData;
import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;
import java.util.Base64;

public class UserService {
    private final UserDataAccess userDAO;
    private final AuthDataAccess authDAO;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder();

    public UserService(UserDataAccess userDAO, AuthDataAccess authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public String loginUser(String username, String password) throws DataAccessException {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Username and password are required");
        }
        try {
            if (userDAO.getUserData(username) == null) {
                throw new DataAccessException("No user found");
            } else if (!BCrypt.checkpw(password, userDAO.getUserData(username).password())) {
                throw new DataAccessException("Wrong password");
            }
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        } catch (server.ServerException e) {
            throw new DataAccessException(e.getMessage());
        }

        AuthTokenData authTokenData = null;
        try {
            authTokenData = new AuthTokenData(generateAuthToken(), username);
            authDAO.addAuthData(authTokenData);
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }

        return authTokenData.toString();
    }

    public void logoutUser(String token) throws DataAccessException {
        try {
            if (authDAO.getAuthData(token) == null) {
                throw new DataAccessException("Invalid token");
            }
            authDAO.removeAuthData(authDAO.getAuthData(token));
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private String generateAuthToken() throws ServerException {
        byte[] randomBytes = new byte[24];
        SECURE_RANDOM.nextBytes(randomBytes);
        String authToken = ENCODER.encodeToString(randomBytes);

        try {
            // Verify uniqueness
            if (authDAO.getAuthData(authToken) != null) {
                return generateAuthToken();
            }
        } catch (dataaccess.ServerException e) {
            if (e.getMessage().contains("not found")) {
                return authToken;
            } else {
                new server.ServerException(e.getMessage(), 500);
            }
        }
        return authToken;
    }
}
