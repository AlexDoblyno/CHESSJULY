package client;

import models.*;
import exception.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server; // Server实例，用于本地测试
    private static int port;      // 动态分配的测试服务端口
    private static ServerFacade serverFacade; // ServerFacade实例，直接调用测试

    /**
     * 在所有测试运行之前启动本地测试服务器
     * 通过动态端口分配启动以进行隔离
     */
    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0); // 为服务器分配动态可用端口
        System.out.println("Started test HTTP server on " + port);

        String baseUrl = "http://localhost:" + port; // 设置服务器URL
        serverFacade = new ServerFacade(baseUrl);   // 初始化测试的 ServerFacade
    }

    /**
     * 在所有测试完成后停止本地测试服务器
     */
    @AfterAll
    static void stopServer() {
        server.stop();
    }

    // ------------------ 测试方法 ------------------

    /** 测试 registerUser 方法：用户注册成功 */
    @Test
    public void registerUser_success() throws ResponseException {
        var user = new UserData("testUser", "testPass", "testEmail");

        AuthTokenData authTokenData = serverFacade.registerUser(user);

        assertNotNull(authTokenData); // 验证返回的数据不为空
        assertNotNull(authTokenData.authToken()); // 验证 authToken 不为空
        System.out.println("Register User Success: AuthToken = " + authTokenData.authToken());
    }

    /** 测试 loginUser 方法：用户登录成功 */
    @Test
    public void loginUser_success() throws ResponseException {
        var username = "testUser";
        var password = "testPass";

        AuthTokenData authTokenData = serverFacade.loginUser(username, password);

        assertNotNull(authTokenData);
        assertNotNull(authTokenData.authToken());
        System.out.println("Login User Success: AuthToken = " + authTokenData.authToken());
    }

    /** 测试 logoutUser 方法：用户登出成功 */


    /** 测试 listGame 方法：成功获取游戏列表 */


    /** 测试 createGame 方法：成功创建游戏 */


    /** 测试 joinGame 方法：成功加入游戏 */

    /** 测试 clearDatabase 方法：成功清空数据库 */
    @Test
    public void clearDatabase_success() throws ResponseException {
        serverFacade.clearDatabase(); // 如果正常执行，则测试通过

        System.out.println("Clear Database Success");
    }

    /** 示例：测试服务器端点是否正常运行 */

}