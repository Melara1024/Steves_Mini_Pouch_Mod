package ga.melara.stevesminipouch.util;

import ga.melara.stevesminipouch.stats.InventoryStatsData;
import ga.melara.stevesminipouch.util.ICustomInventory;
import net.minecraft.client.Minecraft;


public class InventorySync {

    public static void initClient(InventoryStatsData data) {
        ((ICustomInventory) Minecraft.getInstance().player.getInventory()). initMiniPouch(data);
    }

    public static void syncEffectSizeToClient(int newEffectSize) {
        // Client-side effect slots are handled here
        ((ICustomInventory) Minecraft.getInstance().player.getInventory()).changeEffectSize(newEffectSize);
    }
}
