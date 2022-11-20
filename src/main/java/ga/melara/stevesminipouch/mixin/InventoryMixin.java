package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.Config;
import ga.melara.stevesminipouch.ModRegistry;
import ga.melara.stevesminipouch.event.ClientEffectSlotSyncEvent;
import ga.melara.stevesminipouch.event.InventorySyncEvent;
import ga.melara.stevesminipouch.stats.PlayerInventorySizeData;
import ga.melara.stevesminipouch.util.*;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;


@Mixin(Inventory.class)
public abstract class InventoryMixin implements IStorageChangable, IAdditionalStorage {

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


    // Compartments operations are prone to thread collisions and require synchronized.
    @Shadow
    public List<NonNullList<ItemStack>> compartments = new CopyOnWriteArrayList<>() {
        @Override
        public Iterator<NonNullList<ItemStack>> iterator() {
            synchronized(this) {
                return super.iterator();
            }
        }
    };

    @Shadow
    private boolean hasRemainingSpaceForItem(ItemStack itemStack, ItemStack itemStack1) {
        return false;
    }

    @Shadow
    public ItemStack getItem(int id) {
        return null;
    }

    @Shadow
    @Final
    @Mutable
    public Player player;

    public void initMiniPouch(int inventorySize, int effectSize, boolean isActiveInventory, boolean isActivateArmor, boolean isActivateOffhand, boolean isActivateCraft) {
        this.effectSize = effectSize;
        setStorageSize(inventorySize, player);

        setInventory(player, isActiveInventory);
        ((IMenuChangable) player.containerMenu).judgeInventoryHiding(player);

        setArmor(player, isActivateArmor);
        ((IMenuChangable) player.containerMenu).judgeArmorHiding(player);

        setOffhand(player, isActivateOffhand);
        ((IMenuChangable) player.containerMenu).judgeOffhandHiding(player);

        setCraft(player, isActivateCraft);
        ((IMenuChangable) player.containerMenu).judgeCraftHiding(player);
    }

    public void initServer(int inventorySize, int effectSize, boolean isActivateInventory, boolean isActivateArmor, boolean isActivateOffhand, boolean isActivateCraft) {
        initMiniPouch(inventorySize, effectSize, isActivateInventory, isActivateArmor, isActivateOffhand, isActivateCraft);
    }

    @Override
    @SubscribeEvent
    public void initClient(InventorySyncEvent e) {
        PlayerInventorySizeData data = e.getData();
        initMiniPouch(data.getInventorySize(),
                data.getEffectSize(),
                data.isActiveInventory(),
                data.isActivateArmor(),
                data.isActiveOffhand(),
                data.isActivateCraft());
    }


    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void oninit(Player p_35983_, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.register(this);

        inventorySize = Config.DEFAULT_SIZE.get();
        maxPage = (int)Math.max(Math.floor((inventorySize - 10) / 27f), 0);
        hotbarSize = Math.min(inventorySize, 9);

        items = LockableItemStackList.withSize(inventorySize, (Inventory) (Object) this, false);
        armor = LockableItemStackList.withSize(4, (Inventory) (Object) this, false);
        ((LockableItemStackList) armor).setObserver((detectItem) -> {
            // When there is a change in the list, this code is executed
            // Code to monitor the increase in slot enchantments.
            enchantSize = 0;
            armor.forEach(
                    (item) -> enchantSize += item.getEnchantmentLevel(ModRegistry.SLOT_ENCHANT.get())
            );
            updateStorageSize();
        });
        offhand = LockableItemStackList.withSize(1, (Inventory) (Object) this, false);

        compartments.add(0, items);
        compartments.add(1, armor);
        compartments.add(2, offhand);

        isActiveInventory = Config.DEFAULT_INVENTORY.get();
        isActiveArmor = Config.DEFAULT_ARMOR.get();
        isActiveOffhand = Config.DEFAULT_OFFHAND.get();
        isActiveCraft = Config.DEFAULT_CRAFT.get();
    }


