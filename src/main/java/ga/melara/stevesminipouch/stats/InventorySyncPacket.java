package ga.melara.stevesminipouch.stats;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class InventorySyncPacket {

    PlayerInventorySizeData data;

    public InventorySyncPacket(PlayerInventorySizeData data) {
        this.data = data;

        System.out.println("inventorySyncPacket init");
    }

    public InventorySyncPacket(FriendlyByteBuf buf) {
        boolean isActivateInventory = buf.readBoolean();
        boolean isActivateArmor = buf.readBoolean();
        boolean isActiveOffhand = buf.readBoolean();
        boolean isActivateCraft = buf.readBoolean();
        int slot = buf.readInt();
        int effectSlot = buf.readInt();

        this.data = new PlayerInventorySizeData(slot, effectSlot, isActivateInventory, isActiveOffhand, isActivateCraft, isActivateArmor);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(this.data.isActiveInventory());
        buf.writeBoolean(this.data.isEquippable());
        buf.writeBoolean(this.data.isActiveOffhand());
        buf.writeBoolean(this.data.isCraftable());
        buf.writeInt(this.data.getSlot());
        buf.writeInt(this.data.getEffectSlot());
    }

    //こいつ自身はサーバーのクラス
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {


            //ここからイベントを送信して初期化？

            MinecraftForge.EVENT_BUS.post(new InventorySyncEvent(this.data));

            ctx.setPacketHandled(true);
        });
        return true;
    }

}
