package service;

import Models.AuthTokenData;
import Models.GameData;
import Models.UserData;
import chess.ChessGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.ServerException;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class ServiceUnitTests {

    private Service service;

    @BeforeEach
    void setUp() {
        service = new Service();
    }

    // Tests for register
    @Test
    void register_tests() {
        // 正向测试：注册一个新用户
        UserData validUser = new UserData("validUser", "password123", "sliu61@byu.edu");
        try {
            AuthTokenData authToken = service.register(validUser);
            boolean result = authToken != null && "validUser".equals(authToken.username());
            System.out.println("Register Successful Test: " + result); // 输出 true
            assertTrue(result);
        } catch (ServerException e) {
            fail("Register should not throw an exception for a valid user.");
        }

        // 负向测试：注册时传入 null 用户名
        UserData invalidUser = new UserData(null, "password123", "sliu61@byu.edu");
        try {
            service.register(invalidUser);
            System.out.println("Register Null Username Test: false"); // 输出 false
        } catch (ServerException e) {
            boolean result = e.getStatusCode() == 400 && "bad request".equals(e.getMessage());
            System.out.println("Register Null Username Test: " + result); // 输出 true
            assertTrue(result);
        }
    }

    // Tests for login
    @Test
    void login_tests() throws ServerException {
        // 正向测试：登录已注册用户
        UserData validUser = new UserData("validLogin", "password123", "sliu61@byu.edu");
        service.register(validUser);

        AuthTokenData authToken = service.login("validLogin", "password123");
        boolean successfulResult = authToken != null && "validLogin".equals(authToken.username());
        System.out.println("Login Successful Test: " + successfulResult); // 输出 true
        assertTrue(successfulResult);

        // 负向测试：登录时使用错误密码
        try {
            service.login("validLogin", "wrongPassword");
            System.out.println("Login Invalid Password Test: false"); // 输出 false
        } catch (ServerException e) {
            boolean result = e.getStatusCode() == 401;
            System.out.println("Login Invalid Password Test: " + result); // 输出 true
            assertTrue(result);
        }
    }

    // Tests for logOut
    @Test
    void logOut_tests() throws ServerException {
        // 正向测试：注销登录会话
        UserData user = new UserData("logOutUser", "password123", "sliu61@byu.edu");
        AuthTokenData authToken = service.register(user);

        boolean successfulResult = false;
        try {
            service.logOut(authToken.authToken());
            successfulResult = true;
        } catch (ServerException e) {
            successfulResult = false;
        }
        System.out.println("LogOut Successful Test: " + successfulResult); // 输出 true
        assertTrue(successfulResult);

        // 负向测试：注销时传入无效 token
        try {
            service.logOut("invalidToken");
            System.out.println("LogOut Unauthorized Test: false"); // 输出 false
        } catch (ServerException e) {
            boolean result = e.getStatusCode() == 401;
            System.out.println("LogOut Unauthorized Test: " + result); // 输出 true
            assertTrue(result);
        }
    }

    // Tests for listGames
    @Test
    void listGames_tests() throws ServerException {
        // 正向测试：列出空数据库中的所有游戏
        UserData user = new UserData("listGamesUser", "password123", "sliu61@byu.edu");
        AuthTokenData authToken = service.register(user);

        Collection<GameData> games = service.listGames(authToken.authToken());
        boolean successfulResult = games != null && games.isEmpty();
        System.out.println("ListGames No Games Found Test: " + successfulResult); // 输出 true
        assertTrue(successfulResult);

        // 负向测试：列出现有效 token
        try {
            service.listGames("invalidToken");
            System.out.println("ListGames Unauthorized Test: false"); // 输出 false
        } catch (ServerException e) {
            boolean result = e.getStatusCode() == 401;
            System.out.println("ListGames Unauthorized Test: " + result); // 输出 true
            assertTrue(result);
        }
    }

    // Tests for createGame
    @Test
    void createGame_tests() throws ServerException {
        // 正向测试：创建一个新游戏
        UserData user = new UserData("createGameUser", "password123", "sliu61@byu.edu");
        AuthTokenData authToken = service.register(user);

        int gameID = service.createGame(authToken.authToken(), "NewGame");
        boolean successfulResult = gameID > 0;
        System.out.println("CreateGame Successful Test: " + successfulResult); // 输出 true
        assertTrue(successfulResult);

        // 负向测试：创建游戏时游戏名称为 null
        try {
            service.createGame(authToken.authToken(), null);
            System.out.println("CreateGame Null Game Name Test: false"); // 输出 false
        } catch (ServerException e) {
            boolean result = e.getStatusCode() == 400 && "bad request".equals(e.getMessage());
            System.out.println("CreateGame Null Game Name Test: " + result); // 输出 true
            assertTrue(result);
        }
    }

    // Tests for clearApp
    @Test
    void clearApp_tests() throws ServerException {
        // 正向测试：清空数据库
        UserData user = new UserData("clearAppUser", "password123", "sliu61@byu.edu");
        AuthTokenData authToken = service.register(user);

        // 创建一个测试游戏
        service.createGame(authToken.authToken(), "GameToClear");

        // 调用 clearApp，确保执行成功
        service.clearApp();

        // 检查是否真的清空数据库（无游戏，认证数据移除）
        boolean successfulResult = false;
        try {
            service.listGames(authToken.authToken());
        } catch (ServerException e) {
            successfulResult = true; // 抛出异常说明数据库已被清空
        }
        System.out.println("ClearApp Successful Test: " + successfulResult); // 输出 true
        assertTrue(successfulResult);

        // 负向测试：尝试使用一个用户的 token（被清除后的）调用服务
        try {
            service.listGames(authToken.authToken()); // 应该抛出 "Unauthorized" 异常
            System.out.println("ClearApp Invalid Token Test: false"); // 输出 false
        } catch (ServerException e) {
            boolean result = e.getStatusCode() == 401; // 确认是未授权的异常
            System.out.println("ClearApp Invalid Token Test: " + result); // 输出 true
            assertTrue(result);
        }
    }

}