    @Inject(method = "getSlotWithRemainingSpace(Lnet/minecraft/world/item/ItemStack;)I", at = @At(value = "HEAD"), cancellable = true)
    public void onGetRemainingSpace(ItemStack p_36051_, CallbackInfoReturnable<Integer> cir) {
        if(this.hasRemainingSpaceForItem(this.getItem(this.selected), p_36051_)) {
            cir.setReturnValue(this.selected);
        } else if(this.hasRemainingSpaceForItem(this.getItem(40), p_36051_)) {
            cir.setReturnValue(40);
        } else {
            for(int i = 0; i < this.items.size(); ++i) {
                if(this.hasRemainingSpaceForItem(this.items.get(i), p_36051_)) {
                    if(i < 36) cir.setReturnValue(i);
                    // Added slots are detected as free space
                    else cir.setReturnValue(i + 5);
                }
            }
        }
    }

    @Inject(method = "getFreeSlot()I", at = @At(value = "HEAD"), cancellable = true)
    public void onGetFreeSlot(CallbackInfoReturnable<Integer> cir) {
        for(int i = 0; i < this.items.size(); ++i) {
            if(this.items.get(i).isEmpty() && !((LockableItemStackList) items).lockList.get(i)) {
                if(i < 36) cir.setReturnValue(i);
                // Added slots are detected as free space
                else cir.setReturnValue(i + 5);
            }
        }
        if(Objects.isNull(cir.getReturnValue())) cir.setReturnValue(-1);
    }


    @Override
    public void setInventory(Player player, boolean change) {
        if(change != isActiveInventory) toggleInventory(player);
    }


    @Override
    public void toggleInventory(Player player) {
        if (Config.FORCE_INVENTORY.get() && this.isActiveInventory == Config.DEFAULT_INVENTORY.get()) return;

        setArmor(this.player, false);
        setCraft(this.player, false);
        setStorageSize(1, this.player);

        isActiveInventory = !isActiveInventory;

        ((IMenuChangable) this.player.containerMenu).judgeInventoryHiding(this.player);
    }

    @Override
    public void setArmor(Player player, boolean change) {
        if(change != isActiveArmor) toggleArmor(this.player);
    }


    @Override
    public void toggleArmor(Player player) {
        if (Config.FORCE_ARMOR.get() && this.isActiveArmor == Config.DEFAULT_ARMOR.get()) return;

        if(this.isActiveArmor) {
            // Scatter out what remains on the old list.
            for(ItemStack item : armor) {
                Level level = player.level;
                ItemEntity itementity = new ItemEntity(level, player.getX(), player.getEyeY() - 0.3, player.getZ(), item);
                itementity.setDefaultPickUpDelay();
                itementity.setThrower(player.getUUID());
                level.addFreshEntity(itementity);
            }
            ((LockableItemStackList)armor).allLock();
            this.isActiveArmor = false;
            return;
        }
        ((LockableItemStackList)armor).allOpen();
        this.isActiveArmor = true;
        ((IMenuChangable) player.containerMenu).judgeArmorHiding(player);
    }

    @Override
    public void setOffhand(Player player, boolean change) {
        if(change != isActiveOffhand) toggleOffhand(player);
    }

    @Override
    public void toggleOffhand(Player player) {
        if (Config.FORCE_OFFHAND.get() && this.isActiveOffhand == Config.DEFAULT_OFFHAND.get()) return;

        if(this.isActiveOffhand) {
            // Scatter out what remains on the old list.
            for(ItemStack item : offhand) {
                Level level = player.level;
                ItemEntity itementity = new ItemEntity(level, player.getX(), player.getEyeY() - 0.3, player.getZ(), item);
                itementity.setDefaultPickUpDelay();
                itementity.setThrower(player.getUUID());
                level.addFreshEntity(itementity);
            }
            ((LockableItemStackList)offhand).allLock();
            this.isActiveOffhand = false;
        } else
        {
            ((LockableItemStackList)offhand).allOpen();
            this.isActiveOffhand = true;
            ((IMenuChangable) player.containerMenu).judgeOffhandHiding(player);
        }
    }

