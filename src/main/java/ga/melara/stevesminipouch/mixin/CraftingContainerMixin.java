package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.Config;
import ga.melara.stevesminipouch.util.ICraftingContainerChangable;
import ga.melara.stevesminipouch.util.LockableItemStackList;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CraftingContainer.class)
public class CraftingContainerMixin implements ICraftingContainerChangable {

    private boolean isActiveCraft = Config.DEFAULT_CRAFT.get();

    @Shadow
    @Final
    @Mutable
    private NonNullList<ItemStack> items;

    @Shadow
    @Mutable
    public AbstractContainerMenu menu;

    @Override
    public void setCraft(boolean isActiveCraft, Player player) {
        if(!(menu instanceof InventoryMenu)) return;

        if(!isActiveCraft) {
            // Handling of loss of crafting ability when an item is present in a crafting slot.
            for(ItemStack item : items) {
                Level level = player.level;
                ItemEntity itementity = new ItemEntity(level, player.getX(), player.getEyeY() - 0.3, player.getZ(), item);
                itementity.setDefaultPickUpDelay();
                itementity.setThrower(player.getUUID());
                level.addFreshEntity(itementity);
            }
            if(items instanceof LockableItemStackList lockable) lockable.allLock();
            else
                    items = LockableItemStackList.withSize(4, ((InventoryMenu)menu).owner.getInventory(), true);
            this.isActiveCraft = false;
            return;
        }
        if(items instanceof LockableItemStackList lockable) lockable.allOpen();
        else
            items = LockableItemStackList.withSize(4, ((InventoryMenu)menu).owner.getInventory(), false);
        this.isActiveCraft = true;
    }

    @Override
    public boolean isActivateCraft() {
        return this.isActiveCraft;
    }
}
