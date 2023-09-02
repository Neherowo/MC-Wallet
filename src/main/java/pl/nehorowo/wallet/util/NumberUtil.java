package pl.nehorowo.wallet.util;

import org.bukkit.entity.Player;
import pl.nehorowo.wallet.WalletPlugin;

public class NumberUtil {

    public static boolean isInteger(Player player, String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            WalletPlugin.getInstance().getMessageConfiguration()
                    .getWrongAmount()
                    .send(player);
            return false;
        }
    }
}
