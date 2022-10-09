package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.util.IHasSlotPage;
import ga.melara.stevesminipouch.util.IHasSlotType;
import ga.melara.stevesminipouch.util.IStorageChangable;
import ga.melara.stevesminipouch.util.SlotType;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public class SlotMixin implements IHasSlotType, IHasSlotPage {
    /*
    Todo スロットのアクティブ状態を変更できるようにする，新変数closedSlotを追加，これによって閉じられたスロットを表現する

    Todo ページを受け取ってinventoryからの参照を変更できるようにする(これはSlotType.INVENTORYのときのみ適用されるように実装)
    Todo ページの数の分だけインベントリのitemsのインデックスをシフトする，もしもitemsに参照できない場合はスロットを閉じる用に設定

    Todo Set Get Removeの３つについてページからたどれるように
    スロットの変更点
    インベントリから取得するアイテムをページ変数によってシフトする
    プライベート変数slotによってインベントリのitemsリストとの関連付けがされている
    Slotクラスはアイテムそのものを保持しない
    未使用スロットには未使用アイテムを挿入，maypickupのオーバーライドでクリックとピックアップの阻止
    カーソルのホバーに関してはレンダラーで対応可能
     */

    public SlotType type = SlotType.OTHER;
    public int page = 0;

    @Final
    @Shadow
    @Mutable
    private int slot;

    @Final
    @Shadow
    @Mutable
    public Container container;

    @Shadow
    public int index;

    @Shadow
    public int x;

    @Shadow
    public int y;

    @Shadow
    public void setChanged() {}

    private boolean isHiding = false;


    @Override
    public void setType(SlotType type)
    {
        this.type = type;
    }

    @Override
    public SlotType getType()
    {
        return this.type;
    }

    @Override
    public void setPage(int page)
    {
        this.page = page;
    }

    @Override
    public int getPage()
    {
        return this.page;
    }


    @Inject(method = "initialize(Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    public void onInitialize(ItemStack p_40240_, CallbackInfo ci)
    {
        if(this.type == SlotType.INVENTORY && page>0)
        {
            if(this.slot + 27*page < ((IStorageChangable)container).getSize())
            {
                System.out.println("set item to " + (this.slot + 27*page + 5) + " name " + p_40240_);
                this.container.setItem(this.slot + 27*page + 5, p_40240_);
                this.setChanged();
                ci.cancel();
            }
        }
    }

    @Inject(method = "set(Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    public void onSetItem(ItemStack p_40240_, CallbackInfo ci)
    {
        if(this.type == SlotType.INVENTORY && page>0)
        {
            if(this.slot + 27*page < ((IStorageChangable)container).getSize())
            {
                this.container.setItem(this.slot + 27*page + 5, p_40240_);
                this.setChanged();
                ci.cancel();
            }
        }
    }

    @Inject(method = "getItem()Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    public void onGetItem(CallbackInfoReturnable<ItemStack> cir)
    {
        if(this.type == SlotType.INVENTORY && page>0)
        {
            if(this.slot + 27*page < ((IStorageChangable)container).getSize())
            {
                System.out.println("set item to " +  (this.slot + 27*page + 5) + " name " + this.container.getItem(this.slot + 27*page + 5));
                cir.setReturnValue(this.container.getItem(this.slot + 27*page + 5));
            }
        }
    }

    @Inject(method = "remove(I)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    public void onRemoveItem(int p_40227_, CallbackInfoReturnable<ItemStack> cir)
    {
        if(this.type == SlotType.INVENTORY)
        {
            if(this.slot + 27*page < ((IStorageChangable)container).getSize())
            {
                cir.setReturnValue(this.container.removeItem(this.slot + 27*page, p_40227_));
            }
        }
    }

}
