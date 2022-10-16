package ga.melara.stevesminipouch.mixin;

import com.google.common.collect.ImmutableList;
import ga.melara.stevesminipouch.Config;
import ga.melara.stevesminipouch.util.IAdditionalStorage;
import ga.melara.stevesminipouch.util.IStorageChangable;
import ga.melara.stevesminipouch.util.LockableItemStackList;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import org.apache.commons.lang3.Validate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;


//こいつはサーバー側
@Mixin(Inventory.class)
public abstract class InventoryMixin implements IStorageChangable, IAdditionalStorage
{

    /*
    Todo コマンド・インベントリ拡張アイテムの放つイベントに合わせてitemsやsizeを増減する


    拾ってもちゃんとスロットに反映されない問題->Inventory.addのどこかがおかしい？
     */

    private int maxPage;
    private int inventorySize;


    private boolean isActiveInventory = Config.DEFAULT_INVENTORY.get();
    private boolean isActiveArmor = Config.DEFAULT_ARMOR.get();
    private boolean isActiveOffhand = Config.DEFAULT_OFFHAND.get();

    @Shadow
    public NonNullList<ItemStack> items;

    @Shadow
    public NonNullList<ItemStack> armor;

    @Shadow
    public NonNullList<ItemStack> offhand;

    @Shadow
    public int selected;

    //こいつの参照だけは絶対に変更するな！！
    @Shadow
    public List<NonNullList<ItemStack>> compartments = new ArrayList<NonNullList<ItemStack>>();

    @Shadow
    private boolean hasRemainingSpaceForItem(ItemStack p_36015_, ItemStack p_36016_)
    {
        return false;
    }

    @Shadow
    public ItemStack getItem(int id)
    {
        return null;
    }


    @Shadow
    public abstract void tick();

    @Shadow @Final public Player player;

    @Inject(method = "<init>", at = @At(value = "RETURN"), cancellable = true)
    public void oninitRender(CallbackInfo ci)
    {

        maxPage = 5;
        //もとの数より減らしてはいけない……
        inventorySize = 39;

        items = LockableItemStackList.withSize(36, (Inventory)(Object)this , false);
        armor = LockableItemStackList.withSize(4, (Inventory)(Object)this,true);
        offhand = LockableItemStackList.withSize(1, (Inventory)(Object)this,true);
        compartments.add(0, items);
        compartments.add(1, armor);
        compartments.add(2, offhand);

        //Todo プレイヤーに紐付けられたスロット数を初期化で適用する

        compartments.remove(items);
        items = LockableItemStackList.withSize(inventorySize, (Inventory)(Object)this,false);
        compartments.add(0, items);

        isActiveOffhand = false;
        isActiveArmor = false;
    }


    @Inject(method = "getSlotWithRemainingSpace(Lnet/minecraft/world/item/ItemStack;)I", at = @At(value = "HEAD"), cancellable = true)
    public void onGetRemainingSpace(ItemStack p_36051_, CallbackInfoReturnable<Integer> cir)
    {
        if(this.hasRemainingSpaceForItem(this.getItem(this.selected), p_36051_))
        {
            cir.setReturnValue(this.selected);
        } else if(this.hasRemainingSpaceForItem(this.getItem(40), p_36051_))
        {
            cir.setReturnValue(40);
        } else
        {
            for(int i = 0; i < this.items.size(); ++i)
            {
                if(this.hasRemainingSpaceForItem(this.items.get(i), p_36051_))
                {
                    if(i < 36) cir.setReturnValue(i);
                    else cir.setReturnValue(i + 5);
                }
            }
        }

        System.out.println("remaining");
        //this.items.forEach(System.out::println);
        System.out.println(cir.getReturnValue());

    }

