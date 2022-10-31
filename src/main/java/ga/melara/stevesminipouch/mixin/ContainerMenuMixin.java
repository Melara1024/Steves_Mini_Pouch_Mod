package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.data.PlayerInventorySizeData;
import ga.melara.stevesminipouch.data.StatsSynchronizer;
import ga.melara.stevesminipouch.event.PageChangeEvent;
import ga.melara.stevesminipouch.util.*;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerMenu.class)
public abstract class ContainerMenuMixin implements IMenuChangable, IMenuSynchronizer {
    /*
    Todo ページが変更されたときのイベント・メッセージを受け取ってページ変数を保持する
    Todo 可能な限りイベントで実装したほうがよい(タイミングに合わせて変更できるので)

    Todo ページ変更イベントに合わせてslots, lastSlots, remoteSlots, dataSlotsなど各変数を一挙に更新する機能を実装
    Todo AbstractContainerMenu内のdoClickで触ったスロットのログを取ろう

    AbstractContainerMenuの変更点
    slotリストには変更なし
    lastslots, remoteslotsリストをページ変更ごとにリアルタイムで更新する必要がある
    MenuインスタンスはScreenインスタンスからmenuを通じて参照可能
     */

    //インベントリが開けないようにする設定
    //Todo クライアント側->Minecraft.setScreenのキャンセル・Tutorial.onOpenInventoryのキャンセル
    //サーバー側->インベントリの開閉ってもしかして鯖側から制限できないかも？
    //まあどうせチェストとかいじったら見えちゃうし気にしない
    //Todo オフハンド開放アイテム以外はインベントリが開放されていないと使えない(consumeされない)ように

    //Todo インベントリ開放・閉鎖を実装，開放時はisActivateInventoryをいじるだけで良い
    //Todo 閉鎖時はスロットを強制的に1に，その他の機能も全て閉鎖する(オフハンドを除く)


    @Shadow
    public NonNullList<Slot> slots;


    //こいつを使ってクライアントにデータを送る
    StatsSynchronizer synchronizer;

    PlayerInventorySizeData data;

    @Override
    public void setStatsSynchronizer(StatsSynchronizer synchronizer)
    {
        this.synchronizer = synchronizer;
        //System.out.println("setStatsSynchronizer called from menu");

        synchronizer.sendInitialData(data);
    }

    public void initMenu(PlayerInventorySizeData data){
        this.data = data;
    }

    @Inject(method = "<init>", at = @At("RETURN"), cancellable = true)
    public void onConstruct(MenuType p_38851_, int p_38852_, CallbackInfo ci)
    {
        MinecraftForge.EVENT_BUS.register(this);
        //System.out.println("menu class init");
    }

