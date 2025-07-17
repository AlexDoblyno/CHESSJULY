package server;

import models.AuthTokenData;
import models.GameData;
import models.MessageResponse;
import models.UserData;
import chess.ChessGame;
import com.google.gson.JsonSyntaxException;
import service.Service;
import spark.*;
import com.google.gson.Gson;

import java.util.Collection;
import java.util.Map;

public class Server {

    // Create a single Gson object for all Gson operations
    private final Gson gson = new Gson();
    private final Service service;

    public Server() {
        service = new Service();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);
        Spark.get("/game", this::listGame);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clearDatabase);

        Spark.exception(ServerException.class, this::handleException);

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

