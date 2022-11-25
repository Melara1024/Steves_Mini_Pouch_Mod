package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.Config;
import ga.melara.stevesminipouch.util.ICraftingContainerChangable;
import ga.melara.stevesminipouch.util.LockableItemStackList;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;

@Mixin(CraftingInventory.class)
public class CraftingContainerMixin implements ICraftingContainerChangable {

    private boolean isActiveCraft = Config.DEFAULT_CRAFT.get();

    @Shadow
    @Final
    @Mutable
    private NonNullList<ItemStack> items;

    @Shadow
    @Final
    @Mutable
    private Container menu;

    @Override
    public void setCraft(boolean isActiveCraft, PlayerEntity player) {
        if(!(menu instanceof PlayerContainer)) return;

        if(!isActiveCraft) {
            // Handling of loss of crafting ability when an item is present in a crafting slot.
            for(ItemStack item : items) {
                World level = player.level;
                ItemEntity itementity = new ItemEntity(level, player.getX(), player.getEyeY() - 0.3, player.getZ(), item);
                itementity.setDefaultPickUpDelay();
                itementity.setThrower(player.getUUID());
                level.addFreshEntity(itementity);
            }
            if(items instanceof LockableItemStackList) {
                LockableItemStackList lockable = (LockableItemStackList) items;
                lockable.allLock();
            }
            else
                items = LockableItemStackList.withSize(4, ((PlayerContainer) menu).owner.getInventory(), true);
            this.isActiveCraft = false;
            return;
        }
        if(items instanceof LockableItemStackList){
            LockableItemStackList lockable = (LockableItemStackList) items;
            lockable.allOpen();
        }
        else
            items = LockableItemStackList.withSize(4, ((PlayerContainer) menu).owner.getInventory(), false);
        this.isActiveCraft = true;
    }

    @Override
    public boolean isActivateCraft() {
        return this.isActiveCraft;
    }
}
