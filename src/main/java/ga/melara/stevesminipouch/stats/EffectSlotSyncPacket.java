package ga.melara.stevesminipouch.stats;

import ga.melara.stevesminipouch.event.ClientEffectSlotSyncEvent;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class EffectSlotSyncPacket {

    private final int effectSlot;

    public EffectSlotSyncPacket(int setSize) {
        this.effectSlot = setSize;
    }

    public EffectSlotSyncPacket(PacketBuffer buf) {
        effectSlot = buf.readInt();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(effectSlot);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            MinecraftForge.EVENT_BUS.post(new ClientEffectSlotSyncEvent(effectSlot));
            ctx.setPacketHandled(true);
        });
        return true;
    }

}
