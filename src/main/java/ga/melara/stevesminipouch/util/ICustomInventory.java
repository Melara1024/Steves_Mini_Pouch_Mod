package ga.melara.stevesminipouch.util;

import ga.melara.stevesminipouch.stats.InventoryStatsData;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public interface ICustomInventory {

    void initMiniPouch(InventoryStatsData stats);

    void initServer(InventoryStatsData stats);

    NonNullList<ItemStack> getBackUpPouch();

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

    InventoryStatsData getAllData();
}
