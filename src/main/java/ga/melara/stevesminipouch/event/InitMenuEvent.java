package ga.melara.stevesminipouch.event;


import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.eventbus.api.Event;

public class InitMenuEvent extends Event {

    // Event when AbstractContainerMenu initialized.
    private Container menu;

    public InitMenuEvent(Container menu)
    {
        this.menu = menu;
    }

    public Container getMenu()
    {
        return this.menu;
    }
}