    @Inject(method = "getFreeSlot()I", at = @At(value = "HEAD"), cancellable = true)
    public void onGetFreeSlot(CallbackInfoReturnable<Integer> cir)
    {
        for(int i = 0; i < this.items.size(); ++i)
        {
            if(this.items.get(i).isEmpty())
            {
                if(i < 36) cir.setReturnValue(i);
                else cir.setReturnValue(i + 5);
            }
        }
        //getSlotWithRemainingSpace, getFreeSlot双方が-1を返してしまっている
        //空いているスロットがあるときでもなぜか36が返る？

//        public int getFreeSlot() {
//        for(int i = 0; i < this.items.size(); ++i) {
//            if (this.items.get(i).isEmpty()) {
//                return i;
//            }
//        }
//
//        return -1;
//    }


        System.out.println("free");
        System.out.println(cir.getReturnValue());
//        for(int i = 0; i < this.items.size(); ++i) {
//            System.out.printf("item%s : %s%n", i, this.items.get(i).toString());
//        }
    }


    @Override
    public void toggleInventory(Player player)
    {
        //全部の無効化
        //他の機能をまとめて起動するだけなので実装は後で
        player.sendSystemMessage(Component.literal("Inventory Toggled!"));

    }


    @Override
    public void toggleArmor(Player player)
    {
        //アーマーリストの無効化
        //溢れたアイテムを撒き散らす
        //menu.slotsを回してSlotType.ARMORを無効化・隠蔽処理有効化

        if(this.isActiveArmor)
        {
            for(ItemStack item: armor)
            {
                Level level = player.level;
                ItemEntity itementity = new ItemEntity(level, player.getX(), player.getEyeY() - 0.3, player.getZ(), item);
                itementity.setDefaultPickUpDelay();
                itementity.setThrower(player.getUUID());
                level.addFreshEntity(itementity);
            }
            this.isActiveArmor = false;
            return;
        }
        this.isActiveArmor = true;



        player.sendSystemMessage(Component.literal("Armor Toggled!"));


//        if(armor.size() == 0)
//        {
//            compartments.remove(armor);
//            armor = NonNullList.withSize(4, ItemStack.EMPTY);
//            compartments.add(1, armor);
//        }
//        else
//        {
//            for(ItemStack item: armor)
//            {
//                //ぶちまけ
//            }
//            compartments.remove(armor);
//            armor = NonNullList.withSize(0, ItemStack.EMPTY);
//            compartments.add(1, armor);
//        }
    }

    @Override
    public void toggleOffhand(Player player)
    {
        if(this.isActiveOffhand)
        {
            for(ItemStack item: offhand)
            {
                Level level = player.level;
                ItemEntity itementity = new ItemEntity(level, player.getX(), player.getEyeY() - 0.3, player.getZ(), item);
                itementity.setDefaultPickUpDelay();
                itementity.setThrower(player.getUUID());
                level.addFreshEntity(itementity);
            }
            this.isActiveOffhand = false;
            return;
        }
        this.isActiveOffhand = true;
        //オフハンドリストの無効化(というかダミーデータ挿入)
        //溢れたアイテムを撒き散らす
        //menu.slotsを回してSlotType.OFFHANDを無効化・隠蔽処理有効化
        player.sendSystemMessage(Component.literal("Offhand Toggled!"));

//        for(ItemStack item: armor)
//        {
//            //ぶちまけ
//        }
//        compartments.remove(armor);
//        armor = NonNullList.withSize(0, ItemStack.EMPTY);
//        compartments.add(1, armor);
    }

    @Override
    public boolean isActiveInventory()
    {
        return this.isActiveInventory;
    }

    @Override
    public boolean isActiveArmor()
    {
        return this.isActiveArmor;
    }

    @Override
    public boolean isActiveOffhand()
    {
        return this.isActiveOffhand;
    }

