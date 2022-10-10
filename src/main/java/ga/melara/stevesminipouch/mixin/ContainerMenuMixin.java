package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.Config;
import ga.melara.stevesminipouch.util.*;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerMenu.class)
public abstract class ContainerMenuMixin{
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

    @Shadow public abstract void slotsChanged(Container p_38868_);

    @Shadow protected abstract Slot addSlot(Slot p_38898_);

    @Shadow public abstract int incrementStateId();

    @Shadow
    public NonNullList<Slot> slots;

    @Shadow
    public NonNullList<ItemStack> remoteSlots;

    @Shadow
    public NonNullList<ItemStack> lastSlots;


    @Shadow
    public void broadcastFullState(){};

    int pageMax = 1;
    int page = 0;

    @Inject(method = "<init>", at = @At("RETURN"), cancellable = true)
    public void onConstruct(MenuType p_38851_, int p_38852_, CallbackInfo ci)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Inject(method = "initializeContents(ILjava/util/List;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "RETURN"), cancellable = true)
    public void oninitContent(CallbackInfo ci)
    {
        System.out.println("initialize Contents");
    }

    @Inject(method = "doClick(IILnet/minecraft/world/inventory/ClickType;Lnet/minecraft/world/entity/player/Player;)V", at = @At("RETURN"), cancellable = true)
    public void onClick(int slotId, int p_150432_, ClickType p_150433_, Player p_150434_, CallbackInfo ci)
    {

        if(slotId > 0){Slot s = slots.get(slotId);
            //System.out.println("touched!! " + s.getSlotIndex() + page*27 + " " + s.getItem() + " " + p_150433_);
        }

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





    //todo  very dirty. must be rewrite.

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
