package pl.nehorowo.wallet.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.nehorowo.wallet.controller.UserController;
import pl.nehorowo.wallet.service.UserService;

public class Placeholder extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "mc-wallet";
    }

    @Override
    public @NotNull String getAuthor() {
        return "nehorowo";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.1-SNAPSHOT";
    }

    public String onRequest(OfflinePlayer offlinePlayer, String identifier) {
        if (offlinePlayer.getPlayer() == null) return null;
        Player player = offlinePlayer.getPlayer();
        if(!identifier.equals("balans")) return null;
        UserController user = UserService.getInstance().get(player.getUniqueId()).get();

        return String.valueOf(user.getMoney());
    }

}
