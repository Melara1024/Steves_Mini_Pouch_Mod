package ga.melara.stevesminipouch.util;

import net.minecraft.entity.player.PlayerEntity;

public interface ICraftingContainerChangable {
    void setCraft(boolean isActiveCraft, PlayerEntity player);

    boolean isActivateCraft();
}
