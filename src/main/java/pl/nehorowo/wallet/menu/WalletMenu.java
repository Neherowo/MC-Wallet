package pl.nehorowo.wallet.menu;

import com.google.common.collect.ImmutableMultimap;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.nehorowo.wallet.WalletPlugin;
import pl.nehorowo.wallet.service.FavorService;
import pl.nehorowo.wallet.service.UserService;
import pl.nehorowo.wallet.util.TextUtil;
import pl.nehorowo.wallet.util.SlotUtil;

public class WalletMenu implements InventoryProvider {


    @Override
    public void init(Player player, InventoryContents contents) {
        FavorService.getInstance().getServiceSet().forEach(service -> {
            contents.set(SlotUtil.calcSpace(service.getSlot()), ClickableItem.of(service.getItem(), false, event -> {
                UserService.getInstance().get(player.getUniqueId()).ifPresent(user -> {
                    if(user.getMoney() >= service.getPrice()) {
                        user.setMoney(user.getMoney() - service.getPrice());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), service.getCommand()
                                .replace("[PLAYER]", player.getName())
                        );

                        player.closeInventory();
                    } else {
                        WalletPlugin.getInstance().getMessageConfiguration()
                                .getNotEnoughtMoney()
                                .send(player);
                        player.closeInventory();
                    }
                });
            }));

            contents.set(
                    SlotUtil.calcSpace(WalletPlugin.getInstance().getConfiguration().getInfoItemSlot()),
                    ClickableItem.of(WalletPlugin.getInstance().getConfiguration().getInfoItem(), false, event -> {
                        player.closeInventory();
                        WalletPlugin.getInstance().getMessageConfiguration()
                                .getYouHave()
                                .addPlaceholder(ImmutableMultimap.of("[MONEY]", UserService.getInstance().get(player.getUniqueId()).get().getMoney()))
                                .send(player);
                    })
            );
        });
    }

    public void openServicesMenu(Player player) {
        SmartInventory.builder()
                .provider(this)
                .title(TextUtil.fixColor(WalletPlugin.getInstance().getConfiguration().getServicesMenuTitle()))
                .build()
                .open(player);
    }
}
