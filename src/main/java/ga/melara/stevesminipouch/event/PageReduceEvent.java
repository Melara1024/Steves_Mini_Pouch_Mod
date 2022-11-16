package ga.melara.stevesminipouch.event;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

public class PageReduceEvent extends Event implements IModBusEvent {
    int page = 0;

    public PageReduceEvent(int p) {
        this.page = p;
        System.out.println("event fired");
    }

    public int getPage() {
        //Typeからキーを使ってプロパティマップから該当リストを入手，switchとかはしない(いちいち追加が必要になってしまう)
        return this.page;
    }
}

