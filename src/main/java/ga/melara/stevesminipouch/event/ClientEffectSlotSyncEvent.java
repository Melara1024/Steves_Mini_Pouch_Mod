package ga.melara.stevesminipouch.event;

import net.minecraftforge.eventbus.api.Event;

public class ClientEffectSlotSyncEvent extends Event {
    private final int size;

    // Event when slots are added by slot effect.
    public ClientEffectSlotSyncEvent(int increment) {
        this.size = increment;
    }

    public int getEffectSize() {
        return this.size;
    }
}
