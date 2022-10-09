package ga.melara.stevesminipouch.util;

import org.spongepowered.asm.mixin.Mutable;


public interface IHasSlotType {

    abstract void setType(SlotType type);

    abstract SlotType getType();


}
