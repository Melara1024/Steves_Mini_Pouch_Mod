package ga.melara.stevesminipouch.event;

import ga.melara.stevesminipouch.stats.InventoryStatsData;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

public class InventorySyncEvent extends Event implements IModBusEvent {
    InventoryStatsData data;
    Player player;

    // Event when inventory state is synchronized to the client.
    public InventorySyncEvent(Player player, InventoryStatsData data) {
        this.player = player;
        this.data = data;
    }

    public InventoryStatsData getData() {
        return this.data;
    }

    public Player getPlayer(){return this.player;}
}
