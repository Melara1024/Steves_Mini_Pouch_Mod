package ga.melara.stevesminipouch.event;

import ga.melara.stevesminipouch.stats.InventoryStatsData;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.UUID;

public class InventorySyncEvent extends Event implements IModBusEvent {
    InventoryStatsData data;
    UUID senderUUID;


    // Event when inventory state is synchronized to the client.
    public InventorySyncEvent(UUID uuid, InventoryStatsData data) {
        this.senderUUID = uuid;
        this.data = data;
    }

    public InventoryStatsData getData() {
        return this.data;
    }

    public UUID getUUID(){return this.senderUUID;}
}
