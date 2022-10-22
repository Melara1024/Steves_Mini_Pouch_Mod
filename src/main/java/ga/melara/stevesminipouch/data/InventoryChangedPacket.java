package ga.melara.stevesminipouch.data;

import ga.melara.stevesminipouch.event.InventoryChangeEvent;
import ga.melara.stevesminipouch.event.PageChangeEvent;
import ga.melara.stevesminipouch.util.InventoryEffect;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class InventoryChangedPacket {
    private int inventoryEffect;

    private int change = 0;
    private UUID senderUUID;


    public InventoryChangedPacket(InventoryEffect inventoryEffect, UUID senderUUID) {
        this.inventoryEffect = InventoryEffect.getByType(inventoryEffect);
        this.senderUUID = senderUUID;
    }

    public InventoryChangedPacket(InventoryEffect inventoryEffect, int change, UUID senderUUID) {
        this.inventoryEffect = InventoryEffect.getByType(inventoryEffect);
        this.change = change;
        this.senderUUID = senderUUID;
    }

    public InventoryChangedPacket(FriendlyByteBuf buf) {
        this.inventoryEffect = buf.readInt();
        this.change = buf.readInt();
        this.senderUUID = buf.readUUID();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.inventoryEffect);
        buf.writeInt(this.change);
        buf.writeUUID(this.senderUUID);
    }

    //こいつ自身はクライアントのクラス
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            //InventoryEffect.getById(inventoryEffect).apply(Minecraft.getInstance().player);
            //クライアント側プレイヤークラスにアクセスする必要がある
            //ただしMinecraft.getInstance()は使えない
            //イベントを飛ばしてプレイヤーを保持するクラスから実行する
            MinecraftForge.EVENT_BUS.post(new InventoryChangeEvent(inventoryEffect, change));
            System.out.println("direction is "+ ctx.getDirection());
            ctx.setPacketHandled(true);
        });
        return true;
    }
}
