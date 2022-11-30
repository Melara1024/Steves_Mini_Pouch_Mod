package ga.melara.stevesminipouch.util;

import ga.melara.stevesminipouch.event.InventorySyncEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public interface IAdditionalDataHandler {

    CompoundTag saveStatus(CompoundTag tag);

    void loadStatus(CompoundTag tag);

    ListTag saveAdditional(ListTag tag);

    void loadAdditional(ListTag tag);

}
