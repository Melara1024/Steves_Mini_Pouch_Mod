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
        compartments.add(0, items);
        compartments.add(1, armor);
        compartments.add(2, offhand);

        compartments.remove(items);
        items = NonNullList.withSize(90, ItemStack.EMPTY);
        compartments.add(0, items);
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
    public void onSave(ListTag tags, CallbackInfoReturnable<ListTag> cir)
    {
//        System.out.println("armor id " + armor);
//        for(ItemStack i : items)
//        {
//            System.out.println(i.getDisplayName());
//        }
        for(int i = 0; i < 36; ++i) {
            if (!items.get(i).isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putByte("Slot", (byte)i);
                items.get(i).save(compoundtag);
                tags.add(compoundtag);
            }
        }

        for(int j = 0; j < this.armor.size(); ++j) {
            if (!armor.get(j).isEmpty()) {
                CompoundTag compoundtag1 = new CompoundTag();
                compoundtag1.putByte("Slot", (byte)(j + 100));
                armor.get(j).save(compoundtag1);
                tags.add(compoundtag1);
            }
        }

        for(int k = 0; k < this.offhand.size(); ++k) {
            if (!offhand.get(k).isEmpty()) {
                CompoundTag compoundtag2 = new CompoundTag();
                compoundtag2.putByte("Slot", (byte)(k + 150));
                offhand.get(k).save(compoundtag2);
                tags.add(compoundtag2);
            }
        }

        cir.setReturnValue(tags);
    }

    @Inject(method = "load(Lnet/minecraft/nbt/ListTag;)V", at = @At(value = "HEAD"), cancellable = true)
    public void onLoad(ListTag tags, CallbackInfo ci)
    {
        items.clear();
        armor.clear();
        offhand.clear();

        for(int i = 0; i < tags.size(); ++i) {
            CompoundTag compoundtag = tags.getCompound(i);
            int j = compoundtag.getByte("Slot") & 255;
            ItemStack itemstack = ItemStack.of(compoundtag);
            if (!itemstack.isEmpty()) {
                if (j >= 0 && j < 36) {
                    items.set(j, itemstack);
                } else if (j >= 100 && j < armor.size() + 100) {
                    armor.set(j - 100, itemstack);
                } else if (j >= 150 && j < offhand.size() + 150) {
                    offhand.set(j - 150, itemstack);
                }
            }
        }

        items.forEach(System.out::println);
        ci.cancel();
    }

    @Inject(method = "setItem(ILnet/minecraft/world/item/ItemStack;)V", at = @At(value = "HEAD"), cancellable = true)
    public void onSetItem(int id, ItemStack p_36000_, CallbackInfo ci) {

        //vanilla inventory
        if(id < 36)
        {
            if (items != null) {
                items.set(id, p_36000_);
            }
            ci.cancel();
        }

        //armor
        if(id >= 36 && id < 40)
        {
            if (armor != null) {
                armor.set(id - 36, p_36000_);
            }
            ci.cancel();
        }

        //offhand
        if(id == 40)
        {
            if (offhand != null) {
                offhand.set(id -40, p_36000_);
            }
            ci.cancel();
        }

        //minipouch
        if(id > 40)
        {
            if (items != null) {
                items.set(id-5, p_36000_);
            }
            ci.cancel();
        }
    }

    @Inject(method = "getItem(I)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "HEAD"), cancellable = true)
    public void onGetItem(int id, CallbackInfoReturnable<ItemStack> cir)
    {

        //vanilla inventory
        if(id < 36)
        {
            if (items != null) {
                cir.setReturnValue(items.get(id));
            }
        }

        //armor
        if(id >= 36 && id < 40)
        {
            if (armor != null) {
                cir.setReturnValue(armor.get(id - 36));
            }
        }

        //offhand
        if(id == 40)
        {
            if (offhand != null) {
                cir.setReturnValue(offhand.get(id - 40));
            }
        }

        //minipouch
        if(id > 40)
        {
            if (items != null) {
                cir.setReturnValue(items.get(id-5));
            }
        }
    }

    @Override
    public ListTag saveAdditional(ListTag tag) {

        //Todo セーブ関連がなんかおかしい

        //itemsの35番までは無視
        for(int i = 36; i < items.size(); ++i) {
            //System.out.println("saveAdditional " + i);
            if (!items.get(i).isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putInt("Slot", i);
                items.get(i).save(compoundtag);
                tag.add(compoundtag);
            }
        }

        return tag;
    }

    @Override
    public void loadAdditional(ListTag tag) {
        //タグは全部読むので0からでOK
        for(int i = 0; i < tag.size(); ++i) {
            CompoundTag compoundtag = tag.getCompound(i);
            int j = compoundtag.getInt("Slot");
            ItemStack itemstack = ItemStack.of(compoundtag);
            if (!itemstack.isEmpty()) {
                if (j < items.size()) {
                    items.set(j, itemstack);
                }
            }
        }
        items.forEach(System.out::println);
    }
}