    @Override
    public void setCraft(Player player, boolean change) {
        if(change != isActiveCraft) toggleCraft(player);
    }

    @Override
    public void toggleCraft(Player player) {
        if (Config.FORCE_CRAFT.get() && this.isActiveCraft == Config.DEFAULT_CRAFT.get()) return;

        isActiveCraft = !isActiveCraft;
        ((IMenuChangable) player.inventoryMenu).judgeCraftHiding(player);
        ((IMenuChangable) player.containerMenu).judgeCraftHiding(player);
        ((ICraftingContainerChangable) player.inventoryMenu.getCraftSlots()).toggleCraft(player);
    }

    @Override
    public boolean isActiveInventory() {
        return this.isActiveInventory;
    }

    @Override
    public boolean isActiveArmor() {
        return this.isActiveArmor;
    }

    @Override
    public boolean isActiveOffhand() {
        return this.isActiveOffhand;
    }

    @Override
    public boolean isActiveCraft() {
        return this.isActiveCraft;
    }

    @Override
    public void setStorageSize(int change, Player player) {
        changeStorageSize(change - inventorySize, player);
    }


    @Override
    public void changeStorageSize(int change, Player player) {
        if (Config.FORCE_SIZE.get() && this.inventorySize == Config.MAX_SIZE.get()) return;

        inventorySize += change;
        LockableItemStackList newItems;
        if(inventorySize < 1) inventorySize = 1;
        if(inventorySize >= Config.MAX_SIZE.get()) inventorySize = Config.MAX_SIZE.get();

        hotbarSize = 9;
        int allSize = (inventorySize + effectSize + enchantSize);

        if(allSize < 9) {
            hotbarSize = allSize;
            if(selected > allSize) selected = allSize - 1;
        }

        int newMaxPage = (int)Math.max(Math.floor((allSize - 10) / 27f), 0);

        // When the number of pages remains the same
        if (maxPage == newMaxPage)
        {
            int decrements = ((maxPage+1)*27 + 9) - allSize;
            ((LockableItemStackList)items).allOpen();
            for (int i=0; i<decrements; i++)
            {
                ((LockableItemStackList)items).lock(items.size()-1-i);
            }
        }
        // When the number of pages changes
        else
        {
            maxPage = newMaxPage;

            newItems = LockableItemStackList.withSize((maxPage+1)*27 + 9, (Inventory) (Object) this, false);

            int decrements = ((maxPage+1)*27 + 9) - allSize;
            for (int i=0; i<decrements; i++)
            {
                ((LockableItemStackList)items).lock(items.size()-1-i);
            }

            // Transfer items to the new list and scatter out what remains on the old list.
            for(int i = 0; i < (Math.min(items.size(), newItems.size())); i++) {
                newItems.set(i, items.get(i));
                items.set(i, ItemStack.EMPTY);
            }
            for(ItemStack item : items) {
                if(item == ItemStack.EMPTY) continue;
                Level level = player.level;
                ItemEntity itementity = new ItemEntity(level, player.getX(), player.getEyeY() - 0.3, player.getZ(), item);
                itementity.setDefaultPickUpDelay();
                itementity.setThrower(player.getUUID());
                level.addFreshEntity(itementity);
            }
            synchronized(compartments) {
                compartments.remove(items);
                items = newItems;
                compartments.add(0, items);
            }
        }
        ((IMenuChangable) player.containerMenu).judgePageReduction(change, getMaxPage(), player);
    }

    @Override
    public void updateStorageSize() {
        // 0 argument can be used to update the number of slots for enchantments and effects.
        changeStorageSize(0, player);
    }

