package ga.melara.stevesminipouch.util;

import net.minecraft.world.entity.player.Player;

public interface ICraftingContainerChangable {
    void setCraft(boolean isActiveCraft, Player player);

    boolean isActivateCraft();
}
