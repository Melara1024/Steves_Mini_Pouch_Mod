package ga.melara.stevesminipouch.util;

import com.google.common.collect.Lists;
import ga.melara.stevesminipouch.ModRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LockableItemStackList extends NonNullList<ItemStack> {

    private List<ItemStack> mutableList;

    private final Inventory inventory;

    private static final ItemStack defaultItem = ItemStack.EMPTY;

    private Consumer<ItemStack> observer = itemStack -> {
    };

    private boolean isActivateObserver = false;

    public void setObserver(Consumer<ItemStack> observer) {
        this.observer = observer;
        isActivateObserver = true;
    }


    public List<Boolean> lockList = new ArrayList<Boolean>() {
        @Override
        public Boolean set(int index, Boolean element) {
            if(element && Objects.nonNull(inventory)) {
                ItemStack itemStack = LockableItemStackList.this.get(index);
                throwItem(inventory.player, itemStack);
                LockableItemStackList.this.set(index, ItemStack.EMPTY);
            }
            return super.set(index, element);
        }
    };

    public void allLock() {
        for(int i = 0; i < this.lockList.size(); i++) {
            this.lockList.set(i, true);
        }
    }

    public void allOpen() {
        for(int i = 0; i < this.lockList.size(); i++) {
            this.lockList.set(i, false);
        }
    }

    public void lock(int target) {
        lockList.set(target, true);
    }

    public void open(int target) {
        lockList.set(target, false);
    }


    public static LockableItemStackList create(Inventory inventory, boolean stopper) {
        return new LockableItemStackList(Lists.newArrayList(), inventory, stopper);
    }

    public static LockableItemStackList withSize(int size, Inventory inventory, boolean stopper) {
        ItemStack[] aobject = new ItemStack[size];
        Arrays.fill(aobject, defaultItem);
        return new LockableItemStackList(Arrays.asList(aobject), inventory, stopper);
    }

    @SafeVarargs
    public static LockableItemStackList of(Inventory inventory, boolean stopper, ItemStack... itemArray) {
        return new LockableItemStackList(Arrays.asList(itemArray), inventory, stopper);
    }

    protected LockableItemStackList(List<ItemStack> itemList, Inventory inventory, boolean initLock) {
        super(itemList, defaultItem);
        this.inventory = inventory;

        for(ItemStack item : itemList) {
            lockList.add(initLock);
        }
    }


    @Override
    @Nonnull
    public ItemStack get(int id) {
        if(id > this.size()-1) return defaultItem;
        if(lockList.get(id)) return defaultItem;
        return super.get(id);
    }

    @Override
    public ItemStack set(int id, ItemStack itemStack) {
        if(id > this.size()-1) throwItem(inventory.player, itemStack);
        // If the slot is locked, throw the item in its place.
        if(lockList.get(id)) {
            throwItem(inventory.player, itemStack);
            return defaultItem;
        }

        ItemStack result = super.set(id, itemStack);
        if(isActivateObserver) this.observer.accept(itemStack);
        return result;
    }

    @Override
    public ItemStack remove(int id) {
        if(id > this.size()-1) return defaultItem;
        if(lockList.get(id)) return defaultItem;
        ItemStack result = super.remove(id);
        if(isActivateObserver) this.observer.accept(ItemStack.EMPTY);
        return result;
    }

    private void throwItem(Player player, ItemStack itemStack)
    {
        Level level = inventory.player.level;
        Player entity = inventory.player;
        ItemEntity itementity = new ItemEntity(level, entity.getX(), entity.getEyeY() - 0.3, entity.getZ(), itemStack);
        itementity.setDefaultPickUpDelay();
        itementity.setThrower(entity.getUUID());
        level.addFreshEntity(itementity);
    }
}
