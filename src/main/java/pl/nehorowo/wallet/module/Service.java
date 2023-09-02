package pl.nehorowo.wallet.module;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Getter@Setter
public class Service {

    private ItemStack item;
    private int slot;
    private int price;
    private String command;

    public Service(ItemStack item, int slot, int price, String command) {
        this.item = item;
        this.slot = slot;
        this.price = price;
        this.command = command;
    }
}
