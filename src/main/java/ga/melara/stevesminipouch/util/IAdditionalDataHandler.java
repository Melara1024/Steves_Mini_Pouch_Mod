package ga.melara.stevesminipouch.util;

import ga.melara.stevesminipouch.event.InventorySyncEvent;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public interface IAdditionalDataHandler {

    CompoundNBT saveStatus(CompoundNBT tag);

    void loadStatus(CompoundNBT tag);

    ListNBT saveAdditional(ListNBT tag);

    void loadAdditional(ListNBT tag);

    void initClient(InventorySyncEvent e);
}
