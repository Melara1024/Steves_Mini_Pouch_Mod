package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.util.IHasSlotType;
import ga.melara.stevesminipouch.util.SlotType;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Slot.class)
public class SlotMixin implements IHasSlotType {
    /*
    Todo スロットのアクティブ状態を変更できるようにする，新変数closedSlotを追加，これによって閉じられたスロットを表現する

    Todo ページを受け取ってinventoryからの参照を変更できるようにする(これはSlotType.INVENTORYのときのみ適用されるように実装)
    Todo ページの数の分だけインベントリのitemsのインデックスをシフトする，もしもitemsに参照できない場合はスロットを閉じる用に設定
    スロットの変更点
    インベントリから取得するアイテムをページ変数によってシフトする
    プライベート変数slotによってインベントリのitemsリストとの関連付けがされている
    Slotクラスはアイテムそのものを保持しない
    未使用スロットには未使用アイテムを挿入，maypickupのオーバーライドでクリックとピックアップの阻止
    カーソルのホバーに関してはレンダラーで対応可能
     */

    public SlotType type = SlotType.OTHER;
    public int page = 0;

    private static final int ESCAPE_RANGE = 10000;

    @Shadow
    public int x;
    @Shadow
    public int y;

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

    @Override
    public void hide()
    {
        if(!isHiding)this.y = this.y + ESCAPE_RANGE;
        isHiding = true;
    }

    @Override
    public void show()
    {
        if(isHiding)this.y = this.y - ESCAPE_RANGE;
        isHiding = false;
    }

}
