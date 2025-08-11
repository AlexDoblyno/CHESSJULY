package Service;

import dataaccess.UserDataAccess;
import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.ServerException;
import Service.*;
import dataaccess.AuthDataAccess;
import dataaccess.UserDataAccess;
import models.AuthTokenData;
import models.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;
import java.util.Base64;

public class RegisterService {

    private final UserDataAccess userDataAccess;
    private final AuthDataAccess authDataAccess;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder();

    public RegisterService(UserDataAccess userDA, AuthDataAccess authDA) {
        this.userDataAccess = userDA;
        this.authDataAccess = authDA;
    }

    public String createUser(String username, String password, String email) throws DataAccessException {
        if (username == null || password == null || email == null || username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            throw new IllegalArgumentException("Username, password, and email are required");
        }
        try {
            if (userDataAccess.getUserData(username) != null) {
                throw new DataAccessException("Username is already taken");
            }
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        } catch (server.ServerException e) {
            throw new DataAccessException(e.getMessage());
        }
        AuthTokenData authTokenData = null;
        try {
            String hashedPW = BCrypt.hashpw(password, BCrypt.gensalt());
            UserData hashedData = new UserData(username, hashedPW, email);

            userDataAccess.addUserData(hashedData);
            authTokenData = new AuthTokenData(generateAuthToken(), hashedData.username());
            authDataAccess.addAuthData(authTokenData);
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }
        return authTokenData.toString();
    }

    private String generateAuthToken() throws ServerException {
        byte[] randomBytes = new byte[24];
        SECURE_RANDOM.nextBytes(randomBytes);
        String authToken = ENCODER.encodeToString(randomBytes);

        try {
            // Verify uniqueness
            if (authDataAccess.getAuthData(authToken) != null) {
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
