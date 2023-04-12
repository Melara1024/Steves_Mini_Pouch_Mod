package ga.melara.stevesminipouch.event;

import ga.melara.stevesminipouch.stats.InventoryStatsData;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

public class InventorySyncEvent extends Event implements IModBusEvent {
    InventoryStatsData data;

    // Event when inventory state is synchronized to the client.
    public InventorySyncEvent(InventoryStatsData data) {
        this.data = data;
    }

    public InventoryStatsData getData() {
        return this.data;
    }
}
