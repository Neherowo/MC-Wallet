package pl.nehorowo.wallet.configuration;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.nehorowo.wallet.builder.ItemBuilder;
import pl.nehorowo.wallet.module.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Getter
public class ServiceItemsConfiguration extends OkaeriConfig {

    private Set<Service> services = Set.of(
            new Service(
                    new ItemBuilder(Material.GOLD_INGOT, 1)
                            .setName("&eVIP")
                            .addLore(Arrays.asList(
                                    " &8** &fKliknij tutaj, aby zakupić rangę &eVIP!",
                                    " &8** &fTa ranga kosztuje &e20 wPLN, które zakupisz w naszym itemshopie!"
                            ))
                            .build(),
                    10,
                    20,
                    "say [PLAYER] zakupił vipa!"
                ),
            new Service(
                    new ItemBuilder(Material.DIAMOND, 1)
                            .setName("&6SVIP")
                            .addLore(Arrays.asList(
                                    " &8** &fKliknij tutaj, aby zakupić rangę &e6SVIP!",
                                    " &8** &fTa ranga kosztuje &e30 wPLN, które zakupisz w naszym itemshopie!"
                            ))
                            .build(),
                    11,
                    30,
                    "say [PLAYER] zakupił svipa!"
            )
        );

}
