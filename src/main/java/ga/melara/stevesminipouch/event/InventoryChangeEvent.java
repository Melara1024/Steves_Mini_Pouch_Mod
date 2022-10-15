package ga.melara.stevesminipouch.event;

import net.minecraft.client.Minecraft;
import net.minecraftforge.eventbus.api.Event;

public class InventoryChangeEvent extends Event {
    int inventoryEffect = 0;

    public InventoryChangeEvent(int p){
        this.inventoryEffect = p;
    }

    public int getInventoryEffect()
    {
        return this.inventoryEffect;
    }
}
