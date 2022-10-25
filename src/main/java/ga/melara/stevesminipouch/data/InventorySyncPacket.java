package ga.melara.stevesminipouch.data;

import ga.melara.stevesminipouch.event.PageChangeEvent;
import ga.melara.stevesminipouch.util.IStorageChangable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
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
            //Todo クライアント側プレイヤーにNBT値の適用
            //ctx.getSender().getLevel()

            //保存
            Player player = ctx.getSender();
            LazyOptional<PlayerInventorySizeData> l = player.getCapability(PlayerInventoryProvider.DATA);
            PlayerInventorySizeData p = l.orElse(new PlayerInventorySizeData());


            //最初に初期化を行っていないのでデータがnullだった
            //ログイン後にSyncInventoryStatePacketなどを送りつけてクライアント側にNBTの結果を伝達すべき
            //下のようにすると，インベントリの初期設定しか読み込んでいないのでNBTの保存値を完全に無視している
            p.setActiveInventory(((IStorageChangable) player.getInventory()).isActiveInventory());
            p.setEquippable(((IStorageChangable) player.getInventory()).isActiveArmor());
            p.setActiveOffhand(((IStorageChangable) player.getInventory()).isActiveOffhand());
            p.setCraftable(((IStorageChangable) player.getInventory()).isActiveCraft());
            p.setSlot(((IStorageChangable) player.getInventory()).getInventorySize());

            ClientInventoryData.set(slot, isActiveInventory, isActiveOffhand, isCraftable, isEquippable);
            ctx.setPacketHandled(true);
        });
        return true;
    }
}
