package pl.nehorowo.wallet.util;

import fr.minuskube.inv.content.SlotPos;

public class SlotUtil {

    public static SlotPos calcSpace(int slot) {
        int row = slot / 9;
        int column = slot - (row * 9);
        return new SlotPos(row, column);
    }
}
