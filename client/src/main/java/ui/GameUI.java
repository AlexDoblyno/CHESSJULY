package ui;
import websocket.commands.MakeMoveCommands;
import chess.*;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ChessClient;
import exception.ResponseException;
import exception.UIStateException;
import websocket.commands.UserGameCommand;

import java.util.Scanner;

public class GameUI extends BaseUI {
    private Boolean isPlayer;
    private ChessboardDrawer drawer;
    private ChessGame chessGame;
    private final ChessGame.TeamColor color;
    private final Scanner scanner;
    private final Integer gameID; // Game ID the user is connected to
    private final String authToken;
    private WebSocketClient webSocketClient;

    public GameUI(ChessClient client, ChessboardDrawer drawer, boolean isPlayer, Integer gameID, ChessGame.TeamColor teamColor, String authToken) throws Exception {
        super(client);
        state = UIStatesEnum.GAMEUI;
        this.drawer = drawer;
        this.isPlayer = isPlayer;
        this.color = teamColor;
        this.gameID = gameID;
        this.authToken = authToken;
        this.chessGame = drawer.getChessGame();
        this.scanner = new Scanner(System.in);
        try {
            this.webSocketClient = client.getServer().createWebSocketClient(this);
        } catch (Exception e) {
            throw new Exception("Error creating web socket client");
        }
    }

    @Override
    public String handler(String input) throws ResponseException {
        String[] tokens = input.split(" ");
        switch(tokens[0].toLowerCase()) {
            case "quit" -> quitGame();
            case "highlight"->highlightLegalMoves();
            case "move" -> makeMove();
            case "leave" -> leaveGame();
            default -> displayHelpInfo();
        };
        return null;
    }

    private void quitGame() throws ResponseException {
        int gameID = client.getDataCache().getCurrentGameID()+1;
        String gameName = client.getDataCache().getGameByIndex(gameID).gameName();

        client.getDataCache().setCurrentGameID(0);
        String returnStatement = "Left game " + gameName + "successfully.";
        client.logout();
        throw new UIStateException(new PostloginUI(client), returnStatement);
    }

    private Boolean leaveGame() throws UIStateException {
        System.out.print("Are you sure you want to leave? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (confirmation.equals("yes") || confirmation.equals("y")) {
            UserGameCommand resignCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            webSocketClient.sendMessage(resignCommand);
            System.out.println("You have left the game.");
            throw new UIStateException(new PostloginUI(client), "");
        } else {
            System.out.println("Leave canceled.");
            throw new UIStateException(this, "");
        }
    }


    @Override
    public String displayHelpInfo() {
        return """
    --- GAME COMMANDS ---
    Type a command to get the corresponding action.
    - quit      | Leave your current game.
    - help      | Display this help menu.
    """;
    }
//-------------------------//
    private ChessPosition parsePosition(String input) {
        if (input.length() == 2) {
            int col = input.charAt(0) - 'a' + 1;
            int row = input.charAt(1) - '1' + 1;
            if (col > 0 && col < 9 && row > 0 && row < 9) {
                return new ChessPosition(row, col);
            } else {
                System.out.println("Position must be in format [a-h][1-8].");
                return null;
            }
        } else {
            System.out.println("Position must be in format [a-h][1-8].");
            return null;
        }
    }





    private void highlightLegalMoves() {
        System.out.print("Enter the position of the piece to highlight (e.g., e2): ");
        ChessPosition position = parsePosition(scanner.nextLine().trim());
        if (position != null) {
            ChessGame.TeamColor bottom = (color == null) ? ChessGame.TeamColor.WHITE : color;
            drawer.printHighlightedMoves(drawer.getChessGame().getBoard(), bottom, drawer.getChessGame().validMoves(position));
        } else {
            System.out.println("Invalid position");
        }
    }

    private void makeMove() {
        if (this.chessGame.isOver()) {
            System.out.println("The game is over.");
            return;
        }
        if (!this.chessGame.getTeamTurn().equals(color)) {
            System.out.println("It is not your turn.");
            return;
        }
        System.out.print("Enter the start position (e.g., e2): ");
        ChessPosition start = parsePosition(scanner.nextLine().trim());
        if (start == null) {
            return;
        }
        ChessPiece piece = chessGame.getBoard().getPiece(start);
        if (piece == null || piece.getTeamColor() != this.color) {
            System.out.println("Invalid piece");
            return;
        }
        System.out.print("Enter the end position (e.g., e4): ");
        ChessPosition end = parsePosition(scanner.nextLine().trim());
        if (end == null) {
            return;
        }

        // Check if it's a pawn upgrade move
        ChessPiece.PieceType promotion = null;
        assert piece != null;
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if (((color == ChessGame.TeamColor.BLACK) && (end.getRow() == 1))
                    || ((color == ChessGame.TeamColor.WHITE) && (end.getRow() == 8))) {
                System.out.print("Enter promotion piece (e.g., queen): ");
                switch (scanner.nextLine().trim()) {
                    case "queen":
                        promotion = ChessPiece.PieceType.QUEEN;
                        break;
                    case "rook":
                        promotion = ChessPiece.PieceType.ROOK;
                        break;
                    case "bishop":
                        promotion = ChessPiece.PieceType.BISHOP;
                        break;
                    case "knight":
                        promotion = ChessPiece.PieceType.KNIGHT;
                        break;
                    default:
                        System.out.println("Invalid promotion piece");
                        makeMove();
                }
            }
        }
        ChessMove move = new ChessMove(start, end, promotion);
        try {
            chessGame.makeMove(move);
        } catch (InvalidMoveException e) {
            System.out.println("Invalid move");
            return;
        }

        try {
            UserGameCommand moveCommand = new MakeMoveCommands(UserGameCommand.CommandType.MAKE_MOVE, authToken,
                    gameID, move);

            webSocketClient.sendMessage(moveCommand);
        } catch (IllegalArgumentException e) {
            System.out.println("Error making move: " + e.getMessage());
        }
        System.out.println(drawer.drawBoardString(null));
    }

    public void loadGame(ChessGame game){
        this.chessGame = game;
        ChessGame.TeamColor bottom = (color == null) ? ChessGame.TeamColor.WHITE : color;
        drawer.printHighlightedMoves(chessGame.getBoard(), bottom, null);
        System.out.println("The game has been updated.");
    }

}
