package ga.melara.stevesminipouch.stats;

import ga.melara.stevesminipouch.event.ClientEffectSlotSyncEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EffectSlotSyncPacket {

    private int effectSlot;

    public EffectSlotSyncPacket(int setSize) {
        this.effectSlot = setSize;
    }

    public EffectSlotSyncPacket(FriendlyByteBuf buf) {
        effectSlot = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
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
