package pl.nehorowo.wallet.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.nehorowo.wallet.service.UserService;

public class JoinQuitListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void handlePlayerJoinEvent(PlayerJoinEvent event) {
        UserService.getInstance().compute(event.getPlayer().getUniqueId()).thenAccept(userController -> {
            userController.setPlayer(event.getPlayer());
            userController.setName(event.getPlayer().getName());

            //im thinking abt this
            userController.update();
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void handlePlayerQuitEvent(PlayerQuitEvent event) {
        UserService.getInstance().get(event.getPlayer().getUniqueId()).ifPresent(userController ->
                userController.setPlayer(null)
        );
    }
}
