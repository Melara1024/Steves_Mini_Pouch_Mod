package ga.melara.stevesminipouch.util;

import org.spongepowered.asm.mixin.Mutable;


public interface IHasSlotType {

    SlotType type = null;
    int page = 0;


    abstract void setType(SlotType type);

    abstract SlotType getType();

    abstract void setPage(int page);

    abstract int getPage();

    abstract void hide();

    abstract void show();


}
