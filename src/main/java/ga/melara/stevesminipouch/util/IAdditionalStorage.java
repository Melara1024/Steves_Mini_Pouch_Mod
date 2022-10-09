package ga.melara.stevesminipouch.util;

import net.minecraft.nbt.ListTag;

public interface IAdditionalStorage {

    abstract ListTag saveAdditional(ListTag tag);

    abstract void loadAdditional(ListTag tag);
}
