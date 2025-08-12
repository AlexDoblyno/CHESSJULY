package dataaccess;

import models.AuthTokenData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 基于SQL的认证数据访问实现类
 * 使用JDBC处理认证令牌的增删查操作
 *
 * @see <a href="https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html">Oracle官方try-with-resources文档</a>
 */
public class SqlAuthDataAccess implements AuthDataAccess, SqlAccess {

    /**
     * 数据库表创建语句集合
     * 使用VARCHAR类型存储认证令牌和用户名
     *
     * @see <a href="https://www.w3schools.com/sql/sql_create_table.asp">SQL CREATE TABLE语句详解</a>
     */
    private static final String[] CREATE_STATEMENTS = {
            """
        CREATE TABLE IF NOT EXISTS AuthData (
            `authToken` VARCHAR(64) NOT NULL PRIMARY KEY,
            `username` VARCHAR(256) NOT NULL
        )
        """
    };

    /**
     * 构造函数，初始化数据库配置
     * 使用异常包装技术处理检查型异常
     *
     * @see <a href="https://www.baeldung.com/java-exception-handling">Java异常处理最佳实践</a>
     */
    public SqlAuthDataAccess() {
        try {
            configureDatabase();
        } catch (ServerException | DataAccessException e) {
            throw new RuntimeException("认证数据访问层初始化失败", e);
        }
    }

    /**
     * 添加认证数据到数据库
     * 使用PreparedStatement防止SQL注入攻击
     *
     * @param authData 要添加的认证数据
     * @throws ServerException 添加失败时抛出
     * @see <a href="https://www.journaldev.com/2019/jdbc-preparedstatement-example">PreparedStatement使用指南</a>
     */
    @Override
    public void addAuthData(AuthTokenData authData) throws ServerException {
        final String INSERT_SQL = "INSERT INTO AuthData (authToken, username) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {

            stmt.setString(1, authData.authToken());
            stmt.setString(2, authData.username());
            stmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new ServerException("添加认证数据失败: " + e.getMessage());
        }
    }

    /**
     * 从数据库中移除指定的认证数据
     * 检查受影响行数确保令牌确实存在
     *
     * @param authData 要移除的认证数据
     * @throws ServerException 移除失败时抛出
     */
    @Override
    public void removeAuthData(AuthTokenData authData) throws ServerException {
        final String DELETE_SQL = "DELETE FROM AuthData WHERE authToken = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setString(1, authData.authToken());
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new ServerException("认证令牌不存在");
            }
        } catch (SQLException | DataAccessException e) {
            throw new ServerException("移除认证数据失败: " + e.getMessage());
        }
    }

    /**
     * 根据认证令牌查询认证数据
     * 使用try-with-resources自动管理资源，避免资源泄漏 [[1]]
     *
     * @param authToken 认证令牌
     * @return 认证数据对象
     * @throws ServerException 查询失败时抛出
     * @see <a href="https://www.cnblogs.com/lizhenghn/p/3815966.html">Java异常处理最佳实践</a>
     */
    @Override
    public AuthTokenData getAuthData(String authToken) throws ServerException {
        final String SELECT_SQL = "SELECT * FROM AuthData WHERE authToken = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_SQL)) {

            stmt.setString(1, authToken);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new ServerException("认证令牌不存在");
                }
                return new AuthTokenData(rs.getString("authToken"), rs.getString("username"));
            }
        } catch (SQLException | DataAccessException e) {
            throw new ServerException("获取认证数据失败: " + e.getMessage());
        }
    }

    /**
     * 清空所有认证令牌
     * 执行DELETE操作而非DROP表
     *
     * @throws ServerException 清空失败时抛出
     */
    @Override
    public void clearAuthTokens() throws ServerException {
        final String CLEAR_SQL = "DELETE FROM AuthData";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CLEAR_SQL)) {

            stmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new ServerException("清空认证数据失败: " + e.getMessage());
        }
    }

    /**
     * 根据认证令牌获取用户名
     * 复用getAuthData方法实现
     *
     * @param authToken 认证令牌
     * @return 用户名
     * @throws ServerException 获取失败时抛出
     */
    @Override
    public String getUsername(String authToken) throws ServerException {
        AuthTokenData authData = getAuthData(authToken);
        return authData.username();
    }

    /**
     * 通用的SQL更新执行方法
     * 使用try-with-resources确保资源自动关闭，避免内存泄漏 [[1]]
     *
     * @param statement SQL语句
     * @param params 参数列表
     * @return 受影响的行数
     * @throws ServerException 执行失败时抛出
     * @see <a href="https://www.oracle.com/java/technologies/exception-handling-javase.html">Oracle异常处理指南</a>
     */
    @Override
    public int executeUpdate(String statement, Object... params) throws ServerException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(statement)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return stmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new ServerException("SQL更新失败: " + e.getMessage());
        }
    }

    /**
     * 配置数据库结构
     * 确保认证数据表存在
     * 优先使用try-with-resources：比手动try-finally更安全简洁 [[7]]
     *
     * @throws ServerException 配置失败时抛出
     * @throws DataAccessException 数据访问异常
     */
    @Override
    public void configureDatabase() throws ServerException, DataAccessException {
        DatabaseManager.createDatabase();

        try (Connection conn = DatabaseManager.getConnection()) {
            for (String sql : CREATE_STATEMENTS) {
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new ServerException("数据库配置失败: " + e.getMessage());
        } catch (DataAccessException e) {
            throw new ServerException("数据访问异常: " + e.getMessage());
        }
    }
}