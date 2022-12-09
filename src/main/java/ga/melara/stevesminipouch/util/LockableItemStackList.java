package ga.melara.stevesminipouch.util;

import com.google.common.collect.Lists;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public class LockableItemStackList extends NonNullList<ItemStack> {

    private final Inventory inventory;

    private static final ItemStack defaultItem = ItemStack.EMPTY;

    private BiConsumer<Integer, ItemStack> observer = (itemStack, slot) -> {
    };

    private boolean isActivateObserver = false;

    public void setObserver(BiConsumer<Integer, ItemStack> observer) {
        this.observer = observer;
        isActivateObserver = true;
    }


    public List<Boolean> lockList;

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

        lockList = new ArrayList<>() {
            @Override
            public Boolean set(int index, Boolean element) {
                if(element && Objects.nonNull(inventory)) {
                    ItemStack itemStack = LockableItemStackList.this.get(index);
                    throwItem(itemStack);
                    LockableItemStackList.this.set(index, ItemStack.EMPTY);
                }
                return super.set(index, element);
            }
        };

        for(int i = 0; i< itemList.size(); i++) lockList.add(initLock);
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
        // If the slot is locked, throw the item in its place.
        if(id > this.size()-1 || lockList.get(id)) {
            throwItem(itemStack);
            return defaultItem;
        }

        ItemStack result = super.set(id, itemStack);
        if(isActivateObserver) this.observer.accept(id, itemStack);
        return result;
    }

    @Override
    public ItemStack remove(int id) {
        if(id > this.size()-1) return defaultItem;
        if(lockList.get(id)) return defaultItem;
        ItemStack result = super.remove(id);
        if(isActivateObserver) this.observer.accept(id, ItemStack.EMPTY);
        return result;
    }

    private void throwItem(ItemStack itemStack)
    {
        if(Objects.isNull(inventory) || Objects.isNull(inventory.player) || Objects.isNull(inventory.player.level)) return;
        Level level = inventory.player.level;
        ItemEntity itementity = new ItemEntity(level, inventory.player.getX(), inventory.player.getEyeY() - 0.3, inventory.player.getZ(), itemStack);
        itementity.setDefaultPickUpDelay();
        itementity.setThrower(inventory.player.getUUID());
        level.addFreshEntity(itementity);
    }
}
