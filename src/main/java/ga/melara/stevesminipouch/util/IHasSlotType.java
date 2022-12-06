package ga.melara.stevesminipouch.util;

import org.spongepowered.asm.mixin.Mutable;

import java.util.UUID;


public interface IHasSlotType {

    void setType(SlotType type);

    SlotType getType();

    UUID getOwner();
}
