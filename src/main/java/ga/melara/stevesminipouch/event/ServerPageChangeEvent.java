package ga.melara.stevesminipouch.event;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;

public class ServerPageChangeEvent extends Event implements IModBusEvent {
    private final int page;

    // Event when the page change button is pressed.
    public ServerPageChangeEvent(int page) {
        this.page = page;
    }

    public int getPage() {
        return this.page;
    }
}
