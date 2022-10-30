package ga.melara.stevesminipouch.data;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class InventorySyncPacket
{

    private boolean isActiveInventory;
    private boolean isActiveOffhand;
    private boolean isActivateCraft;
    private boolean isActivateArmor;
    private int slot;

    public InventorySyncPacket(PlayerInventorySizeData data) {
        this.isActiveInventory = data.isActiveInventory();
        this.isActiveOffhand = data.isActiveOffhand();
        this.isActivateArmor = data.isEquippable();
        this.isActivateCraft = data.isCraftable();
        this.slot = data.getSlot();

        System.out.println("inventorySyncPacket init");
    }

    public InventorySyncPacket(FriendlyByteBuf buf) {
        isActiveInventory = buf.readBoolean();
        isActivateArmor = buf.readBoolean();
        isActiveOffhand = buf.readBoolean();
        isActivateCraft = buf.readBoolean();
        slot = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(isActiveInventory);
        buf.writeBoolean(isActivateArmor);
        buf.writeBoolean(isActiveOffhand);
        buf.writeBoolean(isActivateCraft);
        buf.writeInt(slot);
    }

    //こいつ自身はサーバーのクラス
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {

            //どうやらhandleできていない
            System.out.println("handled packet");

            System.out.println(slot);
            System.out.println(isActiveInventory);
            System.out.println(isActivateArmor);
            System.out.println(isActiveOffhand);
            System.out.println(isActivateCraft);

            ClientInventoryData.set(slot, isActiveInventory, isActivateArmor, isActiveOffhand, isActivateCraft);

            //ここからイベントを送信して初期化？

            MinecraftForge.EVENT_BUS.post(new InventorySyncEvent());

            ctx.setPacketHandled(true);
        });
        return true;
    }

}
