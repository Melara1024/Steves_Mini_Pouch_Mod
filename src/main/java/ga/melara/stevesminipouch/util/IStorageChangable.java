package ga.melara.stevesminipouch.util;

import ga.melara.stevesminipouch.stats.PlayerInventorySizeData;
import net.minecraft.world.entity.player.Player;

public interface IStorageChangable {
    abstract void toggleInventory(Player player);

    abstract void toggleArmor(Player player);

    abstract void toggleOffhand(Player player);

    abstract void toggleCraft(Player player);

    abstract boolean isActiveInventory();

    abstract boolean isActiveArmor();

    abstract boolean isActiveOffhand();

    abstract boolean isActiveCraft();

    abstract void changeStorageSize(int change, Player player);

    abstract void updateStorageSize();

    abstract void changeEffectSize(int change);

    abstract boolean isValidSlot(int id);

    abstract int getMaxPage();

    abstract int getInventorySize();

    abstract int getHotbarSize();


    PlayerInventorySizeData getAllData();
}
