package pl.nehorowo.wallet.task;

import org.bukkit.Bukkit;
import pl.nehorowo.wallet.controller.UserController;
import pl.nehorowo.wallet.service.UserService;

public class UpdateTask implements Runnable {

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> new UserController(player.getUniqueId()).update());
    }
}
