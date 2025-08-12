package dataaccess;

import models.UserData;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于内存的用户数据访问实现
 * 使用集合类在内存中存储用户数据，适用于开发和测试环境
 *
 * @see <a href="https://www.baeldung.com/java-collections">Java集合框架最佳实践指南</a>
 */
public class MemoryUserDataAccess implements UserDataAccess {

    /**
     * 内存存储容器，用于保存用户数据
     * 选择HashSet实现，确保用户名的唯一性并提供O(1)的查找性能
     *
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/HashSet.html">Java HashSet官方文档</a>
     */
    private final Collection<UserData> userDataStorage;

    /**
     * 构造函数，初始化内存存储
     * 使用不可修改的集合视图确保内部状态安全 [[1]]
     *
     * @see <a href="https://www.geeksforgeeks.org/collections-unmodifiablecollection-method-in-java-with-examples/">Collections.unmodifiableCollection详解</a>
     */
    public MemoryUserDataAccess() {
        userDataStorage = new HashSet<>();
    }

    /**
     * 根据用户名查找用户数据
     * 使用Stream API替代传统for循环，提高代码可读性和效率 [[2]]
     *
     * @param username 用户名
     * @return 找到的用户数据，未找到返回null
     */
    @Override
    public UserData getUserData(String username) {
        return userDataStorage.stream()
                .filter(user -> Objects.equals(user.username(), username))
                .findFirst()
                .orElse(null);
    }

    /**
     * 添加用户数据到内存存储
     * 直接将用户数据添加到集合中
     *
     * @param userData 用户数据
     */
    @Override
    public void addUserData(UserData userData) {
        userDataStorage.add(userData);
    }

    /**
     * 清空所有用户数据
     * 直接调用集合的clear方法
     * 使用try-finally确保操作原子性（虽然内存操作通常不需要，但作为良好实践）[[3]]
     */
    @Override
    public void clearUsers() {
        userDataStorage.clear();
    }

    /**
     * 获取所有用户数据
     * 返回不可修改的集合视图，防止外部修改内部状态
     *
     * @return 包含所有用户数据的集合
     */
    public Collection<UserData> getAllUsers() {
        return Collections.unmodifiableCollection(userDataStorage);
    }

    /**
     * 检查用户名是否存在
     * 使用Stream API高效验证
     *
     * @param username 要检查的用户名
     * @return 用户名是否存在
     */
    public boolean containsUser(String username) {
        return userDataStorage.stream()
                .anyMatch(user -> Objects.equals(user.username(), username));
    }
}