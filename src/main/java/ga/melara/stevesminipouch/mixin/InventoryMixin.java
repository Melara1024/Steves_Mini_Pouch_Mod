package ga.melara.stevesminipouch.mixin;

import com.google.common.collect.ImmutableList;
import ga.melara.stevesminipouch.Config;
import ga.melara.stevesminipouch.util.IAdditionalStorage;
import ga.melara.stevesminipouch.util.IStorageChangable;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;


//こいつはサーバー側
@Mixin(Inventory.class)
public class InventoryMixin implements IStorageChangable, IAdditionalStorage {

    /*
    Todo コマンド・インベントリ拡張アイテムの放つイベントに合わせてitemsやsizeを増減する
     */

    private int maxPage;
    private int inventorySize;

    @Shadow
    public NonNullList<ItemStack> items;

    @Shadow
    public NonNullList<ItemStack> armor;

    @Shadow
    public NonNullList<ItemStack> offhand;

    //こいつの参照だけは絶対に変更するな！！
    @Shadow
    public List<NonNullList<ItemStack>> compartments = new ArrayList<NonNullList<ItemStack>>();


    @Inject(method = "<init>", at = @At(value = "RETURN"), cancellable = true)
    public void oninitRender(CallbackInfo ci)
    {
        maxPage = 2;
        inventorySize = 90;

        items = NonNullList.withSize(36, ItemStack.EMPTY);
        armor = NonNullList.withSize(4, ItemStack.EMPTY);
        offhand = NonNullList.withSize(1, ItemStack.EMPTY);
        compartments.add(items);
        compartments.add(armor);
        compartments.add(offhand);

        compartments.remove(items);
        items = NonNullList.withSize(90, ItemStack.EMPTY);
        compartments.add(items);
    }

    @Override
    public void changeStorageSize(int change, Level level, LivingEntity entity)
    {
        //Todo いちいちリセット+値のコピーを行う
//        if(change < 0)return;
//
//        maxPage = (int)(Math.ceil((change-9) / 27)*2);
//
//        System.out.println("storagesize changed to " + maxPage);
//        //大きくなる方向ならEMPTYを増やす
//        NonNullList<ItemStack> newList = NonNullList.withSize(maxPage, ItemStack.EMPTY);
//
//        if(change >= maxPage)
//        {
//            for(int i=0; i<items.size(); i++)
//            {
//                newList.set(i, items.get(i));
//            }
//        }
//        //小さくなる方向なら引数Levelのentityがいる位置にアイテムエンティティを召喚
//        else
//        {
//            for(int i=0; i< newList.size(); i++)
//            {
//                newList.set(i, items.get(i));
//            }
//
//            for(int j= newList.size(); j< items.size(); j++)
//            {
//                //items.get(j).
//                ItemEntity itementity = new ItemEntity(level, entity.getX(), entity.getEyeY() - 0.3, entity.getZ(), items.get(j));
//                itementity.setDefaultPickUpDelay();
//                itementity.setThrower(entity.getUUID());
//                level.addFreshEntity(itementity);
//            }
//        }
    }

    @Override
    public int getMaxPage()
    {
        return maxPage;
    }

    @Override
    public int getSize()
    {
        return inventorySize;
    }

    @Inject(method = "save(Lnet/minecraft/nbt/ListTag;)Lnet/minecraft/nbt/ListTag;", at = @At(value = "HEAD"), cancellable = true)
    public void onSave(ListTag p_36027_, CallbackInfoReturnable<ListTag> cir)
    {
        System.out.println("armor id " + this.armor);
        for(ItemStack i : items)
        {
            //System.out.println(i.getDisplayName());
        }
        for(int i = 0; i < 36; ++i) {
            if (!this.items.get(i).isEmpty()) {
                System.out.println(items.get(i).getDisplayName());
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putByte("Slot", (byte)i);
                this.items.get(i).save(compoundtag);
                p_36027_.add(compoundtag);
            }
        }

        for(int j = 0; j < this.armor.size(); ++j) {
            if (!this.armor.get(j).isEmpty()) {
                CompoundTag compoundtag1 = new CompoundTag();
                compoundtag1.putByte("Slot", (byte)(j + 100));
                this.armor.get(j).save(compoundtag1);
                p_36027_.add(compoundtag1);
            }
        }

        for(int k = 0; k < this.offhand.size(); ++k) {
            if (!this.offhand.get(k).isEmpty()) {
                CompoundTag compoundtag2 = new CompoundTag();
                compoundtag2.putByte("Slot", (byte)(k + 150));
                this.offhand.get(k).save(compoundtag2);
                p_36027_.add(compoundtag2);
            }
        }

        cir.setReturnValue(p_36027_);
    }

    @Inject(method = "load(Lnet/minecraft/nbt/ListTag;)V", at = @At(value = "HEAD"), cancellable = true)
    public void onLoad(ListTag p_36036_, CallbackInfo ci)
    {
        this.items.clear();
        this.armor.clear();
        this.offhand.clear();

        for(int i = 0; i < p_36036_.size(); ++i) {
            CompoundTag compoundtag = p_36036_.getCompound(i);
            int j = compoundtag.getByte("Slot") & 255;
            ItemStack itemstack = ItemStack.of(compoundtag);
            if (!itemstack.isEmpty()) {
                if (j >= 0 && j < this.items.size()) {
                    this.items.set(j, itemstack);
                } else if (j >= 100 && j < this.armor.size() + 100) {
                    this.armor.set(j - 100, itemstack);
                } else if (j >= 150 && j < this.offhand.size() + 150) {
                    this.offhand.set(j - 150, itemstack);
                }
            }
        }
        ci.cancel();
    }

    @Override
    public ListTag saveAdditional(ListTag tag) {

        for(int i = 36; i < this.items.size(); ++i) {
            //System.out.println("saveAdditional " + i);
            if (!this.items.get(i).isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putInt("Slot", i);
                this.items.get(i).save(compoundtag);
                tag.add(compoundtag);
            }
        }

        return tag;
    }

    @Override
    public void loadAdditional(ListTag tag) {
        for(int i = 0; i < tag.size(); ++i) {
            CompoundTag compoundtag = tag.getCompound(i);
            int j = compoundtag.getInt("Slot");
            ItemStack itemstack = ItemStack.of(compoundtag);
            if (!itemstack.isEmpty()) {
                if (j >= 0 && j < this.items.size()) {
                    this.items.set(j, itemstack);
                }
            }
        }
    }
}
