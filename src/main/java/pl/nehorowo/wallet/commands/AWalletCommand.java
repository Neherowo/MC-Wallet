package pl.nehorowo.wallet.commands;

import com.google.common.collect.ImmutableMultimap;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.nehorowo.wallet.WalletPlugin;
import pl.nehorowo.wallet.commands.api.CommandAPI;
import pl.nehorowo.wallet.controller.UserController;
import pl.nehorowo.wallet.service.UserService;
import pl.nehorowo.wallet.util.NumberUtil;

import java.util.List;

public class AWalletCommand extends CommandAPI {

    public AWalletCommand() {
        super(
                "awallet",
                "wallet.wallet",
                "Komenda do zarzadzania portfelem graczy.",
                "/awallet <add/remove/set> <player> <amount>",
                List.of("aportfel")
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length <= 1) {
            sendUsage((Player) sender);
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if(target == null) {
            getMessageConfiguration()
                    .getIncorrectPlayer()
                    .send((Player) sender);
            return;
        }

        if(!NumberUtil.isInteger((Player) sender, args[2])) return;
        int amount = Integer.parseInt(args[2]);

        switch (args[0].toLowerCase()) {
            case "add", "dodaj" -> {
                UserService.getInstance().get(target.getUniqueId()).ifPresent(user -> {
                    user.setMoney(user.getMoney() + amount);
                    new UserController(target.getUniqueId()).update();
                });

                getMessageConfiguration()
                        .getAddedMoney()
                        .addPlaceholder(ImmutableMultimap.of(
                                "[AMOUNT]", amount
                        ))
                        .send(target);

                getMessageConfiguration()
                        .getAddedMoneyAdmin()
                        .addPlaceholder(ImmutableMultimap.of(
                                "[AMOUNT]", amount,
                                "[PLAYER]", target.getName()
                        ))
                        .send((Player) sender);
            }
            case "remove", "usun" -> {
                UserService.getInstance().get(target.getUniqueId()).ifPresent(user -> {
                    user.setMoney(user.getMoney() - amount);
                    new UserController(target.getUniqueId()).update();
                });

                getMessageConfiguration()
                        .getRemoveMoney()
                        .addPlaceholder(ImmutableMultimap.of(
                                "[AMOUNT]", amount
                        ))
                        .send(target);

                getMessageConfiguration()
                        .getRemoveMoneyAdmin()
                        .addPlaceholder(ImmutableMultimap.of(
                                "[AMOUNT]", amount,
                                "[PLAYER]", target.getName()
                        ))
                        .send((Player) sender);
            }

            case "set", "ustaw" -> {
                UserService.getInstance().get(target.getUniqueId()).ifPresent(user -> {
                    user.setMoney(amount);
                    new UserController(target.getUniqueId()).update();
                });

                getMessageConfiguration()
                        .getSetMoney()
                        .addPlaceholder(ImmutableMultimap.of(
                                "[AMOUNT]", amount
                        ))
                        .send(target);

                getMessageConfiguration()
                        .getSetMoneyAdmin()
                        .addPlaceholder(ImmutableMultimap.of(
                                "[AMOUNT]", amount,
                                "[PLAYER]", target.getName()
                        ))
                        .send((Player) sender);
            }

            case "reload" -> {
                getMessageConfiguration()
                        .getReloaded()
                        .send((Player) sender);
                getConfiguration().load();
                getMessageConfiguration().load();
                getServiceItemsConfiguration().load();
            }
        }
    }

    @Override
    public List<String> tab(@NonNull Player player, @NotNull @NonNull String[] args) {
        if(args.length == 1) return List.of("add", "remove", "set");
        if(args.length == 2) return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        if(args.length == 3) return List.of("liczba");
        else return null;
    }
}
