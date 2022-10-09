package ga.melara.stevesminipouch.data;

import ga.melara.stevesminipouch.util.PageChangeEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PageChangedPacket {

    //todo omosiro
    private int page;

    public PageChangedPacket(int page) {
        this.page = page;
    }

    public PageChangedPacket(FriendlyByteBuf buf) {
        this.page = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.page);
    }

    //こいつ自身はサーバーのクラス
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            //サーバーのプレイヤーを手に入れてインベントリのページ変数を変更
            //ctx.getSender().inventoryMenu.
            System.out.println("received value is" + page);
            MinecraftForge.EVENT_BUS.post(new PageChangeEvent(page));
//            System.out.println(ctx.getSender().getName());
//            System.out.println(ctx.getDirection().toString());
            ctx.setPacketHandled(true);
        });
        return true;
    }

}