    @Override
    public void changeEffectSize(int change) {
        // Server-side effect slots are handled here
        synchronized(compartments) {
            this.effectSize = change;
            updateStorageSize();
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void syncEffectSizeToClient(ClientEffectSlotSyncEvent e) {
        // Client-side effect slots are handled here
        synchronized(compartments) {
            this.effectSize = e.getEffectSize();
            updateStorageSize();
        }
    }

    @Override
    public boolean isValidSlot(int id) {
        // 0-35 are vanilla item slots.
        if(id < 36) {
            return !((LockableItemStackList) items).lockList.get(id);
        }
        // 36-39 are vanilla armor slots.
        else if(id < 40) {
            return !((LockableItemStackList) armor).lockList.get(id - 36);
        }
        // 40 is vanilla offhand slot.
        else if(id == 40) {
            return !((LockableItemStackList) offhand).lockList.get(0);
        }
        // 41 and above are additional slots.
        // To avoid id collisions, this mod treats the id as the sum of 5(armor+offhand).
        else {
            return !((LockableItemStackList) items).lockList.get(id - 5);
        }
    }

    @Inject(method = "swapPaint(D)V", at = @At(value = "HEAD"), cancellable = true)
    public void onSwapaint(double p_35989_, CallbackInfo ci) {
        // When the hot bar is scrolled
        int i = (int) Math.signum(p_35989_);
        for(this.selected -= i; this.selected < 0; this.selected += hotbarSize) {}

        while(this.selected >= hotbarSize) {
            this.selected -= hotbarSize;
        }
        ci.cancel();
    }

    @Override
    public int getMaxPage() {
        return maxPage;
    }

    @Override
    public int getInventorySize() {
        return (inventorySize + effectSize + enchantSize);
    }

    @Override
    public int getHotbarSize() {
        return hotbarSize;
    }

    @Override
    public PlayerInventorySizeData getAllData() {
        return new PlayerInventorySizeData(inventorySize, effectSize, isActiveInventory, isActiveArmor, isActiveOffhand, isActiveCraft);
    }

    @Inject(method = "setItem(ILnet/minecraft/world/item/ItemStack;)V", at = @At(value = "HEAD"), cancellable = true)
    public void onSetItem(int id, ItemStack itemStack, CallbackInfo ci) {
        synchronized(compartments) {
            // 0-35 are vanilla item slots.
            if(id < 36) {
                if(id + 1 > items.size()) ci.cancel();
                else {
                    items.set(id, itemStack);
                }
                ci.cancel();
            }
            // 36-39 are vanilla armor slots.
            else if(id < 40) {
                if(id - 35 > armor.size()) ci.cancel();
                else {
                    armor.set(id - 36, itemStack);
                }
                ci.cancel();
            }
            // 40 is vanilla offhand slot.
            else if(id == 40) {
                if(id - 39 > offhand.size()) ci.cancel();
                else {
                    offhand.set(0, itemStack);
                }
                ci.cancel();
            }
            // 41 and above are additional slots.
            // To avoid id collisions, this mod treats the id as the sum of 5(armor+offhand).
            else {
                if(id - 40 > items.size()) ci.cancel();
                else {
                    items.set(id - 5, itemStack);
                }
                ci.cancel();
            }
        }
    }

    @Inject(method = "getItem(I)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "HEAD"), cancellable = true)
    public void onGetItem(int id, CallbackInfoReturnable<ItemStack> cir) {

        synchronized(compartments) {
            // 0-35 are vanilla item slots.
            if(id < 36) {
                if(id + 1 > items.size()) cir.setReturnValue(ItemStack.EMPTY);
                else {
                    cir.setReturnValue(items.get(id));
                }
            }
            // 36-39 are vanilla armor slots.
            else if(id < 40) {
                if(id - 35 > armor.size()) cir.setReturnValue(ItemStack.EMPTY);
                else {
                    cir.setReturnValue(armor.get(id - 36));
                }
            }
            // 40 is vanilla offhand slot.
            else if(id == 40) {
                if(id - 39 > offhand.size()) cir.setReturnValue(ItemStack.EMPTY);
                else {
                    cir.setReturnValue(offhand.get(0));
                }
            }
            // 41 and above are additional slots.
            // To avoid id collisions, this mod treats the id as the sum of 5(armor+offhand).
            else {
                if(id - 40 > items.size()) cir.setReturnValue(ItemStack.EMPTY);
                else {
                    cir.setReturnValue(items.get(id - 5));
                }
            }
        }
    }


    @Inject(method = "removeItem(II)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "HEAD"), cancellable = true)
    public void onRemoveItem(int id, int decrement, CallbackInfoReturnable<ItemStack> cir) {
        synchronized(compartments) {
            // 0-35 are vanilla item slots.
            if(id < 36) {
                if(id + 1 > items.size()) cir.setReturnValue(ItemStack.EMPTY);
                else if(!items.get(id).isEmpty()) {
                    cir.setReturnValue(ContainerHelper.removeItem(items, id, decrement));
                }
            }
            // 36-39 are vanilla armor slots.
            else if(id < 40) {
                if(id - 35 > armor.size()) cir.setReturnValue(ItemStack.EMPTY);
                else if(!armor.get(id - 36).isEmpty()) {
                    cir.setReturnValue(ContainerHelper.removeItem(armor, id - 36, decrement));
                }
            }
            // 40 is vanilla offhand slot.
            else if(id == 40) {
                if(id - 39 > offhand.size()) cir.setReturnValue(ItemStack.EMPTY);
                else if(!offhand.get(0).isEmpty()) {
                    cir.setReturnValue(ContainerHelper.removeItem(offhand, 0, decrement));
                }
            }
            // 41 and above are additional slots.
            // To avoid id collisions, this mod treats the id as the sum of 5(armor+offhand).
            else {
                if(id - 40 > items.size()) cir.setReturnValue(ItemStack.EMPTY);
                else if(!items.get(id - 5).isEmpty()) {
                    cir.setReturnValue(ContainerHelper.removeItem(items, id - 5, decrement));
                }
            }
        }
    }

    @Inject(method = "save(Lnet/minecraft/nbt/ListTag;)Lnet/minecraft/nbt/ListTag;", at = @At(value = "HEAD"), cancellable = true)
    public void onSaveInventory(ListTag tags, CallbackInfoReturnable<ListTag> cir) {
        synchronized(compartments) {
            // In the original method, the armor and offhand lists conflict with the item list.
            for(int i = 0; i < 36; ++i) {
                if(!items.get(i).isEmpty()) {
                    CompoundTag compoundtag = new CompoundTag();
                    compoundtag.putByte("Slot", (byte) i);
                    items.get(i).save(compoundtag);
                    tags.add(compoundtag);
                }
            }
            for(int j = 0; j < this.armor.size(); ++j) {
                if(!armor.get(j).isEmpty()) {
                    CompoundTag compoundtag1 = new CompoundTag();
                    compoundtag1.putByte("Slot", (byte) (j + 100));
                    armor.get(j).save(compoundtag1);
                    tags.add(compoundtag1);
                }
            }
            for(int k = 0; k < this.offhand.size(); ++k) {
                if(!offhand.get(k).isEmpty()) {
                    CompoundTag compoundtag2 = new CompoundTag();
                    compoundtag2.putByte("Slot", (byte) (k + 150));
                    offhand.get(k).save(compoundtag2);
                    tags.add(compoundtag2);
                }
            }
        }
        cir.setReturnValue(tags);
    }

    @Inject(method = "load(Lnet/minecraft/nbt/ListTag;)V", at = @At(value = "HEAD"), cancellable = true)
    public void onLoadInventory(ListTag tags, CallbackInfo ci) {
        synchronized(compartments) {
            items.clear();
            armor.clear();
            offhand.clear();
            for(int i = 0; i < tags.size(); ++i) {
                CompoundTag compoundtag = tags.getCompound(i);
                int j = compoundtag.getByte("Slot") & 255;
                ItemStack itemstack = ItemStack.of(compoundtag);
                if(!itemstack.isEmpty()) {
                    // In the original method, the armor and offhand lists conflict with the item list.
                    if(j < 36) {
                        items.set(j, itemstack);
                    } else if(j >= 100 && j < armor.size() + 100) {
                        armor.set(j - 100, itemstack);
                    } else if(j >= 150 && j < offhand.size() + 150) {
                        offhand.set(j - 150, itemstack);
                    }
                }
            }
        }
        ci.cancel();
    }


    @Override
    public ListTag saveAdditional(ListTag tag) {
        // Save added slots (when there are 37 slots or more)
        for(int i = 36; i < items.size(); ++i) {
            if(!items.get(i).isEmpty()) {
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
        // Load added slots (when there are 37 slots or more)
        for(int i = 0; i < tag.size(); ++i) {
            CompoundTag compoundtag = tag.getCompound(i);
            int slotNumber = compoundtag.getInt("Slot");
            ItemStack itemstack = ItemStack.of(compoundtag);
            if(!itemstack.isEmpty()) {
                if(slotNumber < items.size()) {
                    items.set(slotNumber, itemstack);
                }
            }
        }
    }

    @Override
    public CompoundTag saveStatus(CompoundTag tag) {
        tag.putInt(    "inventorysize", inventorySize);
        tag.putInt(    "effectsize"   , effectSize);
        tag.putBoolean("inventory"    , isActiveInventory);
        tag.putBoolean("armor"        , isActiveArmor);
        tag.putBoolean("offhand"      , isActiveOffhand);
        tag.putBoolean("craft"        , isActiveCraft);

        initServer(inventorySize, effectSize, isActiveInventory, isActiveArmor, isActiveOffhand, isActiveCraft);
        ((IMenuSynchronizer) player.containerMenu).initMenu(new PlayerInventorySizeData(inventorySize, effectSize, isActiveInventory, isActiveArmor, isActiveOffhand, isActiveCraft));
        return tag;
    }

    @Override
    public void loadStatus(CompoundTag tag) {
        int effectSize = tag.contains("effectsize") ? tag.getInt("effectsize") : 0;

        int inventorySize;
        if (Config.FORCE_SIZE.get()) {
            inventorySize = Config.MAX_SIZE.get();
        }
        else if(tag.contains("inventorysize"))
        {
            int size = tag.getInt("inventorysize");
            if(size > Config.MAX_SIZE.get()) {
                inventorySize = Config.MAX_SIZE.get();
            }
            else {
                inventorySize = size;
            }
        }
        else
        {
            inventorySize = Math.min(Config.DEFAULT_SIZE.get(), Config.MAX_SIZE.get());
        }

        boolean isActivateInventory = Config.FORCE_INVENTORY.get()  ? Config.DEFAULT_INVENTORY.get()    :
                                      tag.contains("inventory")     ? tag.getBoolean("inventory") : Config.DEFAULT_INVENTORY.get();
        boolean isActivateArmor     = Config.FORCE_ARMOR.get()      ? Config.DEFAULT_ARMOR.get()        :
                                      tag.contains("armor")         ? tag.getBoolean("armor")     : Config.DEFAULT_ARMOR.get();
        boolean isActivateOffhand   = Config.FORCE_OFFHAND.get()    ? Config.DEFAULT_OFFHAND.get()      :
                                      tag.contains("offhand")       ? tag.getBoolean("offhand")   : Config.DEFAULT_OFFHAND.get();
        boolean isActivateCraft     = Config.FORCE_CRAFT.get()      ? Config.DEFAULT_CRAFT.get()        :
                                      tag.contains("craft")         ? tag.getBoolean("craft")     : Config.DEFAULT_CRAFT.get();

        initServer(inventorySize, effectSize, isActivateInventory, isActivateArmor, isActivateOffhand, isActivateCraft);
        ((IMenuSynchronizer) player.containerMenu).initMenu(new PlayerInventorySizeData(inventorySize, effectSize, isActivateInventory, isActivateArmor, isActivateOffhand, isActivateCraft));
    }
}