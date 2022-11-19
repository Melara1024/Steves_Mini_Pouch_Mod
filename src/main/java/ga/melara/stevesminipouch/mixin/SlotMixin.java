package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.util.*;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
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

import java.util.Optional;

@Mixin(Slot.class)
public abstract class SlotMixin implements IHasSlotType, IHasSlotPage, ISlotHidable {
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

    public SlotType type = SlotType.UNDEFINED;


    private int page = 0;

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
    public void setChanged() {
    }

    @Shadow public abstract void initialize(ItemStack p_219997_);

    private boolean isShowing = true;


    @Override
    public void setType(SlotType type) {
        this.type = type;
    }

    @Override
    public SlotType getType() {
        return this.type;
    }

    @Override
    public void setPage(int page) {
        this.page = page;
        if(this.type == SlotType.INVENTORY) {
            if(this.slot + (27 * page) < ((IStorageChangable) container).getInventorySize()) {
                show();
            } else {
                hide();
            }
        }
    }

    @Override
    public int getPage() {
        return this.page;
    }


    @Override
    public void hide() {
        isShowing = false;
    }

    @Override
    public void show() {
        isShowing = true;
    }

    @Override
    public boolean isShowing() {
        return this.isShowing;
    }

    @Inject(method = "<init>", at = @At("RETURN"), cancellable = true)
    public void onInit(Container p_40223_, int p_40224_, int p_40225_, int p_40226_, CallbackInfo ci) {

    }

    @Inject(method = "isActive()Z", at = @At("HEAD"), cancellable = true)
    public void onCallIsActive(CallbackInfoReturnable<Boolean> cir) {
        if(!this.isShowing()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "mayPlace(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    public void onCallMayPlace(CallbackInfoReturnable<Boolean> cir) {
        if(!this.isShowing()) cir.setReturnValue(false);
    }


    @Inject(method = "initialize(Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    public void onInitialize(ItemStack p_40240_, CallbackInfo ci) {

        //todo なんとかしてスロットからインベントリのロック状態を取得
        //containerはinventoryなので，inventorymixin内にクエリ用メソッドを作る？
        //自身のthis.slotの値をつかってisSlotVaridを呼ぶ？
        //ページを捲る前にinitializeが呼ばれてしまっている？
        if(this.type == SlotType.INVENTORY && page > 0) {
            if(this.slot + 27 * page < ((IStorageChangable) container).getInventorySize()) {
                this.show();
                //System.out.println("set item to " + (this.slot + 27*page + 5) + " name " + p_40240_);
                this.container.setItem(this.slot + 27 * page + 5, p_40240_);
                this.setChanged();
                ci.cancel();
            } else {
                //スロットが指す位置のアイテムがそもそも範囲外だった場合
                this.hide();
                ci.cancel();
            }
        }
        if(this.type == SlotType.HOTBAR) {
            if(((IStorageChangable) container).isValidSlot(this.slot)) {
                this.show();
            } else this.hide();
        }
        if(this.type == SlotType.ARMOR) {
            if(((IStorageChangable) container).isActiveArmor()) {
                this.show();
            } else this.hide();
        }
        if(this.type == SlotType.OFFHAND) {
            if(((IStorageChangable) container).isActiveOffhand()) {
                this.show();
            } else this.hide();
        }
        if(this.type == SlotType.CRAFT) {

        }
        if(this.type == SlotType.RESULT) {

        }


    }

    @Inject(method = "set(Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    public void onSetItem(ItemStack p_40240_, CallbackInfo ci) {



        if(this.type == SlotType.INVENTORY && page > 0) {
            if(this.slot + 27 * page < ((IStorageChangable) container).getInventorySize()) {
                this.show();
                this.container.setItem(this.slot + 27 * page + 5, p_40240_);
                this.setChanged();
                ci.cancel();
            } else {
                //スロットが指す位置のアイテムがそもそも範囲外だった場合
                this.hide();
                ci.cancel();
            }
        }
        if(this.type == SlotType.HOTBAR) {
            if(((IStorageChangable) container).isValidSlot(this.slot)) {
                this.show();
            } else this.hide();
        }
        if(this.type == SlotType.ARMOR) {

        }
        if(this.type == SlotType.OFFHAND) {

        }
    }

    @Inject(method = "getItem()Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    public void onGetItem(CallbackInfoReturnable<ItemStack> cir) {
        //System.out.println("called slot is " + (this.slot + 27*page + 5));
        if(this.type == SlotType.INVENTORY && page > 0) {
            if(this.slot + 27 * page < ((IStorageChangable) container).getInventorySize()) {
                this.show();
                //System.out.println("set item to " +  (this.slot + 27*page + 5) + " name " + this.container.getItem(this.slot + 27*page + 5));
                cir.setReturnValue(this.container.getItem(this.slot + 27 * page + 5));
            } else {
                //スロットが指す位置のアイテムがそもそも範囲外だった場合
                this.hide();
                //Fix me slotからgetするときにこれによってairが入ったか
                cir.setReturnValue(ItemStack.EMPTY);
            }
        }
        if(this.type == SlotType.HOTBAR) {
            if(((IStorageChangable) container).isValidSlot(this.slot)) {
                this.show();
            } else this.hide();
        }
        if(this.type == SlotType.ARMOR) {

        }
        if(this.type == SlotType.OFFHAND) {

        }
    }

    @Inject(method = "remove(I)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    public void onRemoveItem(int p_40227_, CallbackInfoReturnable<ItemStack> cir) {
        if(this.type == SlotType.INVENTORY && page > 0) {
            if(this.slot + 27 * page < ((IStorageChangable) container).getInventorySize()) {
                this.show();
                cir.setReturnValue(this.container.removeItem(this.slot + 27 * page + 5, p_40227_));
            } else {
                //スロットが指す位置のアイテムがそもそも範囲外だった場合
                this.hide();
                cir.setReturnValue(ItemStack.EMPTY);
            }
        }
        if(this.type == SlotType.HOTBAR) {
            if(((IStorageChangable) container).isValidSlot(this.slot)) {
                this.show();
            } else this.hide();
        }
        if(this.type == SlotType.ARMOR) {

        }
        if(this.type == SlotType.OFFHAND) {

        }
    }

    @Inject(method = "tryRemove(IILnet/minecraft/world/entity/player/Player;)Ljava/util/Optional;", at = @At("HEAD"), cancellable = true)
    public void onTryRemoveItem(int p_150642_, int p_150643_, Player p_150644_, CallbackInfoReturnable<Optional<ItemStack>> cir) {
        System.out.println("try remove on " + (this.slot));
    }

}
