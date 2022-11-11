package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.Config;
import ga.melara.stevesminipouch.ModRegistry;
import ga.melara.stevesminipouch.event.EffectSlotSyncEvent;
import ga.melara.stevesminipouch.stats.ClientInventoryData;
import ga.melara.stevesminipouch.stats.InventorySyncEvent;
import ga.melara.stevesminipouch.stats.PlayerInventorySizeData;
import ga.melara.stevesminipouch.util.*;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


//こいつはサーバー側
@Mixin(Inventory.class)
public abstract class InventoryMixin implements IStorageChangable, IAdditionalStorage {

    /*

     */

    private int maxPage;

    private int inventorySize = 36;

    private int enchantSize = 0;

    private int effectSize = 0;
    private int hotbarSize = 9;


    private boolean isActiveInventory = true;
    private boolean isActiveArmor = true;
    private boolean isActiveOffhand = true;
    private boolean isActiveCraft = true;

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
    private boolean hasRemainingSpaceForItem(ItemStack p_36015_, ItemStack p_36016_) {
        return false;
    }

    @Shadow
    public ItemStack getItem(int id) {
        return null;
    }

    //Todo アーマースロット追加時にスロットへの反映がない，スロット追加してもなぜか溢れる(lock状態)
    //Todo 一度メニューを閉じないとスロット状態が一切更新されない
    //Todo インベントリ側は更新されているのにメニュー側が更新されていない？


    @Shadow
    public abstract void tick();

    @Shadow
    @Final
    @Mutable
    public Player player;

    @Shadow public abstract void placeItemBackInInventory(ItemStack p_150080_);

    public void initMiniPouch(int slot, int effectSize, boolean inv, boolean arm, boolean off, boolean cft)
    {
        this.effectSize = effectSize;
        setStorageSize(slot, player);

        setInventory(inv, player);
        ((IMenuChangable)player.containerMenu).toggleInventory(player);

        setArmor(arm, player);
        ((IMenuChangable)player.containerMenu).toggleArmor(player);

        setOffhand(off, player);
        ((IMenuChangable)player.containerMenu).toggleOffhand(player);

        setCraft(cft, player);
        ((IMenuChangable)player.containerMenu).toggleCraft(player);
    }

    public void initServer(int slot, boolean inv, boolean arm, boolean off, boolean cft)
    {
        System.out.println("init server");
        initMiniPouch(slot, this.effectSize, inv, arm, off, cft);
    }

    @Override
    @SubscribeEvent
    public void initClient(InventorySyncEvent e)
    {
        System.out.println(player);
        System.out.println("init client");
        initMiniPouch(ClientInventoryData.getSlot(),
                ClientInventoryData.getEffectSlot(),
                ClientInventoryData.isActiveInventory(),
                ClientInventoryData.isEquippable(),
                ClientInventoryData.isActiveOffhand(),
                ClientInventoryData.isCraftable());
    }


    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void oninit(Player p_35983_, CallbackInfo ci) {

        MinecraftForge.EVENT_BUS.register(this);

        maxPage = 0;
        //もとの数より減らしてはいけない……
        inventorySize = 36;
        hotbarSize = 9;

        //正直インスタンスをnewしまくるの重すぎ
        //基本はロックのみで済ませてページ追加のみ新しいインスタンスを作る？

        items = LockableItemStackList.withSize(inventorySize, (Inventory) (Object) this, false);
        armor = LockableItemStackList.withSize(4, (Inventory) (Object) this, false);
        ((LockableItemStackList)armor).setObserver((detectItem)->{
            System.out.println(player.getLevel().isClientSide ? "client":"server");
            enchantSize = 0;
            armor.forEach(
                    (item) -> enchantSize += item.getEnchantmentLevel(ModRegistry.SLOT_ENCHANT.get())
            );
            System.out.printf("enchantSize = %d\n",  enchantSize);
            updateStorageSize();
        });
        offhand = LockableItemStackList.withSize(1, (Inventory) (Object) this, false);
        compartments.add(0, items);
        compartments.add(1, armor);
        compartments.add(2, offhand);

        //Todo プレイヤーに紐付けられたスロット数を初期化で適用する

        isActiveInventory = true;
        isActiveOffhand = true;
        isActiveArmor = true;
        isActiveCraft = true;

    }


