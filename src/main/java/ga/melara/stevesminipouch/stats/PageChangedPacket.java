package ga.melara.stevesminipouch.stats;

import ga.melara.stevesminipouch.event.ServerPageChangeEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PageChangedPacket {


    private int page;

    public PageChangedPacket(int page) {
        this.page = page;
    }

    public PageChangedPacket(FriendlyByteBuf buf) {
        this.page = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.page);
    }

    //こいつ自身はサーバーのクラス
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            MinecraftForge.EVENT_BUS.post(new ServerPageChangeEvent(page));

            ctx.setPacketHandled(true);
        });
        return true;
    }

}
