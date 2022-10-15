package ga.melara.stevesminipouch.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

public class PageChangeEvent extends Event implements IModBusEvent {
    int page = 0;

    public PageChangeEvent(int p){
        this.page = p;
    }

    public int getPage()
    {
        //Typeからキーを使ってプロパティマップから該当リストを入手，switchとかはしない(いちいち追加が必要になってしまう)
        return this.page;
    }
}
