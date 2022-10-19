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
import net.minecraft.world.item.Item;
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
import java.util.Collections;
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
        inventorySize = 92;

        items = LockableItemStackList.withSize(inventorySize, (Inventory)(Object)this , false);
        armor = LockableItemStackList.withSize(4, (Inventory)(Object)this,true);
        offhand = LockableItemStackList.withSize(1, (Inventory)(Object)this,true);
        compartments.add(0, items);
        compartments.add(1, armor);
        compartments.add(2, offhand);

        //Todo プレイヤーに紐付けられたスロット数を初期化で適用する

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

        System.out.println("free");
        System.out.println(cir.getReturnValue());
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

            compartments.remove(armor);
            armor = LockableItemStackList.withSize(4, (Inventory)(Object)this,true);
            compartments.add(1, armor);

            this.isActiveArmor = false;
            return;
        }

        compartments.remove(armor);
        armor = LockableItemStackList.withSize(4, (Inventory)(Object)this,false);
        compartments.add(1, armor);

        this.isActiveArmor = true;

        player.sendSystemMessage(Component.literal("Armor Toggled!"));
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

            compartments.remove(offhand);
            offhand = LockableItemStackList.withSize(1, (Inventory)(Object)this,true);
            compartments.add(2, offhand);

            this.isActiveOffhand = false;
            return;
        }

        compartments.remove(offhand);
        offhand = LockableItemStackList.withSize(1, (Inventory)(Object)this,false);
        compartments.add(2, offhand);

        this.isActiveOffhand = true;

        player.sendSystemMessage(Component.literal("Offhand Toggled!"));
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
        //永続スロットの追加
        //Todo やっぱり一時スロットを別にするのはやめる
        //Todo 正直処理が重くなるし意味がない


        //ゼロスロットを下回るとゲームが落ちる
        //->これに関してはインデックスがマイナスを下回ってるだけ
        //0止めロジックを記述すれば解決するはず


        //なんかマイナス方向に小さくしたときにアイテムが消滅する？
        //ホットバーの奇数スロットに入れたアイテムがもれなく消滅している
        //たぶん拾ったときに一番スロットにものが入ってそのまま消滅という流れ
        //アイテムリストがsetを拒絶している？
        //しかもスロット数をへらすたびに偶数奇数が入れ替わっている？
        //そしてなぜか一つスロットに入れただけなのに27個に増える

        //多分正しくロックがかかっていない
        //setしたときの同期




        //Todo SlotTypeがINVENTORYではなくHOTBARになっているためにバグっている？

        //Todo とりあえず通常インベントリの減少がうまく行ったら一段落
        //Todo 消費した食べ物がアイテムスロット補充されない->数はあっているのでアップデート処理が行われていないかも？
        //Todo 食べ物はサーバー上ではちゃんと消費されているし補充もされているがクライアント側に同期されていない
        //Todo 36スロットを下回ってすぐに歯抜け現象が起こる，はじめにホットバーから使い物にならなくなる
        //Todo 増殖とかはしない模様，単にスロットのアクセスと同期，validslotメソッドが悪さしている
        //Todo InventoryEffectのapplyに新しいオーバーロードを追加，引数でスロット変更数を変えられるようにする．

        //Todo InventoryActivateFoodの作成，常に減らす方向

        //Todo 2x2クラフトの無効化ロジックを実装する
        //Todo craftingcontainerクラスを書き換える必要があるかも
        //Todo craftingcontainer内のitemsにアイテムが格納されたら問答無用でその場に吐き出す
        //craftingcontainerはplayer.getInventory().getCraftingSlotsで参照可能

        //Todo 不使用ホットバースロットの除去・非表示機能の実装
        //Todo インベントリ・スロット・スクリーン全部を書き換える必要がある
        //Todo HUDの表示クラスを見つける必要がある(TinyInvを参考にする？)


        inventorySize += change;
        LockableItemStackList newItems;
        //とりあえずLockableItemStackListとして宣言してから挿入する？


        if(inventorySize < 0)inventorySize = 0;

        if(inventorySize< 36)
        {
            //36以内になってしまう場合にはスロットは36固定
            newItems = LockableItemStackList.withSize(36, (Inventory)(Object)this,false);

            for(int i=0; i< (36-inventorySize) ; i++)
            {
                //まず頭から順にtrueにしていく
                newItems.lockList.set(i, true);
                //最後に対応を合わせるために逆順にする
                Collections.reverse(newItems.lockList);


                int j = 0;
                newItems.lockList.forEach((b)->{System.out.println(" val is "+ b);});
            }

            //減らすべき分の要素のstopperをtrueにしていく
            //置き換えのときのsetで弾かれて自動で放り投げられるのでほかはそのままでOK?
        }
        else
        {
            newItems = LockableItemStackList.withSize(inventorySize, (Inventory)(Object)this,false);
        }



        //置き換え
        for(int i=0; i<(change>0?items:newItems).size(); i++)
        {
            newItems.set(i, items.get(i));
            items.set(i, ItemStack.EMPTY);
        }

        //ぶちまけ
        for(ItemStack item: items)
        {
            Level level = player.level;
            ItemEntity itementity = new ItemEntity(level, player.getX(), player.getEyeY() - 0.3, player.getZ(), item);
            itementity.setDefaultPickUpDelay();
            itementity.setThrower(player.getUUID());
            level.addFreshEntity(itementity);
        }

        //最後にitemsを更新，参照をcompartmentsに挿入して終了
        compartments.remove(items);
        items = newItems;
        compartments.add(0, items);

        player.sendSystemMessage(Component.literal(String.format("Storage Size Changed to %s", change)));
    }

    @Override
    public boolean isValidSlot(int id)
    {
        if(id < 36)
        {
            return !((LockableItemStackList)items).lockList.get(id);
        }
        //armor
        else if(id >= 36 && id < 40)
        {
            return !((LockableItemStackList)armor).lockList.get(id-36);
        }
        //offhand
        else if(id == 40)
        {
            return !((LockableItemStackList)offhand).lockList.get(id-40);
        }
        //minipouch
        else if(id > 40)
        {
            return !((LockableItemStackList)items).lockList.get(id-5);
        }
        return true;
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
    }

    @Override
    public ListTag saveAdditional(ListTag tag)
    {
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
    }
}
