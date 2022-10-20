package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.Config;
import ga.melara.stevesminipouch.util.ICraftingContainerChangable;
import ga.melara.stevesminipouch.util.LockableItemStackList;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
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

    //Todo アイテムリストをLockableItemStackListに置き換え
    //Todo ロック用のメソッドをダックインターフェースで実装する

    private boolean isActiveCraft = Config.DEFAULT_CRAFT.get();

    @Shadow
    @Final
    @Mutable
    private NonNullList<ItemStack> items;

    @Shadow
    public AbstractContainerMenu menu;


    @Override
    public void toggleCraft(Player player) {
        if(!(((CraftingContainer) (Object) this).menu instanceof  InventoryMenu))return;


        if (this.isActiveCraft) {
            for (ItemStack item : items) {
                Level level = player.level;
                ItemEntity itementity = new ItemEntity(level, player.getX(), player.getEyeY() - 0.3, player.getZ(), item);
                itementity.setDefaultPickUpDelay();
                itementity.setThrower(player.getUUID());
                level.addFreshEntity(itementity);
            }


            items = LockableItemStackList.withSize(4, ((InventoryMenu)((CraftingContainer) (Object) this).menu).owner.getInventory(), true);

            this.isActiveCraft = false;
            return;
        }

        items = LockableItemStackList.withSize(4, ((InventoryMenu)((CraftingContainer) (Object) this).menu).owner.getInventory(), false);

        this.isActiveCraft = true;

        player.sendSystemMessage(Component.literal("Offhand Toggled!"));
    }

}
