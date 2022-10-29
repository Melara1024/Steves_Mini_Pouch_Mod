package ga.melara.stevesminipouch.data;

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public interface StatsSynchronizer
{
    void sendInitialData(PlayerInventorySizeData data);
}
