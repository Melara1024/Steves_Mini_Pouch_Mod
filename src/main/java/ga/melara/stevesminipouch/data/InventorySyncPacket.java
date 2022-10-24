package ga.melara.stevesminipouch.data;

import ga.melara.stevesminipouch.event.PageChangeEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class InventorySyncPacket
{

    private boolean isActiveInventory;
    private boolean isActiveOffhand;
    private boolean isCraftable;
    private boolean isEquippable;
    private int slot;

    public InventorySyncPacket(PlayerInventorySizeData data) {
        this.isActiveInventory = data.isActiveInventory();
        this.isActiveOffhand = data.isActiveOffhand();
        this.isEquippable = data.isEquippable();
        this.isCraftable = data.isCraftable();
        this.slot = data.getSlot();
    }

    public InventorySyncPacket(FriendlyByteBuf buf) {
        isActiveInventory = buf.readBoolean();
        isActiveOffhand = buf.readBoolean();
        isEquippable = buf.readBoolean();
        isCraftable = buf.readBoolean();
        slot = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(isActiveInventory);
        buf.writeBoolean(isActiveOffhand);
        buf.writeBoolean(isEquippable);
        buf.writeBoolean(isCraftable);
        buf.writeInt(slot);
    }

    //こいつ自身はサーバーのクラス
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ClientInventoryData.set(slot, isActiveInventory, isActiveOffhand, isCraftable, isEquippable);
            ctx.setPacketHandled(true);
        });
        return true;
    }
}
