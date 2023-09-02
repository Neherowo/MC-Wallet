package pl.nehorowo.wallet.commands.api;

import com.google.common.collect.ImmutableMultimap;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.nehorowo.wallet.WalletPlugin;
import pl.nehorowo.wallet.configuration.Configuration;
import pl.nehorowo.wallet.configuration.MessageConfiguration;
import pl.nehorowo.wallet.configuration.ServiceItemsConfiguration;
import pl.nehorowo.wallet.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class CommandAPI extends BukkitCommand {
    private final String permission;

    private final WalletPlugin instance = WalletPlugin.getInstance();
    private final Configuration configuration = instance.getConfiguration();
    private final MessageConfiguration messageConfiguration = instance.getMessageConfiguration();
    private final ServiceItemsConfiguration serviceItemsConfiguration = instance.getItemsConfiguration();

    @SneakyThrows
    public CommandAPI(@NotNull String name, String permission, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
        super(name, description, usageMessage, aliases);
        this.permission = permission;
        this.setPermission(permission);
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        if(permission == null || permission.isEmpty() || commandSender.hasPermission(permission)) {
            try {
                execute(commandSender, strings);
            } catch (Exception e) {
                var message = e.getMessage();

                if(commandSender.hasPermission(getConfiguration().getPermissionPrefix() + ".cmd.admin")) TextUtil.sendMessage(commandSender, "&cWystąpił błąd poczas wykonywania polecenia! Błąd: &4" + message);
                else TextUtil.sendMessage(commandSender, "&cWystąpił błąd poczas wykonywania polecenia!");
                Bukkit.getLogger().warning("Gracz " + commandSender.getName() + " wykonal polecenie " + s + ". Przy wykonywaniu jej wystąpil blad: " + message);
                e.printStackTrace();
            return false;
        }
        } else {
            messageConfiguration
                    .getNoPermission()
                    .send((Player) commandSender);
        }
        return false;
    }

    @Override
    public @NonNull List<String> tabComplete(@NonNull CommandSender sender, @NonNull String label, @NotNull String[] args) {
        List<String> completions = tab((Player) sender, args);
        if (completions == null) return new ArrayList<>();
        else return completions;
    }

    public void sendUsage(Player player) {
        messageConfiguration.getCorrectUsage()
                .addPlaceholder(ImmutableMultimap.of("[USAGE]", getUsage()))
                .send(player);
    }

    public abstract void execute(CommandSender sender, String[] args);
    public abstract List<String> tab(@NonNull Player player, @NonNull String[] args);

}