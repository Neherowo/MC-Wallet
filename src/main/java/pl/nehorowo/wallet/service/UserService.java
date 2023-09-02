package pl.nehorowo.wallet.service;

import pl.nehorowo.wallet.controller.UserController;
import pl.nehorowo.wallet.database.api.DatabaseCache;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class UserService implements DatabaseCache<UserController> {

    private static UserService instance;

    public static UserService getInstance() {
        if(instance == null) instance = new UserService();
        return instance;
    }

    private final Map<UUID, UserController> userControllerMap = new HashMap<>();

    public CompletableFuture<UserController> compute(UUID uuid){
        return CompletableFuture.completedFuture(userControllerMap.computeIfAbsent(uuid, UserController::new));
    }

    public Optional<UserController> get(UUID uuid){
        return Optional.ofNullable(userControllerMap.get(uuid));
    }

    @Override
    public void add(UserController userController) {
        userControllerMap.put(userController.getUuid(), userController);
    }
}
