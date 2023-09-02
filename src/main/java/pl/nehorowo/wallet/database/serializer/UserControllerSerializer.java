package pl.nehorowo.wallet.database.serializer;

import pl.nehorowo.wallet.controller.UserController;
import pl.nehorowo.wallet.database.api.ItemSerializer;
import pl.nehorowo.wallet.service.UserService;

import java.util.UUID;

public class UserControllerSerializer implements ItemSerializer<UserController> {
    @Override
    public Class<UserController> supportedClass() {
        return UserController.class;
    }

    @Override
    public UserController serialize(String s) {
        return UserService.getInstance().compute(UUID.fromString(s)).join();
    }

    @Override
    public String deserialize(UserController userController) {
        return userController.getUuid().toString();
    }
}
