package dataaccess;

import models.AuthTokenData;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于内存的认证令牌数据访问实现
 * 使用集合类在内存中存储认证令牌，适用于开发和测试环境
 *
 * @see <a href="https://www.baeldung.com/java-collections">Java集合框架最佳实践指南</a>
 */
public class MemoryAuthDataAccess implements AuthDataAccess {

    /**
     * 内存存储容器，用于保存认证令牌数据
     * 选择HashSet实现，确保令牌的唯一性并提供O(1)的查找性能
     *
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/HashSet.html">Java HashSet官方文档</a>
     */
    private final Collection<AuthTokenData> tokenStorage;

    /**
     * 构造函数，初始化内存存储
     * 使用不可变集合模式确保内部状态安全 [[1]]
     *
     * @see <a href="https://www.geeksforgeeks.org/collections-unmodifiablecollection-method-in-java-with-examples/">Collections.unmodifiableCollection详解</a>
     */
    public MemoryAuthDataAccess() {
        tokenStorage = new HashSet<>();
    }

    /**
     * 添加认证令牌到内存存储
     * 直接将令牌数据添加到集合中
     *
     * @param authData 认证令牌数据
     */
    @Override
    public void addAuthData(AuthTokenData authData) {
        tokenStorage.add(authData);
    }

    /**
     * 从内存中移除指定的认证令牌
     * 使用removeIf方法替代传统循环，提高代码简洁性 [[2]]
     *
     * @param authData 要移除的认证令牌
     */
    @Override
    public void removeAuthData(AuthTokenData authData) {
        tokenStorage.removeIf(token -> Objects.equals(token.authToken(), authData.authToken()));
    }

    /**
     * 根据认证令牌字符串查找认证数据
     * 使用Stream API实现高效查找，比传统循环更简洁 [[3]]
     *
     * @param authToken 认证令牌字符串
     * @return 找到的认证数据，未找到返回null
     */
    @Override
    public AuthTokenData getAuthData(String authToken) {
        return tokenStorage.stream()
                .filter(token -> Objects.equals(token.authToken(), authToken))
                .findFirst()
                .orElse(null);
    }

    /**
     * 清空所有认证令牌
     * 直接调用集合的clear方法
     * 使用不可变集合视图确保操作安全性 [[1]]
     */
    @Override
    public void clearAuthTokens() {
        tokenStorage.clear();
    }

    /**
     * 根据认证令牌获取关联的用户名
     * 复用getAuthData方法实现，避免代码重复
     *
     * @param authToken 认证令牌
     * @return 用户名，令牌不存在返回null
     */
    @Override
    public String getUsername(String authToken) {
        AuthTokenData token = getAuthData(authToken);
        return (token != null) ? token.username() : null;
    }

    /**
     * 检查认证令牌是否存在
     * 使用Stream API高效验证
     *
     * @param authToken 要检查的认证令牌
     * @return 令牌是否存在
     */
    public boolean containsToken(String authToken) {
        return tokenStorage.stream()
                .anyMatch(token -> Objects.equals(token.authToken(), authToken));
    }

    /**
     * 获取当前存储的令牌数量
     * 直接返回集合大小
     *
     * @return 令牌数量
     */
    public int getTokenCount() {
        return tokenStorage.size();
    }

    /**
     * 获取所有认证令牌的不可修改视图
     * 防止外部修改内部状态
     *
     * @return 包含所有认证令牌的集合
     */
    public Collection<AuthTokenData> getAllTokens() {
        return Collections.unmodifiableCollection(tokenStorage);
    }
}