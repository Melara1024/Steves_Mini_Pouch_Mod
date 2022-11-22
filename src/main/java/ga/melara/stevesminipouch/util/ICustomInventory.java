package ga.melara.stevesminipouch.util;

import ga.melara.stevesminipouch.stats.PlayerInventorySizeData;
import net.minecraft.world.entity.player.Player;

public interface ICustomInventory {

    void setInventory(Player player, boolean set);
    void toggleInventory(Player player);

    void setArmor(Player player, boolean set);
    void toggleArmor(Player player);

    void setOffhand(Player player, boolean set);
    void toggleOffhand(Player player);

    void setCraft(Player player, boolean set);
    void toggleCraft(Player player);

    boolean isActiveInventory();

    boolean isActiveArmor();

    boolean isActiveOffhand();

    boolean isActiveCraft();

    void setStorageSize(int set, Player player);

    void changeStorageSize(int change, Player player);

    void updateStorageSize();

    void changeEffectSize(int change);

    boolean isValidSlot(int id);

    int getMaxPage();

    int getInventorySize();

    int getHotbarSize();

    PlayerInventorySizeData getAllData();
}
