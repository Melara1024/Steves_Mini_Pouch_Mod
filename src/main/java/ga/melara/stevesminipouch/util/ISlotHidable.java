package ga.melara.stevesminipouch.util;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public interface ISlotHidable {

    void hide();

    void show();

    boolean isShowing();

    void setHiding();
}
