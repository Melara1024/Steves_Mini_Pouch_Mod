package ga.melara.stevesminipouch.util;

import ga.melara.stevesminipouch.stats.PlayerInventorySizeData;
import net.minecraft.world.entity.player.Player;

public interface IStorageChangable {

    void setInventory(Player player, boolean set);
    abstract void toggleInventory(Player player);

    void setArmor(Player player, boolean set);
    abstract void toggleArmor(Player player);

    void setOffhand(Player player, boolean set);
    abstract void toggleOffhand(Player player);

    void setCraft(Player player, boolean set);
    abstract void toggleCraft(Player player);

    abstract boolean isActiveInventory();

    abstract boolean isActiveArmor();

    abstract boolean isActiveOffhand();

    abstract boolean isActiveCraft();

    void setStorageSize(int set, Player player);

    abstract void changeStorageSize(int change, Player player);

    abstract void updateStorageSize();

    abstract void changeEffectSize(int change);

    abstract boolean isValidSlot(int id);

    abstract int getMaxPage();

    abstract int getInventorySize();

    abstract int getHotbarSize();


    PlayerInventorySizeData getAllData();
}
