package ga.melara.stevesminipouch.stats;

import ga.melara.stevesminipouch.event.InventorySyncEvent;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class InventorySyncPacket {

    PlayerInventorySizeData data;

    public InventorySyncPacket(PlayerInventorySizeData data) {
        this.data = data;
    }

    public InventorySyncPacket(PacketBuffer buf) {
        boolean isActivateInventory = buf.readBoolean();
        boolean isActivateArmor = buf.readBoolean();
        boolean isActiveOffhand = buf.readBoolean();
        boolean isActivateCraft = buf.readBoolean();
        int slot = buf.readInt();
        int effectSlot = buf.readInt();

        this.data = new PlayerInventorySizeData(slot, effectSlot, isActivateInventory, isActivateArmor, isActiveOffhand, isActivateCraft);
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeBoolean(this.data.isActiveInventory());
        buf.writeBoolean(this.data.isActiveArmor());
        buf.writeBoolean(this.data.isActiveOffhand());
        buf.writeBoolean(this.data.isActiveCraft());
        buf.writeInt(this.data.getInventorySize());
        buf.writeInt(this.data.getEffectSize());
    }

    //こいつ自身はサーバーのクラス
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            MinecraftForge.EVENT_BUS.post(new InventorySyncEvent(this.data));
            ctx.setPacketHandled(true);
        });
        return true;
    }

}
