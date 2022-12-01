package ga.melara.stevesminipouch.stats;

import ga.melara.stevesminipouch.event.InventorySyncEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class InventorySyncPacket {

    InventoryStatsData data;

    UUID senderUUID;

    public InventorySyncPacket(InventoryStatsData data, UUID uuid) {
        this.data = data;
        this.senderUUID = uuid;
    }

    public InventorySyncPacket(FriendlyByteBuf buf) {
        boolean isActivateInventory = buf.readBoolean();
        boolean isActivateArmor = buf.readBoolean();
        boolean isActiveOffhand = buf.readBoolean();
        boolean isActivateCraft = buf.readBoolean();
        int slot = buf.readInt();
        int effectSlot = buf.readInt();

        this.data = new InventoryStatsData(slot, effectSlot, isActivateInventory, isActivateArmor, isActiveOffhand, isActivateCraft);

        this.senderUUID = buf.readUUID();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(this.data.isActiveInventory());
        buf.writeBoolean(this.data.isActiveArmor());
        buf.writeBoolean(this.data.isActiveOffhand());
        buf.writeBoolean(this.data.isActiveCraft());
        buf.writeInt(this.data.getInventorySize());
        buf.writeInt(this.data.getEffectSize());

        buf.writeUUID(this.senderUUID);
    }

    //こいつ自身はサーバーのクラス
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            System.out.println("packet handled");
            MinecraftForge.EVENT_BUS.post(new InventorySyncEvent(senderUUID, this.data));
            ctx.setPacketHandled(true);
        });
        return true;
    }

}
