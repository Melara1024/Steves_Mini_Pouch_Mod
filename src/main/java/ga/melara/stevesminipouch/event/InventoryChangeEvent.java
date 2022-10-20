package ga.melara.stevesminipouch.event;

import net.minecraft.client.Minecraft;
import net.minecraftforge.eventbus.api.Event;

public class InventoryChangeEvent extends Event {
    int inventoryEffect = 0;
    int change = 0;

    public InventoryChangeEvent(int p){
        this.inventoryEffect = p;
    }

    public InventoryChangeEvent(int p, int c){
        this.inventoryEffect = p;
        this.change = c;
    }

    public int getInventoryEffect()
    {
        return this.inventoryEffect;
    }

    public int getSlotChange()
    {
        return this.change;
    }
}