    @Inject(method = "getSlotWithRemainingSpace(Lnet/minecraft/world/item/ItemStack;)I", at = @At(value = "HEAD"), cancellable = true)
    public void onGetRemainingSpace(ItemStack p_36051_, CallbackInfoReturnable<Integer> cir) {
        if (this.hasRemainingSpaceForItem(this.getItem(this.selected), p_36051_)) {
            cir.setReturnValue(this.selected);
        } else if (this.hasRemainingSpaceForItem(this.getItem(40), p_36051_)) {
            cir.setReturnValue(40);
        } else {
            for (int i = 0; i < this.items.size(); ++i) {
                if (this.hasRemainingSpaceForItem(this.items.get(i), p_36051_)) {
                    if (i < 36) cir.setReturnValue(i);
                    else cir.setReturnValue(i + 5);
                }
            }
        }
    }

    @Inject(method = "getFreeSlot()I", at = @At(value = "HEAD"), cancellable = true)
    public void onGetFreeSlot(CallbackInfoReturnable<Integer> cir) {
        //35番にアイテムが入る問題はここのせい，空きスロットなのでEMPTYを返してしまう
        for (int i = 0; i < this.items.size(); ++i) {
            if (this.items.get(i).isEmpty() && !((LockableItemStackList)items).lockList.get(i)) {
                if (i < 36) cir.setReturnValue(i);
                else cir.setReturnValue(i + 5);
            }
        }
        if(Objects.isNull(cir.getReturnValue()))cir.setReturnValue(-1);
    }


    public void setInventory(boolean change, Player player)
    {
        if(change != isActiveInventory)toggleInventory(player);
    }


    @Override
    public void toggleInventory(Player player) {
        setArmor(false, player);
        setCraft(false, player);
        setStorageSize(1, player);

        isActiveInventory = !isActiveInventory;
        System.out.printf("isActivateInvetory -> %b\n", isActiveInventory);

        ((IMenuChangable)player.containerMenu).toggleInventory(player);

        if(player.getLevel().isClientSide()) player.sendSystemMessage(Component.literal("Inventory Toggled!"));
    }

    public void setArmor(boolean change, Player player)
    {
        if(change != isActiveArmor)toggleArmor(player);
    }


    @Override
    public void toggleArmor(Player player) {

        System.out.println(player.getLevel().isClientSide ? "client inventory!" : "server inventory!");
        //アーマーリストの無効化
        //溢れたアイテムを撒き散らす
        //menu.slotsを回してSlotType.ARMORを無効化・隠蔽処理有効化

        if (this.isActiveArmor) {

            for (ItemStack item : armor) {
                Level level = player.level;
                ItemEntity itementity = new ItemEntity(level, player.getX(), player.getEyeY() - 0.3, player.getZ(), item);
                itementity.setDefaultPickUpDelay();
                itementity.setThrower(player.getUUID());
                level.addFreshEntity(itementity);
            }

            compartments.remove(armor);
            armor = LockableItemStackList.withSize(4, (Inventory) (Object) this, true);
            compartments.add(1, armor);

            this.isActiveArmor = false;
            return;
        }

        compartments.remove(armor);
        armor = LockableItemStackList.withSize(4, (Inventory) (Object) this, false);
        ((LockableItemStackList)armor).setObserver((detectItem)->{
            //Fixme このオブザーバーは呼ばれていない
            System.out.println("called observer");
            System.out.println(player.getLevel().isClientSide());
            enchantSize = 0;
            armor.forEach(
                    (item) -> enchantSize += item.getEnchantmentLevel(ModRegistry.SLOT_ENCHANT.get())
            );
            updateStorageSize();
        });
        compartments.add(1, armor);

        this.isActiveArmor = true;


        ((IMenuChangable)player.containerMenu).toggleArmor(player);

        if(player.getLevel().isClientSide()) player.sendSystemMessage(Component.literal("Armor Toggled!"));
    }

    public void setOffhand(boolean change, Player player)
    {
        if(change != isActiveOffhand)toggleOffhand(player);
    }

