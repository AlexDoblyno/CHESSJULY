package dataaccess;

import models.AuthTokenData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTest {

    private MemoryAuthDataAccess authDataAccess;

    @BeforeEach
    public void setUp() {
        authDataAccess = new MemoryAuthDataAccess();
    }

    @Test
    public void testAddAuthDataPositive() {
        // 正测试：成功添加认证数据
        AuthTokenData authData = new AuthTokenData("token1", "user1");
        authDataAccess.addAuthData(authData);

        assertNotNull(authDataAccess.getAuthData("token1"), "AuthToken should be added successfully.");
    }

    @Test
    public void testAddAuthDataNegative() {
        // 负测试：添加重复的认证数据
        AuthTokenData authData = new AuthTokenData("token1", "user1");
        authDataAccess.addAuthData(authData);
        authDataAccess.addAuthData(authData);

        assertEquals(1, authDataAccess.authTokenDatabase.size(), "Duplicate AuthToken should not be added.");
    }

    @Test
    public void testRemoveAuthDataPositive() {
        // 正测试：成功移除认证数据
        AuthTokenData authData = new AuthTokenData("token1", "user1");
        authDataAccess.addAuthData(authData);
        authDataAccess.removeAuthData(authData);

        assertNull(authDataAccess.getAuthData("token1"), "AuthToken should be removed successfully.");
    }

    @Test
    public void testRemoveAuthDataNegative() {
        // 负测试：移除不存在的认证数据
        AuthTokenData authData = new AuthTokenData("token1", "user1");
        authDataAccess.removeAuthData(authData);

        assertNull(authDataAccess.getAuthData("token1"), "Removing non-existent AuthToken should have no effect.");
    }

    @Test
    public void testGetAuthDataPositive() {
        // 正测试：成功获取认证数据
        AuthTokenData authData = new AuthTokenData("token1", "user1");
        authDataAccess.addAuthData(authData);

        AuthTokenData retrievedData = authDataAccess.getAuthData("token1");
        assertNotNull(retrievedData, "AuthToken should be retrieved successfully.");
        assertEquals(authData, retrievedData, "Retrieved AuthToken should match the added AuthToken.");
    }

    @Test
    public void testGetAuthDataNegative() {
        // 负测试：试图获取不存在的认证数据
        assertNull(authDataAccess.getAuthData("nonExistentToken"), "Should return null for non-existent token.");
    }

    @Test
    public void testClearAuthTokens() {
        // 正测试：清空认证数据
        authDataAccess.addAuthData(new AuthTokenData("token1", "user1"));
        authDataAccess.addAuthData(new AuthTokenData("token2", "user2"));

        authDataAccess.clearAuthTokens();

        assertTrue(authDataAccess.authTokenDatabase.isEmpty(), "AuthToken database should be empty after clearing.");
    }
}