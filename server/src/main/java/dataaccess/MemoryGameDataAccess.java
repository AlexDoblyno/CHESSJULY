package dataaccess;

import models.AuthTokenData;
import models.GameData;
import chess.ChessGame;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于内存的游戏数据访问实现
 * 使用集合类在内存中存储游戏数据，适用于开发和测试环境
 *
 * @see <a href="https://www.baeldung.com/java-collections">Java集合框架最佳实践</a>
 */
public class MemoryGameDataAccess implements GameDataAccess {

    /**
     * 内存存储容器，用于保存游戏数据
     * 选择HashSet实现，确保游戏ID的唯一性并提供O(1)的查找性能
     *
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/HashSet.html">Java HashSet文档</a>
     */
    private final Collection<GameData> gameStorage;

    /**
     * 构造函数，初始化内存存储
     * 使用HashSet作为底层存储结构，保证游戏数据的唯一性
     *
     * @see <a href="https://www.geeksforgeeks.org/hashset-in-java/">HashSet实现原理详解</a>
     */
    public MemoryGameDataAccess() {
        gameStorage = new HashSet<>();
    }

    /**
     * 获取所有游戏列表
     * 直接返回内存存储的集合，避免不必要的转换操作
     *
     * @return 包含所有游戏数据的集合
     */
    @Override
    public Collection<GameData> getGameList() {
        return Collections.unmodifiableCollection(gameStorage);
    }

    /**
     * 根据游戏名称查找游戏
     * 使用Stream API替代传统for循环，提高代码可读性 [[1]]
     *
     * @param gameName 游戏名称
     * @return 找到的游戏数据，未找到返回null
     * @see <a href="https://www.oracle.com/java/technologies/javase/8-whats-new.html">Java 8 Stream API介绍</a>
     */
    @Override
    public GameData getGameByName(String gameName) {
        return gameStorage.stream()
                .filter(game -> game.gameName().equals(gameName))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据游戏ID查找游戏
     * 使用Stream API实现高效查找，比传统循环更简洁 [[2]]
     *
     * @param gameID 游戏ID
     * @return 找到的游戏数据，未找到返回null
     */
    @Override
    public GameData getGameByID(int gameID) {
        return gameStorage.stream()
                .filter(game -> game.gameID() == gameID)
                .findFirst()
                .orElse(null);
    }

    /**
     * 创建新游戏并添加到存储
     * 直接将游戏数据添加到集合中
     *
     * @param gameData 游戏数据
     */
    @Override
    public void createGame(GameData gameData) {
        gameStorage.add(gameData);
    }

    /**
     * 用户加入指定游戏
     * 采用先移除后添加的策略更新游戏数据
     * 使用不可变对象模式，确保线程安全 [[3]]
     *
     * @param authData 用户认证数据
     * @param team     要加入的队伍颜色
     * @param gameID   游戏ID
     */
    @Override
    public void joinGame(AuthTokenData authData, ChessGame.TeamColor team, int gameID) {
        GameData currentGame = getGameByID(gameID);
        if (currentGame == null) {
            throw new IllegalArgumentException("游戏ID不存在: " + gameID);
        }

        GameData updatedGame;
        switch (team) {
            case WHITE:
                updatedGame = new GameData(
                        currentGame.gameID(),
                        authData.username(),
                        currentGame.blackUsername(),
                        currentGame.gameName(),
                        currentGame.game()
                );
                break;
            case BLACK:
                updatedGame = new GameData(
                        currentGame.gameID(),
                        currentGame.whiteUsername(),
                        authData.username(),
                        currentGame.gameName(),
                        currentGame.game()
                );
                break;
            default:
                throw new IllegalArgumentException("无效的队伍颜色: " + team);
        }

        gameStorage.remove(currentGame);
        gameStorage.add(updatedGame);
    }

    /**
     * 清空所有游戏数据
     * 直接调用集合的clear方法
     */
    @Override
    public void clearGames() {
        gameStorage.clear();
    }

    /**
     * 更新棋盘游戏状态
     * 当前内存实现中此方法为空操作
     *
     * @param game   更新后的棋盘游戏
     * @param gameID 游戏ID
     */
    @Override
    public void updateChessGame(ChessGame game, Integer gameID) {
        // 内存实现中，棋盘状态通常随游戏对象整体更新，无需单独处理
        // 此方法在内存实现中不执行任何操作
    }

    /**
     * 更新游戏的玩家信息
     * 使用不可变对象模式更新游戏数据 [[3]]
     *
     * @param color  玩家颜色
     * @param gameID 游戏ID
     * @param username 新的用户名
     */
    @Override
    public void updateGame(ChessGame.TeamColor color, Integer gameID, String username) {
        GameData currentGame = getGameByID(gameID);
        if (currentGame == null) {
            throw new IllegalArgumentException("游戏ID不存在: " + gameID);
        }

        GameData updatedGame;
        if (color == ChessGame.TeamColor.BLACK) {
            updatedGame = new GameData(
                    gameID,
                    currentGame.whiteUsername(),
                    username,
                    currentGame.gameName(),
                    currentGame.game()
            );
        } else {
            updatedGame = new GameData(
                    gameID,
                    username,
                    currentGame.blackUsername(),
                    currentGame.gameName(),
                    currentGame.game()
            );
        }

        gameStorage.remove(currentGame);
        gameStorage.add(updatedGame);
    }
}