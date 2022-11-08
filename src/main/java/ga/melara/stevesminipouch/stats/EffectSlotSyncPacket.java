package ga.melara.stevesminipouch.stats;

import ga.melara.stevesminipouch.event.EffectSlotSyncEvent;
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

    //こいつ自身はサーバーのクラス
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {

            ClientInventoryData.setEffectSlot(effectSlot);

            //ここからイベントを送信して初期化？

            MinecraftForge.EVENT_BUS.post(new EffectSlotSyncEvent(effectSlot));

            ctx.setPacketHandled(true);
        });
        return true;
    }

}
