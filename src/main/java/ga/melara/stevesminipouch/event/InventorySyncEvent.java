package ga.melara.stevesminipouch.event;

import ga.melara.stevesminipouch.stats.PlayerInventorySizeData;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;

public class InventorySyncEvent extends Event implements IModBusEvent {
    PlayerInventorySizeData data;

    // Event when inventory state is synchronized to the client.
    public InventorySyncEvent(PlayerInventorySizeData data) {
        this.data = data;
    }

    public PlayerInventorySizeData getData() {
        return this.data;
    }
}
