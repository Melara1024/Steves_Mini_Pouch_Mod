package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.Config;
import ga.melara.stevesminipouch.ModRegistry;
import ga.melara.stevesminipouch.event.ClientEffectSlotSyncEvent;
import ga.melara.stevesminipouch.event.InitMenuEvent;
import ga.melara.stevesminipouch.event.InventorySyncEvent;
import ga.melara.stevesminipouch.stats.PlayerInventorySizeData;
import ga.melara.stevesminipouch.util.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
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

import javax.management.ListenerNotFoundException;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static ga.melara.stevesminipouch.StevesMiniPouch.LOGGER;
import static net.minecraft.inventory.ItemStackHelper.removeItem;


@Mixin(PlayerInventory.class)
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
    private List<NonNullList<ItemStack>> compartments = new CopyOnWriteArrayList<NonNullList<ItemStack>>() {
        @Override
        public Iterator<NonNullList<ItemStack>> iterator() {
            synchronized (this) {
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
    public PlayerEntity player;



    @Shadow public abstract boolean contains(ItemStack pStack);


    private boolean avoidMiniPouch = true;
    private boolean decided = false;
    @Override
    public boolean avoidMiniPouch()
    {
        // Fixme Worst Code
        // Fixme Must be Rewrite

        // Mixin vanilla player inventory only, ignoring subclasses added by other mods
        if (!decided){

            //Avoid custom inventory for other mods that inherit inventory

            ArrayList<String> classList = new ArrayList<String>(Arrays.asList(
                    "net.minecraft.entity.player.PlayerInventory",
                    "net.sistr.littlemaidrebirth.entity.LMInventorySupplier$LMInventory")) {
            };

            //Avoid entities of other mods that inherit the player
            ArrayList<String> playerList = new ArrayList<String>(Arrays.asList(
                    "net.minecraft.client.entity.player.ClientPlayerEntity",
                    "net.minecraft.client.entity.player.RemoteClientPlayerEntity",
                    "net.minecraft.entity.player.ServerPlayerEntity")) {
            };

            Optional<String> playerName = Optional.ofNullable(this.player.getClass().getName());
            Optional<String> className = Optional.ofNullable(this.getClass().getName());

            if(playerName.isPresent() && className.isPresent()) avoidMiniPouch = !(playerList.contains(playerName.get()) && classList.contains(className.get()));
            else avoidMiniPouch = false;

            if (avoidMiniPouch) LOGGER.warn(className + " is not compatible with Steve's Mini Pouch.");
            else LOGGER.info("Steve's Mini Pouch correctly applied to " + className);

            decided = true;
        }
        return avoidMiniPouch;
    }

    public void initMiniPouch(int inventorySize, int effectSize, boolean isActiveInventory, boolean isActiveArmor, boolean isActiveOffhand, boolean isActiveCraft) {
        this.effectSize = effectSize;
        setStorageSize(inventorySize);

        IMenuChangable menu = (IMenuChangable) player.containerMenu;

        setInventory(isActiveInventory);
        menu.updateInventoryHiding(player);

        setArmor(isActiveArmor);
        menu.updateArmorHiding(player);

        setOffhand(isActiveOffhand);
        menu.updateOffhandHiding(player);

        setCraft(isActiveCraft);
        menu.updateCraftHiding(player);
    }

    public void initServer(int inventorySize, int effectSize, boolean isActiveInventory, boolean isActiveArmor, boolean isActiveOffhand, boolean isActiveCraft) {
        initMiniPouch(inventorySize, effectSize, isActiveInventory, isActiveArmor, isActiveOffhand, isActiveCraft);
    }

    @Override
    @SubscribeEvent
    public void initClient(InventorySyncEvent e) {
        PlayerInventorySizeData data = e.getData();
        initMiniPouch(data.getInventorySize(),
                data.getEffectSize(),
                data.isActiveInventory(),
                data.isActiveArmor(),
                data.isActiveOffhand(),
                data.isActiveCraft());
    }


    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void oninit(PlayerEntity p_35983_, CallbackInfo ci) {
        if (avoidMiniPouch())
        {
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

        MinecraftForge.EVENT_BUS.register(this);

        // When the player first enters the world, it will be initialized according to the Config values.
        items = LockableItemStackList.withSize((maxPage + 1) * 27 + 9, (PlayerInventory) (Object) this, false);
        int decrements = ((maxPage + 1) * 27 + 9) - inventorySize;
        for (int i = 0; i < decrements; i++) {
            if (items.size() > 0) ((LockableItemStackList) items).lock(items.size() - 1 - i);
        }

        armor = LockableItemStackList.withSize(4, (PlayerInventory) (Object) this, isActiveArmor);
        ((LockableItemStackList) armor).setObserver((detectItem) -> {
            // When there is a change in the list, this code is executed
            // Code to monitor the increase in slot enchantments.
            enchantSize = 0;
            armor.forEach(
                    (item) -> enchantSize += EnchantmentHelper.getItemEnchantmentLevel(ModRegistry.SLOT_ENCHANT.get(), item)
            );

            updateStorageSize();
        });
        offhand = LockableItemStackList.withSize(1, (PlayerInventory) (Object) this, isActiveOffhand);

        compartments.add(0, items);
        compartments.add(1, armor);
        compartments.add(2, offhand);

        if (Objects.nonNull(player.inventoryMenu)) {
            initServer(this.inventorySize, this.effectSize, this.isActiveInventory, this.isActiveArmor, this.isActiveOffhand, this.isActiveCraft);
            ((IMenuSynchronizer) this.player.containerMenu).initMenu(new PlayerInventorySizeData(this.inventorySize, this.effectSize, this.isActiveInventory, this.isActiveArmor, this.isActiveOffhand, this.isActiveCraft));
        }
    }

    @SubscribeEvent
    public void onInitMenu(InitMenuEvent e)
    {
        Container menu = e.getMenu();
        ((IMenuSynchronizer) menu).initMenu(new PlayerInventorySizeData(this.inventorySize, this.effectSize, this.isActiveInventory, this.isActiveArmor, this.isActiveOffhand, this.isActiveCraft));
    }


    @Inject(method = "getSlotWithRemainingSpace", at = @At(value = "HEAD"), cancellable = true)
    public void onGetRemainingSpace(ItemStack p_36051_, CallbackInfoReturnable<Integer> cir) {
        if (!avoidMiniPouch()){
            if (this.hasRemainingSpaceForItem(this.getItem(this.selected), p_36051_)) {
                cir.setReturnValue(this.selected);
            } else if (this.hasRemainingSpaceForItem(this.getItem(40), p_36051_)) {
                cir.setReturnValue(40);
            } else {
                for (int i = 0; i < this.items.size(); ++i) {
                    if (this.hasRemainingSpaceForItem(this.items.get(i), p_36051_)) {
                        if (i < 36) cir.setReturnValue(i);
                            // Added slots are detected as free space
                        else cir.setReturnValue(i + 5);
                    }
                }
            }
        }
    }

    @Inject(method = "getFreeSlot()I", at = @At(value = "HEAD"), cancellable = true)
    public void onGetFreeSlot(CallbackInfoReturnable<Integer> cir) {
        if (!avoidMiniPouch())
        {
            for (int i = 0; i < this.items.size(); ++i) {
                if (this.items.get(i).isEmpty() && !((LockableItemStackList) items).lockList.get(i)) {
                    if (i < 36) cir.setReturnValue(i);
                        // Added slots are detected as free space
                    else cir.setReturnValue(i + 5);
                }
            }
            if (Objects.isNull(cir.getReturnValue())) cir.setReturnValue(-1);
        }
    }


    @Override
    public void setInventory(boolean change) {
        boolean setFlag = change;
        if (Config.FORCE_INVENTORY.get()) setFlag = Config.DEFAULT_INVENTORY.get();
        if (avoidMiniPouch()) setFlag = true;

        if (!setFlag) {
            setArmor(false);
            setCraft(false);
            setStorageSize(1);
        }
        this.isActiveInventory = setFlag;
        ((IMenuChangable) this.player.containerMenu).updateInventoryHiding(this.player);
    }

    @Override
    public void toggleInventory() {
        setInventory(!this.isActiveInventory);
    }

    @Override
    public void setArmor(boolean change) {
        boolean setFlag = change;
        if (Config.FORCE_ARMOR.get()) setFlag = Config.DEFAULT_ARMOR.get();
        if (avoidMiniPouch()) setFlag = true;

        if (setFlag)
            ((LockableItemStackList) armor).allOpen();
        else
            ((LockableItemStackList) armor).allLock();

        this.isActiveArmor = setFlag;
        ((IMenuChangable) player.containerMenu).updateArmorHiding(player);
    }


    @Override
    public void toggleArmor() {
        setArmor(!this.isActiveArmor);
    }

    @Override
    public void setOffhand(boolean change) {
        boolean setFlag = change;
        if (Config.FORCE_OFFHAND.get()) setFlag = Config.DEFAULT_OFFHAND.get();
        if (avoidMiniPouch()) setFlag = true;

        if (setFlag)
            ((LockableItemStackList) offhand).allOpen();
        else
            ((LockableItemStackList) offhand).allLock();

        this.isActiveOffhand = setFlag;
        ((IMenuChangable) player.containerMenu).updateOffhandHiding(player);
    }

    @Override
    public void toggleOffhand() {
        setOffhand(!this.isActiveOffhand);
    }

    @Override
    public void setCraft(boolean change) {
        boolean setFlag = change;
        if (Config.FORCE_CRAFT.get()) setFlag = Config.DEFAULT_CRAFT.get();
        if (avoidMiniPouch()) setFlag = true;

        this.isActiveCraft = setFlag;
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

        System.out.println("change storage size");
        if (Config.FORCE_SIZE.get() || inventorySize + change > Config.MAX_SIZE.get()) {
            inventorySize = Config.MAX_SIZE.get();
        } else {
            inventorySize = Math.max(inventorySize + change, 1);
        }
        if (avoidMiniPouch()){
            inventorySize = 36;
            effectSize = 0;
            enchantSize = 0;
        }

        LockableItemStackList newItems;

        int allSize = (inventorySize + effectSize + enchantSize);
        if (allSize < 9) {
            hotbarSize = allSize;
            if (selected > allSize) selected = allSize - 1;
        } else {
            hotbarSize = 9;
        }

        int newMaxPage = (int) Math.max(Math.floor((allSize - 10) / 27f), 0);

        // When the number of pages remains the same
        if (maxPage == newMaxPage) {
            int decrements = ((maxPage + 1) * 27 + 9) - allSize;
            ((LockableItemStackList) items).allOpen();
            for (int i = 0; i < decrements; i++) {
                if (items.size() > 0) ((LockableItemStackList) items).lock(items.size() - 1 - i);
            }
        }
        // When the number of pages changes
        else {
            maxPage = newMaxPage;
            newItems = LockableItemStackList.withSize((maxPage + 1) * 27 + 9, (PlayerInventory) (Object) this, false);
            int decrements = ((maxPage + 1) * 27 + 9) - allSize;
            for (int i = 0; i < decrements; i++) {
                if (newItems.size() > 0) newItems.lock(newItems.size() - 1 - i);
            }

            // Transfer items to the new list and scatter out what remains on the old list.
            for (int i = 0; i < (Math.min(items.size(), newItems.size())); i++) {
                newItems.set(i, items.get(i));
                items.set(i, ItemStack.EMPTY);
            }
            for (ItemStack item : items) {
                if (item == ItemStack.EMPTY) continue;
                World level = player.level;
                ItemEntity itementity = new ItemEntity(level, player.getX(), player.getEyeY() - 0.3, player.getZ(), item);
                itementity.setDefaultPickUpDelay();
                itementity.setThrower(player.getUUID());
                level.addFreshEntity(itementity);
            }
            synchronized (compartments) {
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
        changeStorageSize(0);
    }

    @Override
    public void changeEffectSize(int change) {
        // Server-side effect slots are handled here

        System.out.println("change effect size");
        synchronized (compartments) {
            this.effectSize = change;
            updateStorageSize();
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void syncEffectSizeToClient(ClientEffectSlotSyncEvent e) {
        // Client-side effect slots are handled here

        System.out.println("change effect size");
        synchronized (compartments) {
            this.effectSize = e.getEffectSize();
            updateStorageSize();
        }
    }


    @Inject(method = "swapPaint(D)V", at = @At(value = "HEAD"), cancellable = true)
    public void onSwapaint(double direction, CallbackInfo ci) {

        if(!avoidMiniPouch()){
            // When the hot bar is scrolled
            int i = (int) Math.signum(direction);
            for (this.selected -= i; this.selected < 0; this.selected += hotbarSize) {
            }

            while (this.selected >= hotbarSize) {
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
    public int getBaseSize(){
        return inventorySize;
    }

    @Override
    public int getEffectSize(){
        return effectSize;
    }

    @Override
    public int getEnchantSize(){
        return enchantSize;
    }

    @Override
    public int getHotbarSize() {
        return hotbarSize;
    }

    @Override
    public PlayerInventorySizeData getAllData() {
        return new PlayerInventorySizeData(this.inventorySize, this.effectSize, this.isActiveInventory, this.isActiveArmor, this.isActiveOffhand, this.isActiveCraft);
    }

    @Override
    public boolean isValidSlot(int id) {
        if (avoidMiniPouch()) return true;
        // 0-35 are vanilla item slots.
        if (id < 36) {
            return !((LockableItemStackList) items).lockList.get(id);
        }
        // 36-39 are vanilla armor slots.
        else if (id < 40) {
            return !((LockableItemStackList) armor).lockList.get(id - 36);
        }
        // 40 is vanilla offhand slot.
        else if (id == 40) {
            return !((LockableItemStackList) offhand).lockList.get(0);
        }
        // 41 and above are additional slots.
        // To avoid id collisions, this mod treats the id as the sum of 5(armor+offhand).
        else {
            return !((LockableItemStackList) items).lockList.get(id - 5);
        }
    }

    @Inject(method = "setItem", at = @At(value = "HEAD"), cancellable = true)
    public void onSetItem(int id, ItemStack itemStack, CallbackInfo ci) {
        if (!avoidMiniPouch())
        {
            synchronized (compartments) {
                // 0-35 are vanilla item slots.
                if (id < 36) {
                    if (id + 1 > items.size()) ci.cancel();
                    else {
                        items.set(id, itemStack);
                    }
                    ci.cancel();
                }
                // 36-39 are vanilla armor slots.
                else if (id < 40) {
                    if (id - 35 > armor.size()) ci.cancel();
                    else {
                        armor.set(id - 36, itemStack);
                    }
                    ci.cancel();
                }
                // 40 is vanilla offhand slot.
                else if (id == 40) {
                    if (id - 39 > offhand.size()) ci.cancel();
                    else {
                        offhand.set(0, itemStack);
                    }
                    ci.cancel();
                }
                // 41 and above are additional slots.
                // To avoid id collisions, this mod treats the id as the sum of 5(armor+offhand).
                else {
                    if (id - 40 > items.size()) ci.cancel();
                    else {
                        items.set(id - 5, itemStack);
                    }
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "getItem", at = @At(value = "HEAD"), cancellable = true)
    public void onGetItem(int id, CallbackInfoReturnable<ItemStack> cir) {

        if (!avoidMiniPouch())
        {
            synchronized (compartments) {
                // 0-35 are vanilla item slots.
                if (id < 36) {
                    if (id + 1 > items.size()) cir.setReturnValue(ItemStack.EMPTY);
                    else {
                        cir.setReturnValue(items.get(id));
                    }
                }
                // 36-39 are vanilla armor slots.
                else if (id < 40) {
                    if (id - 35 > armor.size()) cir.setReturnValue(ItemStack.EMPTY);
                    else {
                        cir.setReturnValue(armor.get(id - 36));
                    }
                }
                // 40 is vanilla offhand slot.
                else if (id == 40) {
                    if (id - 39 > offhand.size()) cir.setReturnValue(ItemStack.EMPTY);
                    else {
                        cir.setReturnValue(offhand.get(0));
                    }
                }
                // 41 and above are additional slots.
                // To avoid id collisions, this mod treats the id as the sum of 5(armor+offhand).
                else {
                    if (id - 40 > items.size()) cir.setReturnValue(ItemStack.EMPTY);
                    else {
                        cir.setReturnValue(items.get(id - 5));
                    }
                }
            }
        }
    }


    @Inject(method = "removeItem*", at = @At(value = "HEAD"), cancellable = true)
    public void onRemoveItem(int id, int decrement, CallbackInfoReturnable<ItemStack> cir) {
        if (!avoidMiniPouch())
        {
            synchronized (compartments) {
                // 0-35 are vanilla item slots.
                if (id < 36) {
                    if (id + 1 > items.size()) cir.setReturnValue(ItemStack.EMPTY);
                    else if (!items.get(id).isEmpty()) {
                        cir.setReturnValue(removeItem(items, id, decrement));
                    }
                }
                // 36-39 are vanilla armor slots.
                else if (id < 40) {
                    if (id - 35 > armor.size()) cir.setReturnValue(ItemStack.EMPTY);
                    else if (!armor.get(id - 36).isEmpty()) {
                        cir.setReturnValue(removeItem(armor, id - 36, decrement));
                    }
                }
                // 40 is vanilla offhand slot.
                else if (id == 40) {
                    if (id - 39 > offhand.size()) cir.setReturnValue(ItemStack.EMPTY);
                    else if (!offhand.get(0).isEmpty()) {
                        cir.setReturnValue(removeItem(offhand, 0, decrement));
                    }
                }
                // 41 and above are additional slots.
                // To avoid id collisions, this mod treats the id as the sum of 5(armor+offhand).
                else {
                    if (id - 40 > items.size()) cir.setReturnValue(ItemStack.EMPTY);
                    else if (!items.get(id - 5).isEmpty()) {
                        cir.setReturnValue(removeItem(items, id - 5, decrement));
                    }
                }
            }
        }
    }

    @Inject(method = "save", at = @At(value = "HEAD"), cancellable = true)
    public void onSaveInventory(ListNBT tags, CallbackInfoReturnable<ListNBT> cir) {
        if(!avoidMiniPouch()){
            synchronized (compartments) {
                // In the original method, the armor and offhand lists conflict with the item list.
                for (int i = 0; i < 36; ++i) {
                    if (!items.get(i).isEmpty()) {
                        CompoundNBT compoundtag = new CompoundNBT();
                        compoundtag.putByte("Slot", (byte) i);
                        items.get(i).save(compoundtag);
                        tags.add(compoundtag);
                    }
                }
                for (int j = 0; j < this.armor.size(); ++j) {
                    if (!armor.get(j).isEmpty()) {
                        CompoundNBT compoundtag1 = new CompoundNBT();
                        compoundtag1.putByte("Slot", (byte) (j + 100));
                        armor.get(j).save(compoundtag1);
                        tags.add(compoundtag1);
                    }
                }
                for (int k = 0; k < this.offhand.size(); ++k) {
                    if (!offhand.get(k).isEmpty()) {
                        CompoundNBT compoundtag2 = new CompoundNBT();
                        compoundtag2.putByte("Slot", (byte) (k + 150));
                        offhand.get(k).save(compoundtag2);
                        tags.add(compoundtag2);
                    }
                }
            }
            cir.setReturnValue(tags);
        }
    }

    @Inject(method = "load", at = @At(value = "HEAD"), cancellable = true)
    public void onLoadInventory(ListNBT tags, CallbackInfo ci) {

        if(!avoidMiniPouch()){
            synchronized (compartments) {
                items.clear();
                armor.clear();
                offhand.clear();
                for (int i = 0; i < tags.size(); ++i) {
                    CompoundNBT compoundtag = tags.getCompound(i);
                    int j = compoundtag.getByte("Slot") & 255;
                    ItemStack itemstack = ItemStack.of(compoundtag);
                    if (!itemstack.isEmpty()) {
                        // In the original method, the armor and offhand lists conflict with the item list.
                        if (j < 36) {
                            items.set(j, itemstack);
                        } else if (j >= 100 && j < armor.size() + 100) {
                            armor.set(j - 100, itemstack);
                        } else if (j >= 150 && j < offhand.size() + 150) {
                            offhand.set(j - 150, itemstack);
                        }
                    }
                }
            }
            ci.cancel();
        }
    }


    @Override
    public ListNBT saveAdditional(ListNBT tag) {

        if(avoidMiniPouch()) return tag;

        // Save added slots (when there are 37 slots or more)
        for (int i = 36; i < items.size(); ++i) {
            if (!items.get(i).isEmpty()) {
                CompoundNBT compoundtag = new CompoundNBT();
                compoundtag.putInt("Slot", i);
                items.get(i).save(compoundtag);
                tag.add(compoundtag);
            }
        }
        return tag;
    }

    @Override
    public void loadAdditional(ListNBT tag) {

        if(avoidMiniPouch()) return;

        // Load added slots (when there are 37 slots or more)
        for (int i = 0; i < tag.size(); ++i) {
            CompoundNBT compoundtag = tag.getCompound(i);
            int slotNumber = compoundtag.getInt("Slot");
            ItemStack itemstack = ItemStack.of(compoundtag);
            if (!itemstack.isEmpty()) {
                if (slotNumber < items.size()) {
                    items.set(slotNumber, itemstack);
                }
            }
        }
    }

    @Override
    public CompoundNBT saveStatus(CompoundNBT tag) {

        if(avoidMiniPouch()) return tag;

        tag.putInt("inventorysize", this.inventorySize);
        tag.putInt("effectsize", this.effectSize);
        tag.putBoolean("inventory", this.isActiveInventory);
        tag.putBoolean("armor", this.isActiveArmor);
        tag.putBoolean("offhand", this.isActiveOffhand);
        tag.putBoolean("craft", this.isActiveCraft);

        initServer(this.inventorySize, this.effectSize, this.isActiveInventory, this.isActiveArmor, this.isActiveOffhand, this.isActiveCraft);
        ((IMenuSynchronizer) this.player.containerMenu).initMenu(new PlayerInventorySizeData(this.inventorySize, this.effectSize, this.isActiveInventory, this.isActiveArmor, this.isActiveOffhand, this.isActiveCraft));
        return tag;
    }

    @Override
    public void loadStatus(CompoundNBT tag) {

        if(avoidMiniPouch()) return;

        int effectSize = tag.contains("effectsize") ? tag.getInt("effectsize") : 0;

        int inventorySize;
        if (Config.FORCE_SIZE.get()) {
            inventorySize = Config.MAX_SIZE.get();
        } else if (tag.contains("inventorysize")) {
            int size = tag.getInt("inventorysize");
            if (size > Config.MAX_SIZE.get()) {
                inventorySize = Config.MAX_SIZE.get();
            } else {
                inventorySize = size;
            }
        } else {
            inventorySize = Math.min(Config.DEFAULT_SIZE.get(), Config.MAX_SIZE.get());
        }

        boolean isActiveInventory =
                Config.FORCE_INVENTORY.get() ? Config.DEFAULT_INVENTORY.get() :
                        tag.contains("inventory") ? tag.getBoolean("inventory") : Config.DEFAULT_INVENTORY.get();
        boolean isActiveArmor =
                !Config.FORCE_INVENTORY.get() && (Config.FORCE_ARMOR.get() ? Config.DEFAULT_ARMOR.get() :
                        tag.contains("armor") ? tag.getBoolean("armor") : Config.DEFAULT_ARMOR.get());
        boolean isActiveOffhand =
                Config.FORCE_OFFHAND.get() ? Config.DEFAULT_OFFHAND.get() :
                        tag.contains("offhand") ? tag.getBoolean("offhand") : Config.DEFAULT_OFFHAND.get();
        boolean isActiveCraft =
                !Config.FORCE_INVENTORY.get() && (Config.FORCE_CRAFT.get() ? Config.DEFAULT_CRAFT.get() :
                        tag.contains("craft") ? tag.getBoolean("craft") : Config.DEFAULT_CRAFT.get());

        initServer(inventorySize, effectSize, isActiveInventory, isActiveArmor, isActiveOffhand, isActiveCraft);
        ((IMenuSynchronizer) player.containerMenu).initMenu(new PlayerInventorySizeData(inventorySize, effectSize, isActiveInventory, isActiveArmor, isActiveOffhand, isActiveCraft));
    }
}