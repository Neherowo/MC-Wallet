package pl.nehorowo.wallet.commands;

import com.google.common.collect.ImmutableMultimap;
import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.nehorowo.wallet.WalletPlugin;
import pl.nehorowo.wallet.commands.api.CommandAPI;
import pl.nehorowo.wallet.menu.WalletMenu;
import pl.nehorowo.wallet.service.UserService;
import pl.nehorowo.wallet.util.TextUtil;

import java.util.Arrays;
import java.util.List;

public class WalletCommand extends CommandAPI {

    public WalletCommand() {
        super(
                "wallet",
                WalletPlugin.getInstance().getConfiguration().getPermissionPrefix() + ".wallet",
                "Komenda do zarzadzania portfelem gracza.",
                "/wallet",
                Arrays.asList("portfel", "money", "pieniadze", "kasa", "hajs", "konto")
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            TextUtil.sendMessage(sender, "&cTa komenda jest dostepna tylko dla graczy.");
            return;
        }

        if(args.length == 0) new WalletMenu().openServicesMenu(player);
        else if(args.length == 1 && args[0].equalsIgnoreCase("balans")) {
            UserService.getInstance().get(player.getUniqueId()).ifPresent(user ->
                    getMessageConfiguration()
                            .getYouHave()
                            .addPlaceholder(ImmutableMultimap.of("[MONEY]", user.getMoney()))
                            .send(player)
            );
        }
    }

    @Override
    public List<String> tab(@NonNull Player player, @NotNull @NonNull String[] args) {
        if(args.length == 1) return List.of("balans");
        return null;
    }
}