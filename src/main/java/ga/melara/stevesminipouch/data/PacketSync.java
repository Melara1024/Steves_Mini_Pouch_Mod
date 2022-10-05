package ga.melara.stevesminipouch.data;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSync {

    //todo omosiro
    private int slot;
    private boolean isActiveInventory;
    private boolean isActiveOffhand;
    private boolean isCraftable;
    private boolean isEquippable;

    public PacketSync(PlayerInventorySizeData data) {
        this.slot = data.getSlot();
        this.isActiveInventory = data.isActiveInventory();
        this.isActiveOffhand = data.isActiveOffhand();
        this.isCraftable = data.isCraftable();
        this.isEquippable = data.isEquippable();
    }

    public PacketSync(FriendlyByteBuf buf) {
        this.slot = buf.readInt();
        this.isActiveInventory = buf.readBoolean();
        this.isActiveOffhand = buf.readBoolean();
        this.isCraftable = buf.readBoolean();
        this.isEquippable = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.slot);
        buf.writeBoolean(this.isActiveInventory);
        buf.writeBoolean(this.isActiveOffhand);
        buf.writeBoolean(this.isCraftable);
        buf.writeBoolean(this.isEquippable);
    }

    //こいつ自身はサーバーのクラス
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            //ここでクライアントにデータを送りつける
            ClientInventoryData.set(
                    this.slot,
                    this.isActiveInventory,
                    this.isActiveOffhand,
                    this.isCraftable,
                    this.isEquippable
            );
        });
        return true;
    }

}
