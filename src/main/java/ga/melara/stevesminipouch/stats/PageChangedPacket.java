package ga.melara.stevesminipouch.stats;

import ga.melara.stevesminipouch.event.ServerPageChangeEvent;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PageChangedPacket {

    private final int page;

    public PageChangedPacket(int page) {
        this.page = page;
    }

    public PageChangedPacket(PacketBuffer buf) {
        this.page = buf.readInt();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(this.page);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            MinecraftForge.EVENT_BUS.post(new ServerPageChangeEvent(page));

            ctx.setPacketHandled(true);
        });
        return true;
    }

}