    @Override
    public void changeStorageSize(int change, Player player)
    {
        //リストを再設定する
        //溢れたアイテムを撒き散らす
        //menu.slotsによる無効化は
        player.sendSystemMessage(Component.literal(String.format("Storage Size Changed to %s", change)));
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

//        for(ItemStack item: armor)
//        {
//            for(int j= newList.size(); j< items.size(); j++)
//            {
//                ItemEntity itementity = new ItemEntity(level, entity.getX(), entity.getEyeY() - 0.3, entity.getZ(), items.get(j));
//                itementity.setDefaultPickUpDelay();
//                itementity.setThrower(entity.getUUID());
//                level.addFreshEntity(itementity);
//            }
//        }
//        compartments.remove(armor);
//        armor = NonNullList.withSize(0, ItemStack.EMPTY);
//        compartments.add(1, armor);
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

        for(int i = 0; i < 36; ++i)
        {
            if(!items.get(i).isEmpty())
            {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putByte("Slot", (byte) i);
                items.get(i).save(compoundtag);
                tags.add(compoundtag);
            }
        }

        for(int j = 0; j < this.armor.size(); ++j)
        {
            if(!armor.get(j).isEmpty())
            {
                CompoundTag compoundtag1 = new CompoundTag();
                compoundtag1.putByte("Slot", (byte) (j + 100));
                armor.get(j).save(compoundtag1);
                tags.add(compoundtag1);
            }
        }

        for(int k = 0; k < this.offhand.size(); ++k)
        {
            if(!offhand.get(k).isEmpty())
            {
                CompoundTag compoundtag2 = new CompoundTag();
                compoundtag2.putByte("Slot", (byte) (k + 150));
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

        for(int i = 0; i < tags.size(); ++i)
        {
            CompoundTag compoundtag = tags.getCompound(i);
            int j = compoundtag.getByte("Slot") & 255;
            ItemStack itemstack = ItemStack.of(compoundtag);
            if(!itemstack.isEmpty())
            {
                if(j >= 0 && j < 36)
                {
                    items.set(j, itemstack);
                } else if(j >= 100 && j < armor.size() + 100)
                {
                    armor.set(j - 100, itemstack);
                } else if(j >= 150 && j < offhand.size() + 150)
                {
                    offhand.set(j - 150, itemstack);
                }
            }
        }

        items.forEach(System.out::println);
        ci.cancel();
    }

    @Inject(method = "setItem(ILnet/minecraft/world/item/ItemStack;)V", at = @At(value = "HEAD"), cancellable = true)
    public void onSetItem(int id, ItemStack itemStack, CallbackInfo ci)
    {


        System.out.printf("id is %s, item is %s%n", String.valueOf(id), itemStack.toString());
        //vanilla inventory
        if(id < 36)
        {
            if(id + 1 > items.size()) ci.cancel();
            else if(items != null)
            {
                items.set(id, itemStack);
            }
            ci.cancel();
        }

        //armor
        else if(id >= 36 && id < 40)
        {
            if(id - 35 > armor.size()) ci.cancel();
            else if(armor != null)
            {
                System.out.println("armor set");
                armor.set(id - 36, itemStack);
            }
            ci.cancel();
        }

        //offhand
        else if(id == 40)
        {
            if(id - 39 > offhand.size()) ci.cancel();
            else if(offhand != null)
            {
                offhand.set(id - 40, itemStack);
            }
            ci.cancel();
        }

        //minipouch
        else if(id > 40)
        {
            if(id - 40 > items.size()) ci.cancel();
            else if(items != null)
            {
                items.set(id - 5, itemStack);
            }
            ci.cancel();
        } else
        {
            ci.cancel();
        }

    }

    @Inject(method = "getItem(I)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "HEAD"), cancellable = true)
    public void onGetItem(int id, CallbackInfoReturnable<ItemStack> cir)
    {

        //Todo なぜか5スロット先のアイテムをgetしてしまう
        //Todo レンダリングのときのgetは正しい
        //Todo ホットバーは正しい(SlotType.Inventoryのスロットのみおかしい？)

        //vanilla inventory
        if(id < 36)
        {
            if(id + 1 > items.size()) cir.setReturnValue(ItemStack.EMPTY);
            else if(items != null)
            {
                cir.setReturnValue(items.get(id));
            }
        }

        //armor
        else if(id >= 36 && id < 40)
        {
            if(id - 35 > armor.size()) cir.setReturnValue(ItemStack.EMPTY);
            else if(armor != null)
            {
                cir.setReturnValue(armor.get(id - 36));
            }
        }

        //offhand
        else if(id == 40)
        {
            if(id - 39 > offhand.size()) cir.setReturnValue(ItemStack.EMPTY);
            else if(offhand != null)
            {
                cir.setReturnValue(offhand.get(id - 40));
            }
        }

        //minipouch
        else if(id > 40)
        {
            if(id - 40 > items.size()) cir.setReturnValue(ItemStack.EMPTY);
            else if(items != null)
            {
                cir.setReturnValue(items.get(id - 5));
            }
        }

        //System.out.println(cir.getReturnValue().toString());
    }

    @Inject(method = "removeItem(II)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "HEAD"), cancellable = true)
    public void onRemoveItem(int id, int decrement, CallbackInfoReturnable<ItemStack> cir)
    {
        //vanilla inventory
        if(id < 36)
        {
            if(id + 1 > items.size()) cir.setReturnValue(ItemStack.EMPTY);
            else if(items != null && !items.get(id).isEmpty())
            {
                cir.setReturnValue(ContainerHelper.removeItem(items, id, decrement));
            }
        }

        //armor
        else if(id >= 36 && id < 40)
        {
            if(id - 35 > armor.size()) cir.setReturnValue(ItemStack.EMPTY);
            else if(armor != null && !armor.get(id - 36).isEmpty())
            {
                cir.setReturnValue(ContainerHelper.removeItem(armor, id - 36, decrement));
            }
        }

        //offhand
        else if(id == 40)
        {
            if(id - 39 > offhand.size()) cir.setReturnValue(ItemStack.EMPTY);
            else if(offhand != null && !offhand.get(id - 40).isEmpty())
            {
                cir.setReturnValue(ContainerHelper.removeItem(offhand, id - 40, decrement));
            }
        }

        //minipouch
        else if(id > 40)
        {
            if(id - 40 > items.size()) cir.setReturnValue(ItemStack.EMPTY);
            else if(items != null && !items.get(id - 5).isEmpty())
            {
                cir.setReturnValue(ContainerHelper.removeItem(items, id - 5, decrement));
            }
        } else
        {
            cir.setReturnValue(ItemStack.EMPTY);
        }

        //System.out.println("tryremove from inventory!  id: " + id);
    }

    @Override
    public ListTag saveAdditional(ListTag tag)
    {

        //Todo セーブ関連がなんかおかしい
        //Todo NBT関連は問題なし，Inventoryとスロット間の通信がうまく言っていない可能性がある
        //Todo 単にスロットの初期化に失敗してただけだった

        //itemsの35番までは無視
        for(int i = 36; i < items.size(); ++i)
        {
            //System.out.println("saveAdditional " + i);
            if(!items.get(i).isEmpty())
            {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putInt("Slot", i);
                items.get(i).save(compoundtag);
                tag.add(compoundtag);
            }
        }

        return tag;
    }

    @Override
    public void loadAdditional(ListTag tag)
    {
        //タグは全部読むので0からでOK
        for(int i = 0; i < tag.size(); ++i)
        {
            CompoundTag compoundtag = tag.getCompound(i);
            int j = compoundtag.getInt("Slot");
            ItemStack itemstack = ItemStack.of(compoundtag);
            if(!itemstack.isEmpty())
            {
                if(j < items.size())
                {
                    items.set(j, itemstack);
                }
            }
        }

        //items.forEach(System.out::println);
    }
}
