package ga.melara.stevesminipouch.util;

import com.google.common.collect.Lists;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;


public class LockableItemStackList extends NonNullList<ItemStack> {

    private List<ItemStack> mutableList;


    // このバージョンではプレイヤーをインベントリから取得できない
    private final PlayerInventory inventory;

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
                World level = inventory.player.level;
                PlayerEntity entity = inventory.player;
                ItemStack item = LockableItemStackList.this.get(index);
                ItemEntity itementity = new ItemEntity(level, entity.getX(), entity.getEyeY() - 0.3, entity.getZ(), item);
                itementity.setDefaultPickUpDelay();
                itementity.setThrower(entity.getUUID());
                level.addFreshEntity(itementity);
                System.out.println("locklist stash!!");
                System.out.println(item.getItem());
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


    public static LockableItemStackList create(PlayerInventory inventory, boolean stopper) {
        return new LockableItemStackList(Lists.newArrayList(), inventory, stopper);
    }

    public static LockableItemStackList withSize(int size, PlayerInventory inventory, boolean stopper) {
        ItemStack[] aobject = new ItemStack[size];
        Arrays.fill(aobject, defaultItem);
        return new LockableItemStackList(Arrays.asList(aobject), inventory, stopper);
    }

    @SafeVarargs
    public static LockableItemStackList of(PlayerInventory inventory, boolean stopper, ItemStack... itemArray) {
        return new LockableItemStackList(Arrays.asList(itemArray), inventory, stopper);
    }

    protected LockableItemStackList(List<ItemStack> itemList, PlayerInventory inventory, boolean initLock) {
        super(itemList, defaultItem);
        this.inventory = inventory;

        for(ItemStack item : itemList) {
            lockList.add(initLock);
        }
    }


    @Override
    @Nonnull
    public ItemStack get(int id) {
        if(lockList.get(id)) return defaultItem;
        return super.get(id);
    }

    @Override
    public ItemStack set(int id, ItemStack itemStack) {
        // If the slot is locked, throw the item in its place.
        if(lockList.get(id)) {

            System.out.println("stash!!");
            System.out.println(itemStack.getItem());

            World level = inventory.player.level;
            PlayerEntity entity = inventory.player;
            ItemEntity itementity = new ItemEntity(level, entity.getX(), entity.getEyeY() - 0.3, entity.getZ(), itemStack);
            itementity.setDefaultPickUpDelay();
            itementity.setThrower(entity.getUUID());
            level.addFreshEntity(itementity);
            return defaultItem;
        }

        ItemStack result = super.set(id, itemStack);
        if(isActivateObserver) this.observer.accept(itemStack);
        return result;
    }

    @Override
    public ItemStack remove(int id) {
        if(lockList.get(id)) return defaultItem;
        ItemStack result = super.remove(id);
        if(isActivateObserver) this.observer.accept(ItemStack.EMPTY);
        return result;
    }
}
