package ga.melara.stevesminipouch.stats;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

public class InventorySyncEvent extends Event implements IModBusEvent {


    PlayerInventorySizeData data;

    public InventorySyncEvent(PlayerInventorySizeData data) {
        this.data = data;
    }

    public PlayerInventorySizeData getData() {
        return this.data;
    }
}