    @Inject(method = "initializeContents(ILjava/util/List;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "RETURN"), cancellable = true)
    public void oninitContent(CallbackInfo ci)
    {
        //ここでスロットの初期設定をする？
//        System.out.println("initialize Contents");
//

        System.out.println(Thread.currentThread().getThreadGroup() == SidedThreadGroups.CLIENT ?  "client menu!" : "server menu");

    }

    @Inject(method = "doClick(IILnet/minecraft/world/inventory/ClickType;Lnet/minecraft/world/entity/player/Player;)V", at = @At("RETURN"), cancellable = true)
    public void onClick(int slotId, int p_150432_, ClickType p_150433_, Player p_150434_, CallbackInfo ci)
    {

        if(slotId > 0){Slot s = slots.get(slotId);
            //System.out.println("touched!! " + s.getSlotIndex() + page*27 + " " + s.getItem() + " " + p_150433_);
        }

    }

    @Inject(method = "sendAllDataToRemote", at = @At("RETURN"), cancellable = true)
    public void onSendToRemote(CallbackInfo ci)
    {
        //ここからイベントを発火する？
        //イベントを受け取ってserverplayer側で購読，設定を行う
        //System.out.println("send data");
    }


    @SubscribeEvent
    public void onPageChange(PageChangeEvent e)
    {
        //System.out.println("hello! from abstractcontainermenu! " + e.getPage());
        //送られてきたページ変数が正しいかどうかの処理はスロット側で行う
        //ここでは変な値が送られてきたとしても無視
        for(Slot s : this.slots)
        {
            //スロットに対してページ変更を報告
            ((IHasSlotPage)s).setPage(e.getPage());


            //スロットを再度初期化
            //if(e.getPage() > 0)s.initialize(s.container.getItem(s.getSlotIndex() + e.getPage()*27 + 5));
            //System.out.println(s.getItem().getDisplayName().toString());
        }

        //broadcastFullState();

        //System.out.println("page flipped to " + e.getPage());
    }


    @Override
    public void toggleInventory(Player player){
        for(Slot slot: this.slots)
        {
            //メインハンド以外のすべてのスロットを使用不可にする
            if(slot.getSlotIndex() == 0 && ((IHasSlotType)slot).getType() == SlotType.HOTBAR)
            {
                ((ISlotHidable)slot).show();
            }
            else
            {
                ((ISlotHidable)slot).hide();
            }
        }
        System.out.println("menu Toggled");
    }

    @Override
    public void setArmor(boolean change, Player player)
    {
        if(change != ((IStorageChangable)player.getInventory()).isActiveArmor())toggleArmor(player);
    }

    @Override
    public void toggleArmor(Player player){

        if(!((IStorageChangable)player.getInventory()).isActiveArmor())
        {
            for(Slot slot: this.slots)
            {
                if(((IHasSlotType)slot).getType() == SlotType.ARMOR)
                {
                    ((ISlotHidable)slot).hide();
                }
            }
        }
        else
        {
            for(Slot slot: this.slots)
            {
                if(((IHasSlotType)slot).getType() == SlotType.ARMOR)
                {
                    ((ISlotHidable)slot).show();
                }
            }
        }
    }

    @Override
    public void setCraft(boolean change, Player player)
    {
        if(change != ((IStorageChangable)player.getInventory()).isActiveCraft())toggleCraft(player);
    }

    @Override
    public void toggleCraft(Player player){

        if(!((IStorageChangable)player.getInventory()).isActiveCraft())
        {
            for(Slot slot: this.slots)
            {
                if(((IHasSlotType)slot).getType() == SlotType.CRAFT || ((IHasSlotType)slot).getType() == SlotType.RESULT)
                {
                    ((ISlotHidable)slot).hide();
                }
            }
        }
        else
        {
            for(Slot slot: this.slots)
            {
                if(((IHasSlotType)slot).getType() == SlotType.CRAFT || ((IHasSlotType)slot).getType() == SlotType.RESULT)
                {
                    ((ISlotHidable)slot).show();
                }
            }
        }
    }

    @Override
    public void setOffhand(boolean change, Player player)
    {
        if(change != ((IStorageChangable)player.getInventory()).isActiveOffhand())toggleOffhand(player);
    }

    @Override
    public void toggleOffhand(Player player){

        if(!((IStorageChangable)player.getInventory()).isActiveOffhand())
        {
            for(Slot slot: this.slots)
            {
                if(((IHasSlotType)slot).getType() == SlotType.OFFHAND)
                {
                    ((ISlotHidable)slot).hide();
                }
            }
        }
        else
        {
            for(Slot slot: this.slots)
            {
                if(((IHasSlotType)slot).getType() == SlotType.OFFHAND)
                {
                    ((ISlotHidable)slot).show();
                }
            }
        }
    }

    @Override
    public void changeStorageSize(int change, Player player){
        //itemsリストの大きさで自動的に決まるので36スロット以上のときは何もしない
        if((player.getInventory()).getContainerSize() + change < 36)
        {
            //36スロットより少ないとき，slots内のSlotType.INVENTORYを後ろから無効化

            //……しようかなとおもったけど面倒なのでitemsのlockリストをスロット側から入手する
        }
    }

    @Inject(method = "addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;", at = @At("RETURN"), cancellable = true)
    public void onAddSlot(Slot slot, CallbackInfoReturnable<Slot> cir)
    {
        setType(slot);
    }


    private void setType(Slot targetSlot)
    {
        //System.out.println(targetSlot.container.toString());
        if(targetSlot.container instanceof Inventory)
        {
            if(targetSlot.getSlotIndex() >= 0 && targetSlot.getSlotIndex() < 9)
            {
                ((IHasSlotType)targetSlot).setType(SlotType.HOTBAR);
            }
            if(targetSlot.getSlotIndex() >= 9 && targetSlot.getSlotIndex() < 36)
            {
                ((IHasSlotType)targetSlot).setType(SlotType.INVENTORY);
                ((IHasSlotPage)targetSlot).setPage(0);
            }
            if(targetSlot.getSlotIndex() >= 36 && targetSlot.getSlotIndex() < 40)
            {
                ((IHasSlotType)targetSlot).setType(SlotType.ARMOR);
            }
            if(targetSlot.getSlotIndex() == 40)
            {
                ((IHasSlotType)targetSlot).setType(SlotType.OFFHAND);
            }
        }
        if(targetSlot.container instanceof CraftingContainer)
        {
            if(((CraftingContainer) targetSlot.container).menu instanceof InventoryMenu)
            {
                //2x2クラフティング系のスロットの場合
                ((IHasSlotType)targetSlot).setType(SlotType.CRAFT);
            }
        }
        if(targetSlot instanceof ResultSlot)
        {
            //クラフティングの完成品スロットの場合
            ((IHasSlotType)targetSlot).setType(SlotType.RESULT);
        }



    }
}
