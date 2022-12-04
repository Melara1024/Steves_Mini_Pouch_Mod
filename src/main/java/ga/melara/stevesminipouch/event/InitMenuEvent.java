package ga.melara.stevesminipouch.event;


import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.eventbus.api.Event;

public class InitMenuEvent extends Event {

    // Event when AbstractContainerMenu initialized.
    private AbstractContainerMenu menu;

    public InitMenuEvent(AbstractContainerMenu menu) {
        this.menu = menu;
    }

    public AbstractContainerMenu getMenu() {
        return this.menu;
    }
}
