package pl.nehorowo.wallet.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import pl.nehorowo.wallet.WalletPlugin;
import pl.nehorowo.wallet.database.api.DataUpdater;
import pl.nehorowo.wallet.database.api.stereotype.DataObject;
import pl.nehorowo.wallet.database.api.stereotype.PrimaryKey;
import pl.nehorowo.wallet.database.api.stereotype.Value;
import pl.nehorowo.wallet.service.UpdateService;

import java.sql.ResultSet;
import java.util.UUID;

@DataObject(table = "wusers") @Getter@Setter
public class UserController implements DataUpdater {

    @PrimaryKey(value = @Value(key = "uuid", type = "VARCHAR(64)"))
    private final UUID uuid;

    @Value(key = "name", type = "VARCHAR(32)")
    private String name;

    @Value(key = "money", type = "VARCHAR(32)")
    private int money;

    private Player player;

    public UserController(UUID uuid) {
        this.uuid = uuid;
    }

    @SneakyThrows
    public UserController(ResultSet resultSet){
        this.uuid = UUID.fromString(resultSet.getString("uuid"));
        this.name = resultSet.getString("name");
        this.money = Integer.parseInt(resultSet.getString("money"));
    }

    @Override
    public void update() {
        WalletPlugin.getInstance().getConnector().getScanner(UserController.class)
                .ifPresentOrElse(userControllerDataObjectScanner ->
                        userControllerDataObjectScanner.update(this)
                , () -> System.out.println("UserController scanner doesnt  not exists."));

        UpdateService.getInstance().getUserControllerSet()
                .remove(this);
    }
}
