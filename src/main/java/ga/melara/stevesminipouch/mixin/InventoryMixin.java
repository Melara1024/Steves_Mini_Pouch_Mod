package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.Config;
import ga.melara.stevesminipouch.ModRegistry;
import ga.melara.stevesminipouch.event.ClientEffectSlotSyncEvent;
import ga.melara.stevesminipouch.event.InitMenuEvent;
import ga.melara.stevesminipouch.event.InventorySyncEvent;
import ga.melara.stevesminipouch.stats.InventoryStatsData;
import ga.melara.stevesminipouch.stats.InventorySyncPacket;
import ga.melara.stevesminipouch.stats.Messager;
import ga.melara.stevesminipouch.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.tags.TagKey;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static ga.melara.stevesminipouch.StevesMiniPouch.LOGGER;


@Mixin(Inventory.class)
public abstract class InventoryMixin implements ICustomInventory, IAdditionalDataHandler, IInheritGuard {


    private int inventorySize = Math.min(Config.DEFAULT_SIZE.get(), Config.MAX_SIZE.get());

    private int enchantSize = 0;

    private int effectSize = 0;
    private int hotbarSize = Math.min(inventorySize, 9);

    private int maxPage = (int) Math.max(Math.floor((inventorySize - 10) / 27f), 0);


    private boolean isActiveInventory = Config.DEFAULT_INVENTORY.get();
    private boolean isActiveArmor = Config.DEFAULT_ARMOR.get();
    private boolean isActiveOffhand = Config.DEFAULT_OFFHAND.get();
    private boolean isActiveCraft = Config.DEFAULT_CRAFT.get();


    // The inventory status is changed by rewriting the item list.
    // If this is overwritten by another mod, a conflict will occur.
    @Shadow
    public NonNullList<ItemStack> items;
    @Shadow
    public NonNullList<ItemStack> armor;
    @Shadow
    public NonNullList<ItemStack> offhand;

    @Shadow
    public int selected;


