package pl.nehorowo.wallet.configuration;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.nehorowo.wallet.builder.ItemBuilder;
import pl.nehorowo.wallet.util.TextUtil;

import java.util.Arrays;
import java.util.List;

@Getter
public class Configuration extends OkaeriConfig {

    @Comment("Prefix permissji pluginu, np tools + . + komenda/funkcja - np. tools.rtp")
    private String permissionPrefix = "wallet";
    private String host = "localhost";
    private int port = 3306;
    private String database = "database";
    private String username = "username";
    private String password = "password";
    private boolean ssl = false;
    private String servicesMenuTitle = "&aZakup uslugi!";
    private ItemStack infoItem = new ItemBuilder(Material.BOOK, 1)
            .setName("&fIle mam pieniedzy?")
            .addLore(Arrays.asList(
                    "&8** &7Kliknij na ten przedmiot, aby sprawdzić stan konta!"
            ))
            .build();
    private int infoItemSlot = 4;
}
