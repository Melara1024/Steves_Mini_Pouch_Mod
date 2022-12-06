package ga.melara.stevesminipouch.stats;

import ga.melara.stevesminipouch.util.InventorySync;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class EffectSlotSyncPacket {

    private final int effectSlot;

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
            InventorySync.syncEffectSizeToClient(effectSlot);
            ctx.setPacketHandled(true);
        });
        return true;
    }
}