    // Compartments operations are prone to thread collisions and require synchronized.
    @Final
    @Shadow
    @Mutable
    private List<NonNullList<ItemStack>> compartments = new CopyOnWriteArrayList<>() {
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
    public Player player;

    private boolean avoidMiniPouch = true;
    private boolean decided = false;

    @Override
    public boolean avoidMiniPouch() {
        // Fixme Worst Code
        // Fixme Must be Rewrite

        // Mixin vanilla player inventory only, ignoring subclasses added by other mods
        if(!decided) {

            //Avoid custom inventory for other mods that inherit inventory
            ArrayList<String> classList = new ArrayList<>(Arrays.asList(
                    "net.minecraft.world.entity.player.Inventory",
                    "net.sistr.littlemaidrebirth.entity.LMInventorySupplier$LMInventory")) {
            };

            //Avoid entities of other mods that inherit the player
            ArrayList<String> playerList = new ArrayList<>(Arrays.asList(
                    "net.minecraft.client.player.LocalPlayer",
                    "net.minecraft.server.level.ServerPlayer")) {
            };

            if(Objects.nonNull(this.getClass()) && Objects.nonNull(this.player)) {
                Optional<String> playerName = Optional.ofNullable(this.player.getClass().getName());
                Optional<String> className = Optional.ofNullable(this.getClass().getName());

                if(playerName.isPresent() && className.isPresent())
                    avoidMiniPouch = !(playerList.contains(playerName.get()) && classList.contains(className.get()));
                else avoidMiniPouch = false;

                if(avoidMiniPouch) LOGGER.warn(className + " is not compatible with Steve's Mini Pouch.");
                else LOGGER.info("Steve's Mini Pouch correctly applied to " + className);

                decided = true;
            }
        }
        return avoidMiniPouch;
    }

    @Override
    public void initMiniPouch(InventoryStatsData stats) {
        this.effectSize = stats.getEffectSize();
        setStorageSize(stats.getInventorySize());
        setInventory(stats.isActiveInventory());
        setArmor(stats.isActiveArmor());
        setOffhand(stats.isActiveOffhand());
        setCraft(stats.isActiveCraft());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @OnlyIn(Dist.CLIENT)
    public void initClient(InventorySyncEvent e) {

        //syncが起動するまで一回もinitClientが呼ばれていない！！！！

        initMiniPouch(e.getData());
    }

    @Override
    public void initServer(InventoryStatsData stats) {
        initMiniPouch(stats);
    }

    // Pouch backup for ability to retain items after death
    public NonNullList<ItemStack> backUpPouch;

    @Override
    public NonNullList<ItemStack> getBackUpPouch() {
        return backUpPouch;
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void oninit(Player p_35983_, CallbackInfo ci) {
        if(avoidMiniPouch()) {
            inventorySize = 36;
            enchantSize = 0;
            effectSize = 0;
            hotbarSize = 9;

            maxPage = 0;

            isActiveInventory = true;
            isActiveArmor = true;
            isActiveOffhand = true;
            isActiveCraft = true;
        }

        if(Objects.nonNull(player)) MinecraftForge.EVENT_BUS.register(this);

        // When the player first enters the world, it will be initialized according to the Config values.
        items = LockableItemStackList.withSize((maxPage + 1) * 27 + 9, (Inventory) (Object) this, false);
        backUpPouch = NonNullList.withSize(items.size(), ItemStack.EMPTY);
        int decrements = ((maxPage + 1) * 27 + 9) - inventorySize;
        for(int i = items.size(); i > (items.size() - decrements); i--) {
            if(items.size() > 0) ((LockableItemStackList) items).lock(i - 1);
        }
        ((LockableItemStackList) items).setObserver((id, detectItem) -> {
            backUpPouch.set(id, detectItem);
        });

        armor = LockableItemStackList.withSize(4, (Inventory) (Object) this, !isActiveArmor);
        ((LockableItemStackList) armor).setObserver((id, detectItem) -> {
            // When there is a change in the list, this code is executed
            // Code to monitor the increase in slot enchantments.
            int oldEnchantSize = enchantSize;
            enchantSize = 0;
            armor.forEach(
                    (item) -> enchantSize += item.getEnchantmentLevel(ModRegistry.SLOT_ENCHANT.get())
            );
            if(enchantSize != oldEnchantSize) updateStorageSize();
        });
        offhand = LockableItemStackList.withSize(1, (Inventory) (Object) this, !isActiveOffhand);

        compartments.add(0, items);
        compartments.add(1, armor);
        compartments.add(2, offhand);

        if(Objects.nonNull(player) && Objects.nonNull(player.containerMenu))
            ((IMenuSynchronizer) player.containerMenu).setdataToClient(getAllData());
    }

    @SubscribeEvent
    public void onInitMenu(InitMenuEvent e) {
        AbstractContainerMenu menu = e.getMenu();
        if(Objects.isNull(this.player)) return;
        if(Objects.isNull(player.containerMenu)) return;
        if(!(menu.containerId == this.player.containerMenu.containerId)) return;

        ((IMenuSynchronizer) player.containerMenu).setdataToClient(getAllData());
    }

    @Inject(method = "getSlotWithRemainingSpace(Lnet/minecraft/world/item/ItemStack;)I", at = @At(value = "HEAD"), cancellable = true)
    public void onGetRemainingSpace(ItemStack itemStack, CallbackInfoReturnable<Integer> cir) {
        if(!avoidMiniPouch()) {
            if(this.hasRemainingSpaceForItem(this.getItem(this.selected), itemStack)) {
                cir.setReturnValue(this.selected);
            } else if(this.hasRemainingSpaceForItem(this.getItem(40), itemStack)) {
                cir.setReturnValue(40);
            } else {
                for(int i = 0; i < this.items.size(); ++i) {
                    if(this.hasRemainingSpaceForItem(this.items.get(i), itemStack)) {
                        if(i < 36) cir.setReturnValue(i);
                            // Added slots are detected as free space
                        else cir.setReturnValue(i + 5);
                    }
                }
            }
        }
    }

    @Inject(method = "getFreeSlot()I", at = @At(value = "HEAD"), cancellable = true)
    public void onGetFreeSlot(CallbackInfoReturnable<Integer> cir) {
        if(!avoidMiniPouch()) {
            for(int i = 0; i < this.items.size(); ++i) {
                if(this.items.get(i).isEmpty() && !((LockableItemStackList) items).lockList.get(i)) {
                    if(i < 36) cir.setReturnValue(i);
                        // Added slots are detected as free space
                    else cir.setReturnValue(i + 5);
                }
            }
            if(Objects.isNull(cir.getReturnValue())) cir.setReturnValue(-1);
        }
    }


    @Override
    public void setInventory(boolean change) {
        boolean setFlag = change;
        if(Config.FORCE_INVENTORY.get()) setFlag = Config.DEFAULT_INVENTORY.get();
        if(avoidMiniPouch()) setFlag = true;

        if(!setFlag) {
            setArmor(false);
            setCraft(false);
            setStorageSize(1);
        }
        this.isActiveInventory = setFlag;
        if(Objects.isNull(player)) return;
        ((IMenuChangable) this.player.containerMenu).updateInventoryHiding(this.player);
    }

    @Override
    public void toggleInventory() {
        setInventory(!this.isActiveInventory);
    }

    @Override
    public void setArmor(boolean change) {
        boolean setFlag = change;
        if(Config.FORCE_ARMOR.get()) setFlag = Config.DEFAULT_ARMOR.get();
        if(avoidMiniPouch()) setFlag = true;

        if(setFlag)
            ((LockableItemStackList) armor).allOpen();
        else
            ((LockableItemStackList) armor).allLock();

        this.isActiveArmor = setFlag;
        if(Objects.isNull(player)) return;
        ((IMenuChangable) player.containerMenu).updateArmorHiding(player);
    }


    @Override
    public void toggleArmor() {
        setArmor(!this.isActiveArmor);
    }

    @Override
    public void setOffhand(boolean change) {
        boolean setFlag = change;
        if(Config.FORCE_OFFHAND.get()) setFlag = Config.DEFAULT_OFFHAND.get();
        if(avoidMiniPouch()) setFlag = true;

        if(setFlag)
            ((LockableItemStackList) offhand).allOpen();
        else
            ((LockableItemStackList) offhand).allLock();

        this.isActiveOffhand = setFlag;
        if(Objects.isNull(player)) return;
        ((IMenuChangable) player.containerMenu).updateOffhandHiding(player);
    }

    @Override
    public void toggleOffhand() {
        setOffhand(!this.isActiveOffhand);
    }

    @Override
    public void setCraft(boolean change) {
        boolean setFlag = change;
        if(Config.FORCE_CRAFT.get()) setFlag = Config.DEFAULT_CRAFT.get();
        if(avoidMiniPouch()) setFlag = true;

        this.isActiveCraft = setFlag;
        if(Objects.isNull(player)) return;
        ((IMenuChangable) player.inventoryMenu).updateCraftHiding(player);
        ((IMenuChangable) player.containerMenu).updateCraftHiding(player);
        ((ICraftingContainerChangable) player.inventoryMenu.getCraftSlots()).setCraft(this.isActiveCraft, player);
    }

    @Override
    public void toggleCraft() {
        setCraft(!this.isActiveCraft);
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
    public void setStorageSize(int change) {
        changeStorageSize(change - inventorySize);
    }


    @Override
    public void changeStorageSize(int change) {
        if(Config.FORCE_SIZE.get() || inventorySize + change > Config.MAX_SIZE.get()) {
            inventorySize = Config.MAX_SIZE.get();
        } else {
            inventorySize = Math.max(inventorySize + change, 1);
        }
        if(avoidMiniPouch()) {
            inventorySize = 36;
            effectSize = 0;
            enchantSize = 0;
        }

        int allSize = getInventorySize();
        if(allSize < 9) {
            hotbarSize = allSize;
            if(selected > allSize) selected = allSize - 1;
        } else {
            hotbarSize = 9;
        }

        int newMaxPage = (int) Math.max(Math.floor((allSize - 10) / 27f), 0);

        // When the number of pages remains the same
        if(maxPage == newMaxPage) {
            int decrements = ((maxPage + 1) * 27 + 9) - allSize;
            ((LockableItemStackList) items).allOpen();
            for(int i = items.size(); i > items.size() - decrements; i--) {
                if(items.size() > 0) ((LockableItemStackList) items).lock(i - 1);
            }
        }
        // When the number of pages changes
        else {
            maxPage = newMaxPage;
            LockableItemStackList newItems = LockableItemStackList.withSize((maxPage + 1) * 27 + 9, (Inventory) (Object) this, false);
            NonNullList<ItemStack> newBackUpPouch = NonNullList.withSize(newItems.size(), ItemStack.EMPTY);
            newItems.setObserver((id, detectItem) -> {
                newBackUpPouch.set(id, detectItem);
            });
            int decrements = ((maxPage + 1) * 27 + 9) - allSize;
            for(int i = newItems.size(); i > newItems.size() - decrements; i--) {
                if(newItems.size() > 0) newItems.lock(i - 1);
            }
            // Transfer items to the new list and scatter out what remains on the old list.
            for(int i = 0; i < (Math.min(items.size(), newItems.size())); i++) {
                newItems.set(i, items.get(i));
                items.set(i, ItemStack.EMPTY);
            }
            ((LockableItemStackList) items).allLock();
            synchronized(compartments) {
                compartments.remove(items);
                items = newItems;
                backUpPouch = newBackUpPouch;
                compartments.add(0, items);
            }
        }

        if(Objects.isNull(player)) return;
        ((IMenuChangable) player.containerMenu).judgePageReduction(getMaxPage(), player);
        ((IMenuChangable) player.inventoryMenu).judgePageReduction(getMaxPage(), player);
    }

    @Override
    public void updateStorageSize() {
        // 0 argument can be used to update the number of slots for enchantments and effects.
        changeStorageSize(0);
    }

    @Override
    public void changeEffectSize(int change) {
        // Server-side effect slots are handled here
        synchronized(compartments) {
            int oldEffectSize = effectSize;
            effectSize = change;
            if(effectSize != oldEffectSize) updateStorageSize();
        }
    }

    @Inject(method = "swapPaint(D)V", at = @At(value = "HEAD"), cancellable = true)
    public void onSwapaint(double direction, CallbackInfo ci) {

        if(!avoidMiniPouch()) {
            // When the hot bar is scrolled
            int i = (int) Math.signum(direction);
            for(this.selected -= i; this.selected < 0; this.selected += hotbarSize) {
            }

            while(this.selected >= hotbarSize) {
                this.selected -= hotbarSize;
            }
            ci.cancel();
        }
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
    public int getBaseSize() {
        return inventorySize;
    }

    @Override
    public int getEffectSize() {
        return effectSize;
    }

    @Override
    public int getEnchantSize() {
        return enchantSize;
    }

    @Override
    public int getHotbarSize() {
        return hotbarSize;
    }

    @Override
    public InventoryStatsData getAllData() {
        return new InventoryStatsData(this.inventorySize, this.effectSize, this.isActiveInventory, this.isActiveArmor, this.isActiveOffhand, this.isActiveCraft);
    }

    @Override
    public boolean isValidSlot(int id) {
        if(avoidMiniPouch()) return true;
        synchronized(compartments) {
            // 0-35 are vanilla item slots.
            if(id < 36) return !((LockableItemStackList) items).lockList.get(id);
                // 36-39 are vanilla armor slots.
            else if(id < 40) return !((LockableItemStackList) armor).lockList.get(id - 36);
                // 40 is vanilla offhand slot.
            else if(id == 40) return !((LockableItemStackList) offhand).lockList.get(0);
                // 41 and above are additional slots.
                // To avoid id collisions, this mod treats the id as the sum of 5  = (armor+offhand).
            else return !((LockableItemStackList) items).lockList.get(id - 5);
        }
    }

    @Inject(method = "setItem(ILnet/minecraft/world/item/ItemStack;)V", at = @At(value = "HEAD"), cancellable = true)
    public void onSetItem(int id, ItemStack itemStack, CallbackInfo ci) {
        if(!avoidMiniPouch()) {
            synchronized(compartments) {
                // 0-35 are vanilla item slots.
                if(id < 36) {
                    items.set(id, itemStack);
                    ci.cancel();
                }
                // 36-39 are vanilla armor slots.
                else if(id < 40) {
                    armor.set(id - 36, itemStack);
                    ci.cancel();
                }
                // 40 is vanilla offhand slot.
                else if(id == 40) {
                    offhand.set(0, itemStack);
                    ci.cancel();
                }
                // 41 and above are additional slots.
                // To avoid id collisions, this mod treats the id as the sum of 5(armor+offhand).
                else {
                    items.set(id - 5, itemStack);
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "getItem(I)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "HEAD"), cancellable = true)
    public void onGetItem(int id, CallbackInfoReturnable<ItemStack> cir) {
        if(!avoidMiniPouch()) {
            synchronized(compartments) {

                // 0-35 are vanilla item slots.
                if(id < 36) cir.setReturnValue(items.get(id));
                    // 36-39 are vanilla armor slots.
                else if(id < 40) cir.setReturnValue(armor.get(id - 36));
                    // 40 is vanilla offhand slot.
                else if(id == 40) cir.setReturnValue(offhand.get(0));
                    // 41 and above are additional slots.
                    // To avoid id collisions, this mod treats the id as the sum of 5 = (armor+offhand).
                else cir.setReturnValue(items.get(id - 5));
            }
        }
    }


    @Inject(method = "removeItem(II)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "HEAD"), cancellable = true)
    public void onRemoveItem(int id, int decrement, CallbackInfoReturnable<ItemStack> cir) {
        if(!avoidMiniPouch()) {
            synchronized(compartments) {

                // 0-35 are vanilla item slots.
                if(id < 36) cir.setReturnValue(ContainerHelper.removeItem(items, id, decrement));
                    // 36-39 are vanilla armor slots.
                else if(id < 40) cir.setReturnValue(ContainerHelper.removeItem(armor, id - 36, decrement));
                    // 40 is vanilla offhand slot.
                else if(id == 40) cir.setReturnValue(ContainerHelper.removeItem(offhand, 0, decrement));
                    // 41 and above are additional slots.
                    // To avoid id collisions, this mod treats the id as the sum of 5 = (armor+offhand).
                else cir.setReturnValue(ContainerHelper.removeItem(items, id - 5, decrement));
            }
        }
    }

    @Inject(method = "save(Lnet/minecraft/nbt/ListTag;)Lnet/minecraft/nbt/ListTag;", at = @At(value = "HEAD"), cancellable = true)
    public void onSaveInventory(ListTag tags, CallbackInfoReturnable<ListTag> cir) {
        if(!avoidMiniPouch()) {
            // In the vanilla method, the armor and offhand lists conflict with the item list.
            synchronized(compartments) {
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
    }

    @Inject(method = "load(Lnet/minecraft/nbt/ListTag;)V", at = @At(value = "HEAD"), cancellable = true)
    public void onLoadInventory(ListTag tags, CallbackInfo ci) {
        if(!avoidMiniPouch()) {
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
    }


    @Override
    public ListTag saveAdditional(ListTag tag) {
        // Save added slots (when there are 37 slots or more)
        synchronized(compartments) {
            for(int i = 36; i < items.size(); ++i) {
                if(!items.get(i).isEmpty()) {
                    CompoundTag compoundtag = new CompoundTag();
                    compoundtag.putInt("Slot", i);
                    items.get(i).save(compoundtag);
                    tag.add(compoundtag);
                }
            }
        }
        return tag;
    }

    @Override
    public void loadAdditional(ListTag tag) {
        // Load added slots (when there are 37 slots or more)
        synchronized(compartments) {
            for(int i = 0; i < tag.size(); ++i) {
                CompoundTag compoundtag = tag.getCompound(i);
                int slotNumber = compoundtag.getInt("Slot");
                ItemStack itemstack = ItemStack.of(compoundtag);
                if(!itemstack.isEmpty() && slotNumber < items.size()) {
                    items.set(slotNumber, itemstack);
                }
            }
        }
    }

    @Override
    public CompoundTag saveStatus(CompoundTag tag) {
        tag.putInt("inventorysize", this.inventorySize);
        tag.putInt("effectsize", this.effectSize);
        tag.putBoolean("inventory", this.isActiveInventory);
        tag.putBoolean("armor", this.isActiveArmor);
        tag.putBoolean("offhand", this.isActiveOffhand);
        tag.putBoolean("craft", this.isActiveCraft);
        return tag;
    }

    @Override
    public void loadStatus(CompoundTag tag) {
        int effectSize = tag.contains("effectsize") ? tag.getInt("effectsize") : 0;

        int inventorySize = Math.min(Config.DEFAULT_SIZE.get(), Config.MAX_SIZE.get());
        if(tag.contains("inventorysize")) inventorySize = Math.min(Config.MAX_SIZE.get(), tag.getInt("inventorysize"));
        if(Config.FORCE_SIZE.get()) inventorySize = Config.MAX_SIZE.get();

        boolean isActiveInventory = Config.DEFAULT_INVENTORY.get();
        if(!Config.FORCE_INVENTORY.get() && tag.contains("inventory")) isActiveInventory = tag.getBoolean("inventory");

        boolean isActiveArmor = Config.DEFAULT_ARMOR.get();
        if(!Config.FORCE_INVENTORY.get() && tag.contains("armor")) isActiveArmor = tag.getBoolean("armor");

        boolean isActiveOffhand = Config.DEFAULT_OFFHAND.get();
        if(!Config.FORCE_OFFHAND.get() && tag.contains("offhand")) isActiveOffhand = tag.getBoolean("offhand");

        boolean isActiveCraft = Config.DEFAULT_CRAFT.get();
        if(!Config.FORCE_CRAFT.get() && tag.contains("craft")) isActiveCraft = tag.getBoolean("craft");

        InventoryStatsData stats = new InventoryStatsData(inventorySize, effectSize, isActiveInventory, isActiveArmor, isActiveOffhand, isActiveCraft);
        initServer(stats);
        if(Objects.nonNull(player) && Objects.nonNull(player.containerMenu))
            ((IMenuSynchronizer) player.containerMenu).setdataToClient(getAllData());
    }
}
