package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import dataaccess.ServerException;
import dataaccess.UserDataAccess;
import models.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.MakeMoveCommands;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.*;

@WebSocket
public class WebSocketServer {
    private UserDataAccess userDA;
    private GameDataAccess gameDA;
    private AuthDataAccess authDA;
    private static final Map<Integer, List<Session>> SESSIONS = new HashMap<>();

    public WebSocketServer(AuthDataAccess authDA, GameDataAccess gameDA, UserDataAccess userDA) {
        this.userDA = userDA;
        this.gameDA = gameDA;
        this.authDA = authDA;
    }

    @OnWebSocketConnect
    public void onOpen(Session session) {
        System.out.println("New connection");
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        System.out.println("New message: " + message);
        // Deserialize message into UserGameCommand
        if (message.contains("MAKE_MOVE")) {
            MakeMoveCommands command = new Gson().fromJson(message, MakeMoveCommands.class);
            try {
                handleMakeMove(command, session);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            return;
        }
        UserGameCommand command = parseCommand(message);
        try {
            handleCommand(command, session);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private UserGameCommand parseCommand(String message) {
        return new Gson().fromJson(message, UserGameCommand.class);
    }

    private void handleCommand(UserGameCommand command, Session session) throws DataAccessException {
        // Process CONNECT, MAKE_MOVE, LEAVE, and RESIGN commands
        switch (command.getCommandType()) {
            case CONNECT:
                handleConnect(command, session);
                break;
            case LEAVE:
                handleLeave(command, session);
                break;
            case RESIGN:
                handleResign(command, session);
                break;
            default:
                System.out.println("Unknown command type: " + command.getCommandType());
                break;
        }
    }

    private void handleConnect(UserGameCommand command, Session session) throws DataAccessException {
        // Load game, notify players, etc.
        Integer gameID = command.getGameID();
        String authToken = command.getAuthToken();
        String username = null;
        try {
            username = authDA.getUsername(authToken);
        } catch (ServerException e) {
            ErrorMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "invalid authToken");
            String json = new Gson().toJson(msg);
            sendMessage(json, session);
            throw new DataAccessException(e.getMessage());
        }
        GameData gameData = null;
        try {
            gameData = gameDA.getGameByID(gameID);
        } catch (ServerException e) {
            ErrorMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "game not found");
            String json = new Gson().toJson(msg);
            sendMessage(json, session);
            throw new DataAccessException(e.getMessage());
        }

        try {
            if (authDA.getUsername(authToken) == null) {
                System.out.println("User not found.");
                ErrorMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "invalid authToken");
                String json = new Gson().toJson(msg);
                sendMessage(json, session);
                return;
            }
            if (gameDA.getGameByID(gameID) == null) {
                System.out.println("Game not found.");
                ErrorMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "game not found");
                String json = new Gson().toJson(msg);
                sendMessage(json, session);
                return;
            }
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }

        SESSIONS.computeIfAbsent(gameID, k -> new ArrayList<>()).add(session);

        String color = "observer";
        if (Objects.equals(gameData.whiteUsername(), username)) {
            color = "white";
        } else if (Objects.equals(gameData.blackUsername(), username)) {
            color = "black";
        }

        NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, username +
                " has joined the game as " + color);
        String json = new Gson().toJson(notification);
        broadcastMessageExclude(json, gameID, session);

        LoadGameMessage gameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game());
        String jsonGame = new Gson().toJson(gameMessage);
        sendMessage(jsonGame, session);
    }

    private void handleMakeMove(MakeMoveCommands command, Session session)
            throws DataAccessException {
        // Validate move, update game, notify players
        Integer gameID = command.getGameID();
        String authToken = command.getAuthToken();
        GameData gameData = null;
        try {
            gameData = gameDA.getGameByID(gameID);
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }
        ChessMove move = command.getMove();

        try {
            if (authDA.getAuthData(authToken) == null) {
                ErrorMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "invalid authToken");
                String json = new Gson().toJson(msg);
                sendMessage(json, session);
                return;
            }
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }
        try {
            if (!(authDA.getUsername(authToken).equals(gameData.blackUsername()) && gameData.game().getTeamTurn() == ChessGame.TeamColor.BLACK)
                    && !(authDA.getUsername(authToken).equals(gameData.whiteUsername()) && gameData.game().getTeamTurn() == ChessGame.TeamColor.WHITE)) {
                ErrorMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "wrong turn");
                String json = new Gson().toJson(msg);
                sendMessage(json, session);
                return;
            }
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }
        try {
            if (gameDA.getGameByID(gameID) == null) {
                ErrorMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "game not found");
                String json = new Gson().toJson(msg);
                sendMessage(json, session);
                return;
            }
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }
        if (gameData.game().isOver()) {
            ErrorMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "game is over");
            String json = new Gson().toJson(msg);
            sendMessage(json, session);
            return;
        }
        try {
            ChessGame game = gameData.game();
            ChessGame.TeamColor color = game.getTeamTurn();
            String username = authDA.getUsername(authToken);
            ChessGame.TeamColor opponent = (game.getTeamTurn() == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
            String opponentName = (opponent == ChessGame.TeamColor.WHITE) ? gameData.whiteUsername() : gameData.blackUsername();
            game.makeMove(move);
            //gameDA.updateGame(gameData);
            LoadGameMessage msgLoad = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            String json = new Gson().toJson(msgLoad);
            broadcastMessage(json, gameID);

            NotificationMessage msg = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, color +
                    " user " + username + " has made a move from " + formatPosition(move.getStartPosition()) + " to "
                    + formatPosition(move.getEndPosition()));
            json = new Gson().toJson(msg);
            broadcastMessageExclude(json, gameID, session);
            if (game.isInCheckmate(opponent)) {
                msg = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, opponent +
                        " user " + opponentName + " is in checkmate, " + color + " user " + username + " wins");
                json = new Gson().toJson(msg);
                broadcastMessage(json, gameID);
            } else if (game.isInCheck(opponent)) {
                msg = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, opponent +
                        " user " + opponentName + " is in check");
                json = new Gson().toJson(msg);
                broadcastMessage(json, gameID);
                game.setGameOverStatus(true);
            } else if (game.isInStalemate(opponent) || game.isInStalemate(color)) {
                msg = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "game ends in " +
                        "stalemate");
                json = new Gson().toJson(msg);
                broadcastMessage(json, gameID);
                game.setGameOverStatus(true);
            }
        } catch (InvalidMoveException e) {
            ErrorMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "invalid move");
            String json = new Gson().toJson(msg);
            sendMessage(json, session);
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private String formatPosition(ChessPosition position) {
        char col = (char) ('a' + position.getColumn() - 1);
        return col + String.valueOf(position.getRow());
    }

    private void handleLeave(UserGameCommand command, Session session) throws DataAccessException {
        // Notify other players and update the game state
        Integer gameID = command.getGameID();
        String authToken = command.getAuthToken();
        String username = null;
        GameData gameData = null;
        try {
            username = authDA.getUsername(authToken);
            gameData = gameDA.getGameByID(gameID);
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }

        try {
            if (authDA.getAuthData(authToken) == null) {
                ErrorMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "invalid authToken");
                String json = new Gson().toJson(msg);
                sendMessage(json, session);
                return;
            }
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }
        try {
            if (gameDA.getGameByID(gameID) == null) {
                ErrorMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "game not found");
                String json = new Gson().toJson(msg);
                sendMessage(json, session);
                return;
            }
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }

        String color = "observer";
        if (Objects.equals(gameData.whiteUsername(), username)) {
            color = "white";
        } else if (Objects.equals(gameData.blackUsername(), username)) {
            color = "black";
        }

        NotificationMessage msg = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                color + " user " + username + " has left the game");
        String json = new Gson().toJson(msg);
        broadcastMessageExclude(json, gameID, session);
        if (!color.equals("observer")) {
            ChessGame.TeamColor teamColor = (color.equals("white")) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            try {
                gameDA.updateGame(teamColor, gameID, null);
            } catch (ServerException e) {
                throw new DataAccessException(e.getMessage());
            }
        }
        SESSIONS.get(gameID).remove(session);
    }

    private void handleResign(UserGameCommand command, Session session) throws DataAccessException {
        // End game and notify all connected clients
        Integer gameID = command.getGameID();
        String authToken = command.getAuthToken();
        String username = null;
        try {
            username = authDA.getUsername(authToken);
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }
        GameData gameData = null;
        try {
            gameData = gameDA.getGameByID(gameID);
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }

        if (!username.equals(gameData.blackUsername())
                && !username.equals(gameData.whiteUsername())) {
            ErrorMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "wrong turn");
            String json = new Gson().toJson(msg);
            sendMessage(json, session);
            return;
        }
        try {
            if (authDA.getUsername(authToken) == null) {
                ErrorMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "invalid authToken");
                String json = new Gson().toJson(msg);
                sendMessage(json, session);
                return;
            }
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }
        try {
            if (gameDA.getGameByID(gameID) == null) {
                ErrorMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "game not found");
                String json = new Gson().toJson(msg);
                sendMessage(json, session);
                return;
            }
        } catch (ServerException e) {
            throw new DataAccessException(e.getMessage());
        }
        if (gameData.game().isOver()) {
            ErrorMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "game is over");
            String json = new Gson().toJson(msg);
            sendMessage(json, session);
        } else {
            ChessGame game = gameData.game();
            ChessGame.TeamColor color = (username.equals(gameData.whiteUsername())) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            ChessGame.TeamColor opponent = (color == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
            String opponentName = (opponent == ChessGame.TeamColor.WHITE) ? gameData.whiteUsername() : gameData.blackUsername();
            NotificationMessage msg = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, color
                    + " user " + username + " has resigned, " + opponent + " user " + opponentName + " wins");
            String json = new Gson().toJson(msg);
            broadcastMessage(json, gameID);
            game.setGameOverStatus(true);
            //gameDA.updateChessGame(game, gameID);
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Error: " + throwable.getMessage());
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("Connection closed, reason: " + reason);
    }

    private void broadcastMessage(String msg, Integer gameID) {
        List<Session> gameSessions = SESSIONS.get(gameID);
        if (gameSessions != null) {
            for (Session session : gameSessions) {
                if (session.isOpen()) {
                    sendMessage(msg, session);
                }
            }
        }
    }

    private void broadcastMessageExclude(String msg, Integer gameID, Session exclude) {
        List<Session> gameSessions = SESSIONS.get(gameID);
        if (gameSessions != null) {
            for (Session session : gameSessions) {
                if (session != exclude && session.isOpen()) {
                    sendMessage(msg, session);
                }
            }
        }
    }

    private void sendMessage(String message, Session session) {
        try {
            session.getRemote().sendString(message);
        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }
}
