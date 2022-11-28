package ga.melara.stevesminipouch.util;

import ga.melara.stevesminipouch.stats.PlayerInventorySizeData;
import net.minecraft.world.entity.player.Player;

public interface ICustomInventory {

    void initMiniPouch(int inventorySize, int effectSize, boolean isActiveInventory, boolean isActiveArmor, boolean isActiveOffhand, boolean isActiveCraft);

    void setInventory(boolean set);
    void toggleInventory();

    void setArmor(boolean set);
    void toggleArmor();

    void setOffhand(boolean set);
    void toggleOffhand();

    void setCraft(boolean set);
    void toggleCraft();

    boolean isActiveInventory();

    boolean isActiveArmor();

    boolean isActiveOffhand();

    boolean isActiveCraft();

    void setStorageSize(int set);

    void changeStorageSize(int change);

    void updateStorageSize();

    void changeEffectSize(int change);

    boolean isValidSlot(int id);

    int getMaxPage();

    int getInventorySize();

    int getBaseSize();

    int getEffectSize();

    int getEnchantSize();

    int getHotbarSize();

    PlayerInventorySizeData getAllData();
}
