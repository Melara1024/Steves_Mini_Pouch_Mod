package ga.melara.stevesminipouch.event;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.UUID;

public class ServerPageChangeEvent extends Event implements IModBusEvent {
    private final int page;
    private final UUID uuid;

    // Event when the page change button is pressed.
    public ServerPageChangeEvent(UUID uuid, int page) {
        this.page = page;
        this.uuid = uuid;
    }

    public int getPage() {
        return this.page;
    }

    public UUID getUUID(){
        return this.uuid;
    }
}
