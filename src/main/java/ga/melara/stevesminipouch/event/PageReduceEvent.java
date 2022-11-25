package ga.melara.stevesminipouch.event;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;

public class PageReduceEvent extends Event implements IModBusEvent {
    private final int page;

    // Event when the page players are currently looking at is no longer needed
    // due to a decrease in the number of slots.
    public PageReduceEvent(int page) {
        this.page = page;
    }

    public int getPage() {
        return this.page;
    }
}

