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

public class LockableItemStackList extends NonNullList<ItemStack>
{
    //インベントリ枠をロックするためのブール値リスト
    //主にインベントリのスロット数が36を切ったときに使用
    public List<Boolean> lockList = new ArrayList<Boolean>();

    //Todo 変更可能リストを使って追加，削除をもうちょっと自在にやる
    private List<ItemStack> mutableList;

    //Todo inventory<36のとき用に要素ごとにアクセスを制限可能にする
    //Todo メソッドのオーバーロードでアクセスを指定できるようにする

    private boolean stopper = false;



    //ロックされているときにアイテムをぶちまけるため，自身の所属するインベントリの参照を持っておく
    private Inventory inventory;

    //private static final ItemStack defaultItem = new ItemStack(ModRegistry.DUMMY_ITEM::get, 1);
    private static final ItemStack defaultItem = ItemStack.EMPTY;

    private Consumer<ItemStack> observer = new Consumer<ItemStack>() {
        @Override
        public void accept(ItemStack itemStack) {
            System.out.println("called pre");
        }
    };
    private boolean isActivateObserver = false;

    public void setObserver(Consumer<ItemStack> observer)
    {
        this.observer = observer;
        isActivateObserver = true;
        System.out.println("setObserver");
    }

    public static LockableItemStackList create(Inventory inventory, boolean stopper)
    {
        return new LockableItemStackList(Lists.newArrayList(), inventory, stopper);
    }

    public static LockableItemStackList withSize(int p_122781_, Inventory inventory, boolean stopper)
    {
        ItemStack[] aobject = new ItemStack[p_122781_];
        Arrays.fill(aobject, defaultItem);
        return new LockableItemStackList(Arrays.asList(aobject), inventory, stopper);
    }

    @SafeVarargs
    public static LockableItemStackList of(Inventory inventory, boolean stopper, ItemStack... p_122785_)
    {
        return new LockableItemStackList(Arrays.asList(p_122785_), inventory, stopper);
    }

    protected LockableItemStackList(List<ItemStack> p_122777_, Inventory inventory, boolean stopper)
    {
        super(p_122777_, defaultItem);
        this.inventory = inventory;
        this.stopper = stopper;

        for(ItemStack item: p_122777_)
        {
            lockList.add(stopper);
        }
    }

    @Override
    @Nonnull
    public ItemStack get(int p_122791_)
    {
        if(stopper || lockList.get(p_122791_)) return defaultItem;
        return super.get(p_122791_);
    }

    @Override
    public ItemStack set(int p_122795_, ItemStack p_122796_)
    {
        //System.out.println("set to " + p_122795_ + " item " + p_122796_ + " locklist " + lockList.get(p_122795_));
        if(stopper || lockList.get(p_122795_))
        {
            Level level = inventory.player.level;
            Player entity = inventory.player;
                ItemEntity itementity = new ItemEntity(level, entity.getX(), entity.getEyeY() - 0.3, entity.getZ(), p_122796_);
                itementity.setDefaultPickUpDelay();
                itementity.setThrower(entity.getUUID());
                level.addFreshEntity(itementity);
            return defaultItem;
        }
        if(isActivateObserver) this.observer.accept(p_122796_);
        return super.set(p_122795_, p_122796_);
    }

    @Override
    public ItemStack remove(int p_122793_)
    {
        if(stopper || lockList.get(p_122793_)) return defaultItem;
        if(isActivateObserver) this.observer.accept(ItemStack.EMPTY);
        return super.remove(p_122793_);
    }

    public void allLock()
    {
        this.lockList.forEach((i) -> i = false);
    }

    public void allOpen()
    {
        this.lockList.forEach((i) -> i = true);
    }

    public void lock(int target)
    {
        lockList.set(target, false);
    }

    public void open(int target)
    {
        lockList.set(target, true);
    }

}
