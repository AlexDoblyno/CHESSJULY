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
    void register_successful() throws ServerException {
        // 正向测试：注册一个新用户
        UserData user = new UserData("validUser", "password123","sliu61@byu.edu");
        AuthTokenData authToken = service.register(user);

        // 验证返回 AuthTokenData 对象
        assertNotNull(authToken);
        assertEquals("validUser", authToken.username());
    }

    @Test
    void register_usernameTaken() {
        // 负向测试：注册一个已存在的用户名
        UserData user = new UserData("duplicateUser", "password123","sliu61@byu.edu");

        // 注册用户第一次应成功
        assertDoesNotThrow(() -> service.register(user));

        // 再次注册同名用户应抛出异常
        ServerException exception = assertThrows(ServerException.class, () -> service.register(user));
        assertEquals(403, exception.getStatusCode());
        assertEquals("already taken", exception.getMessage());
    }

    // Tests for login
    @Test
    void login_successful() throws ServerException {
        // 正向测试：使用已注册用户登录
        UserData user = new UserData("validLogin", "password123","sliu61@byu.edu");
        service.register(user);

        AuthTokenData authToken = service.login("validLogin", "password123");

        // 验证返回的 AuthTokenData 非空并且用户名正确
        assertNotNull(authToken);
        assertEquals("validLogin", authToken.username());
    }

    @Test
    void login_invalidCredentials() {
        // 负向测试：尝试使用错误用户名或密码登录
        UserData user = new UserData("invalidLogin", "password123", "sliu61@byu.edu");

        // 注册用户
        assertDoesNotThrow(() -> service.register(user));

        // 使用错误的密码登录应抛出异常
        ServerException exception1 = assertThrows(ServerException.class, () -> service.login("invalidLogin", "wrongPassword"));
        assertEquals(401, exception1.getStatusCode());

        // 使用不存在的用户名登录应抛出异常
        ServerException exception2 = assertThrows(ServerException.class, () -> service.login("nonExistentUser", "password123"));
        assertEquals(401, exception2.getStatusCode());
    }

    // Tests for logOut
    @Test
    void logOut_successful() throws ServerException {
        // 正向测试：注销登录会话
        UserData user = new UserData("logOutUser", "password123","sliu61@byu.edu");
        AuthTokenData authToken = service.register(user);

        assertDoesNotThrow(() -> service.logOut(authToken.authToken()));
    }

    @Test
    void logOut_unauthorized() {
        // 负向测试：尝试使用无效的 token 注销
        ServerException exception = assertThrows(ServerException.class, () -> service.logOut("invalidToken"));
        assertEquals(401, exception.getStatusCode());
    }

    // Tests for listGames
    @Test
    void listGames_noGamesFound() throws ServerException {
        // 正向测试：列出当前空数据库中的所有游戏
        UserData user = new UserData("listGamesUser", "password123","sliu61@byu.edu");
        AuthTokenData authToken = service.register(user);

        Collection<GameData> games = service.listGames(authToken.authToken());
        assertNotNull(games);
        assertTrue(games.isEmpty());
    }

    @Test
    void listGames_unauthorized() {
        // 负向测试：尝试用无效 token 列出游戏
        ServerException exception = assertThrows(ServerException.class, () -> service.listGames("invalidToken"));
        assertEquals(401, exception.getStatusCode());
    }

    // Tests for createGame
    @Test
    void createGame_successful() throws ServerException {
        // 正向测试：创建一个新游戏
        UserData user = new UserData("createGameUser", "password123","sliu61@byu.edu");
        AuthTokenData authToken = service.register(user);

        int gameID = service.createGame(authToken.authToken(), "NewGame");
        assertTrue(gameID > 0);
    }

    @Test
    void createGame_alreadyTaken() throws ServerException {
        // 负向测试：创建重复的游戏名
        UserData user = new UserData("createDuplicateGameUser", "password123","sliu61@byu.edu");
        AuthTokenData authToken = service.register(user);

        service.createGame(authToken.authToken(), "DuplicateGame");

        ServerException exception = assertThrows(ServerException.class, () -> service.createGame(authToken.authToken(), "DuplicateGame"));
        assertEquals(403, exception.getStatusCode());
        assertEquals("already taken", exception.getMessage());
    }

    @Test
    void createGame_unauthorized() {
        // 负向测试：尝试使用无效 token 创建游戏
        ServerException exception = assertThrows(ServerException.class, () -> service.createGame("invalidToken", "NewGame"));
        assertEquals(401, exception.getStatusCode());
    }

    // Tests for joinGame
    @Test
    void joinGame_successful() throws ServerException {
        // 正向测试：加入一个可用队伍
        UserData user = new UserData("joinGameUser", "password123","sliu61@byu.edu");
        AuthTokenData authToken = service.register(user);

        int gameID = service.createGame(authToken.authToken(), "JoinableGame");
        assertDoesNotThrow(() -> service.joinGame(authToken.authToken(), ChessGame.TeamColor.WHITE, gameID));
    }

    @Test
    void joinGame_alreadyTaken() throws ServerException {
        // 负向测试：尝试加入已被占用的队伍
        UserData user = new UserData("joinTakenUser", "password123","sliu61@byu.edu");
        AuthTokenData authToken = service.register(user);

        int gameID = service.createGame(authToken.authToken(), "JoinTakenGame");
        service.joinGame(authToken.authToken(), ChessGame.TeamColor.WHITE, gameID);

        ServerException exception = assertThrows(ServerException.class, () -> service.joinGame(authToken.authToken(), ChessGame.TeamColor.WHITE, gameID));
        assertEquals(403, exception.getStatusCode());
        assertEquals("already taken", exception.getMessage());
    }

    @Test
    void joinGame_invalidGame() throws ServerException {
        // 负向测试：尝试加入不存在的游戏
        UserData user = new UserData("invalidGameUser", "password123","sliu61@byu.edu");
        AuthTokenData authToken = service.register(user);

        ServerException exception = assertThrows(ServerException.class, () -> service.joinGame(authToken.authToken(), ChessGame.TeamColor.WHITE, 999));
        assertEquals(400, exception.getStatusCode());
        assertEquals("bad request", exception.getMessage());
    }

    @Test
    void joinGame_unauthorized() {
        // 负向测试：尝试使用无效 token 加入游戏
        ServerException exception = assertThrows(ServerException.class, () -> service.joinGame("invalidToken", ChessGame.TeamColor.WHITE, 1));
        assertEquals(401, exception.getStatusCode());
    }

    // Test for clearApp
    @Test
    void clearApp_successful() throws ServerException {
        // 正向测试：清空数据库
        UserData user = new UserData("clearAppUser", "password123","sliu61@byu.edu");
        AuthTokenData authToken = service.register(user);

        service.createGame(authToken.authToken(), "GameToClear");

        service.clearApp();

        // 确保所有数据被清空
        assertThrows(ServerException.class, () -> service.listGames(authToken.authToken()));
        assertNull(service.authDataAccess.getAuthData(authToken.authToken()));
    }
}