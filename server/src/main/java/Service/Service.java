package service;

import models.AuthTokenData;
import models.GameData;
import models.UserData;
import chess.ChessGame;
import dataaccess.*;
import server.ServerException;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Collection;

public class Service {
    UserDataAccess userDataAccess;//之后实现 用户信息
    AuthDataAccess authDataAccess;//之后是实现，授权
    GameDataAccess gameDataAccess;//游戏信息
    AuthTokenData authTokenData;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder();

    public Service() {
        userDataAccess = new MemoryUserDataAccess();
        authDataAccess = new MemoryAuthDataAccess();
        gameDataAccess = new MemoryGameDataAccess();
    }


    /**
     * ChessService to register a user in the database
     * @param userData is the UserData object containing the user's data
     * @return the AuthTokenData object created upon registration and logging in to the system
     * @throws ServerException 403: name already taken
     */
    public AuthTokenData register(UserData userData) throws ServerException {
        if (userDataAccess.getUserData(userData.username()) == null) {

            userDataAccess.addUserData(userData);

            authTokenData = new AuthTokenData(generateAuthToken(), userData.username());

            authDataAccess.addAuthData(authTokenData);

            return authTokenData;
        }
        else {
            throw new ServerException("already taken", 403);
        }
    }

    /**
     * Log in a user into the database
     * @param username is the user's username
     * @param password is the user's password
     * @return the AuthTokenData object created upon login
     * @throws ServerException 401
     */
    public AuthTokenData login(String username, String password) throws ServerException {
        UserData userData = userDataAccess.getUserData(username);
        if (userData == null) {
            throw new ServerException("unauthorized", 401);
        }
        if(!userData.password().equals(password)) {
            throw new ServerException("unauthorized", 401);
        }

        authTokenData = new AuthTokenData(generateAuthToken(), username);
        authDataAccess.addAuthData(authTokenData);

        return authTokenData;
    }

    /**
     * Log out an existing user from the database
     * @param authToken is the current login session's authToken
     * @throws ServerException 401
     */
    public void logOut(String authToken) throws ServerException {
        AuthTokenData authData = authDataAccess.getAuthData(authToken);
        if (authData == null) {
            throw new ServerException("unauthorized", 401);
        }
        authDataAccess.removeAuthData(authData);
    }
