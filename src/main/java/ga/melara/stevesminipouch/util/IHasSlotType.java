package ga.melara.stevesminipouch.util;

import org.spongepowered.asm.mixin.Mutable;


public interface IHasSlotType {

    void setType(SlotType type);

    SlotType getType();
}