    @Override
    public void toggleOffhand(Player player) {
        if (this.isActiveOffhand) {


            for (ItemStack item : offhand) {
                Level level = player.level;
                ItemEntity itementity = new ItemEntity(level, player.getX(), player.getEyeY() - 0.3, player.getZ(), item);
                itementity.setDefaultPickUpDelay();
                itementity.setThrower(player.getUUID());
                level.addFreshEntity(itementity);
            }

            compartments.remove(offhand);
            offhand = LockableItemStackList.withSize(1, (Inventory) (Object) this, true);
            compartments.add(2, offhand);

            this.isActiveOffhand = false;

            return;
        }

        compartments.remove(offhand);
        offhand = LockableItemStackList.withSize(1, (Inventory) (Object) this, false);
        compartments.add(2, offhand);

        this.isActiveOffhand = true;

        ((IMenuChangable)player.containerMenu).toggleOffhand(player);

        if(player.getLevel().isClientSide()) player.sendSystemMessage(Component.literal("Offhand Toggled!"));
    }

    public void setCraft(boolean change, Player player)
    {
        if(change != isActiveCraft)toggleCraft(player);
    }

    @Override
    public void toggleCraft(Player player)
    {
        //Todo アイテムリストに対しての操作はしないがisActiveCraftのトグル操作のみ行う部分
        isActiveCraft = !isActiveCraft;

        ((IMenuChangable)player.inventoryMenu).toggleCraft(player);
        ((IMenuChangable)player.containerMenu).toggleCraft(player);
        ((ICraftingContainerChangable)player.inventoryMenu.getCraftSlots()).toggleCraft(player);

        if(player.getLevel().isClientSide()) player.sendSystemMessage(Component.literal("Craft Toggled!"));
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
    public boolean isActiveCraft()
    {
        return this.isActiveCraft;
    }

    public void setStorageSize(int change, Player player)
    {
        changeStorageSize(change - inventorySize, player);
    }


    @Override
    public void changeStorageSize(int change, Player player)
    {
        inventorySize += change;
        LockableItemStackList newItems;
        //とりあえずLockableItemStackListとして宣言してから挿入する？


        //インベントリは必ず1スロット残す
        //インベントリをゼロスロットにするオプションはコマンドとして実装する
        if(inventorySize < 1)inventorySize = 1;

        hotbarSize = 9;
        int allSize = (inventorySize + effectSize + enchantSize);

        if(allSize <= 36)
        {
            maxPage = 0;
            newItems = LockableItemStackList.withSize(36, (Inventory)(Object)this,false);

            for(int i=0; i< (36-allSize) ; i++)
            {
                //まず頭から順にtrueにしていく
                System.out.printf("allsize -> %d", allSize);
                newItems.lockList.set(35-i, true);
            }

            //減らすべき分の要素のstopperをtrueにしていく
            //置き換えのときのsetで弾かれて自動で放り投げられるのでほかはそのままでOK?
            if(allSize < 9)
            {
                hotbarSize = allSize;
                if(selected > allSize)selected =allSize-1;
            }
        }
        else
        {
            maxPage = (int)Math.floor((allSize-9)/27);
            newItems = LockableItemStackList.withSize(allSize, (Inventory)(Object)this,false);
        }

        //置き換え
        for(int i=0; i<(Math.min(items.size(), newItems.size())); i++)
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

        ((IMenuChangable)player.containerMenu).changeStorageSize(change, player);



        //サーバーにこれを送信しようとしたときにも通信エラーの同じようなのが出る？　やっぱり間違った方面での送信が原因なのでは
        if(player.getLevel().isClientSide()) player.sendSystemMessage(Component.literal(String.format("Storage Size Changed to %s", change)));
    }

    @Override
    public void updateStorageSize()
    {
        Thread t = Thread.currentThread();
        String name = t.getName();
        System.out.println("name=" + name);

        System.out.printf("effect -> %d%n", effectSize);
        System.out.printf("enchant -> %d%n", enchantSize);

        changeStorageSize(0, player);
    }

    @Override
    public void changeEffectSize(int change)
    {
        System.out.printf("changeEffectSize -> %s\n", player.getLevel().isClientSide()?"client": "server");
        effectSize = change;
        updateStorageSize();
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void syncEffectSizeToClient(EffectSlotSyncEvent e)
    {
        System.out.println("sync method");
        this.effectSize = e.getEffectSize();
        updateStorageSize();
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


    @Inject(method = "isHotbarSlot(I)Z", at = @At(value = "HEAD"), cancellable = true)
    private static void onDetectHotbarSlot(int p_36046_, CallbackInfoReturnable<Boolean> cir)
    {
        //単にスロットをホットバーとして認識しなくなるだけ，とくにいらないかも？
        //とりあえずホットバーの最大値はここで指定する
        cir.setReturnValue(p_36046_ >= 0 && p_36046_ <9);
    }

    @Inject(method = "swapPaint(D)V", at = @At(value = "HEAD"), cancellable = true)
    public void onSwapaint(double p_35989_, CallbackInfo ci)
    {


        //こいついじると選択不能になる！！ ただしマウスのみ
        int i = (int)Math.signum(p_35989_);

        for(this.selected -= i; this.selected < 0; this.selected += hotbarSize) {
        }

        while(this.selected >= hotbarSize) {
            this.selected -= hotbarSize;
        }

        ci.cancel();
    }

    @Inject(method = "getSuitableHotbarSlot()I", at = @At(value = "RETURN"), cancellable = true)
    public void onGetSuitableHotbarSlot(CallbackInfoReturnable<Integer> cir)
    {
        System.out.println("suitableHotbar" + cir.getReturnValue());
    }

    @Inject(method = "getSelectionSize()I", at = @At(value = "HEAD"), cancellable = true)
    private static void onGetSelectionSize(CallbackInfoReturnable<Integer> cir)
    {

    }


    @Override
    public int getMaxPage()
    {
        return maxPage;
    }

    @Override
    public int getInventorySize()
    {
        return (inventorySize + effectSize + enchantSize);
    }

    @Override
    public int getHotbarSize()
    {
        return hotbarSize;
    }

    @Override
    public PlayerInventorySizeData getAllData()
    {
        return new PlayerInventorySizeData(inventorySize, isActiveInventory, isActiveArmor, isActiveOffhand, isActiveCraft);
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

        //ここから入れ物の初期化を行う


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

        //items.forEach(System.out::println);

        System.out.println("load finished...");
        ci.cancel();
    }

    @Inject(method = "setItem(ILnet/minecraft/world/item/ItemStack;)V", at = @At(value = "HEAD"), cancellable = true)
    public void onSetItem(int id, ItemStack itemStack, CallbackInfo ci)
    {
        System.out.println(itemStack);
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
    public CompoundTag saveStatus(CompoundTag tag)
    {
        tag.putInt("inventorysize", inventorySize);
        tag.putBoolean("activateinventory",isActiveInventory);
        tag.putBoolean("activateoffhand", isActiveOffhand);
        tag.putBoolean("craftable", isActiveCraft);
        tag.putBoolean("equippable", isActiveArmor);

        return tag;
    }

    @Override
    public void loadStatus(CompoundTag tag)
    {
        int slt = tag.contains("inventorysize")? tag.getInt("inventorysize") : Config.DEFAULT_SIZE.get();
        boolean inv = tag.contains("activateinventory")? tag.getBoolean("activateinventory") : Config.DEFAULT_INVENTORY.get();
        boolean off = tag.contains("activateoffhand")? tag.getBoolean("activateoffhand") : Config.DEFAULT_OFFHAND.get();
        boolean cft = tag.contains("craftable")? tag.getBoolean("craftable") : Config.DEFAULT_CRAFT.get();
        boolean arm = tag.contains("equippable")? tag.getBoolean("equippable") : Config.DEFAULT_ARMOR.get();

        initServer(slt, inv, arm, off, cft);

        ServerPlayer serverPlayer = (ServerPlayer)player;
        ((IMenuSynchronizer)player.containerMenu).initMenu(new PlayerInventorySizeData(slt,this.effectSize, inv, off, cft, arm));
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




    //エンチャントスロット数の確認と更新
    //このメソッドをload/save，setitemなどに挟む

    //Todo エンチャスロット・エフェクトスロットに保存されたアイテムはログイン時にどうなる？

    //Todo エンチャスロット->アイテムをarmorリストに読むときに確認
    //Todo エフェクトスロット->エフェクトの再開がどのタイミングで行われるのか確認すべき
    //Todo もしエフェクトの再開がアイテムリスト初期化より遅いなら，エフェクト再開側から
    //Todo インベントリの挿入を行う？

    //Todo NBTにログアウト時のエンチャスロット数とエフェクトスロット数を保持，setItem時にぶちまけずに一時リストに保存？


}
