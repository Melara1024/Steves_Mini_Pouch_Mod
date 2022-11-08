package ga.melara.stevesminipouch.event;

import net.minecraftforge.eventbus.api.Event;

public class EffectSlotSyncEvent extends Event {

    int size = 0;

    public EffectSlotSyncEvent(int setSize)
    {
        this.size = setSize;
    }

    public int getEffectSize()
    {
        return this.size;
    }
}
