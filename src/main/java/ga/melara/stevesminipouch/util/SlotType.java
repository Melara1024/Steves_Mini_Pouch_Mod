package ga.melara.stevesminipouch.util;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;

public enum SlotType {
    INVENTORY,
    HOTBAR,
    OFFHAND,
    ARMOR,
    CRAFT,
    RESULT,
    UNDEFINED;


    public static void setType(Slot targetSlot) {
        if(targetSlot.container instanceof Inventory inventory) {

            if (!((IInheritGuard)inventory).avoidMiniPouch()){
                if(targetSlot.getSlotIndex() >= 0 && targetSlot.getSlotIndex() < 9) {
                    ((IHasSlotType) targetSlot).setType(SlotType.HOTBAR);
                }
                if(targetSlot.getSlotIndex() >= 9 && targetSlot.getSlotIndex() < 36) {
                    ((IHasSlotType) targetSlot).setType(SlotType.INVENTORY);
                }
                if(targetSlot.getSlotIndex() >= 36 && targetSlot.getSlotIndex() < 40) {
                    ((IHasSlotType) targetSlot).setType(SlotType.ARMOR);
                }
                if(targetSlot.getSlotIndex() == 40) {
                    ((IHasSlotType) targetSlot).setType(SlotType.OFFHAND);
                }
            }
        }
        if(targetSlot.container instanceof CraftingContainer) {
            if(((CraftingContainer) targetSlot.container).menu instanceof InventoryMenu) {
                ((IHasSlotType) targetSlot).setType(SlotType.CRAFT);
            }
        }
        if(targetSlot instanceof ResultSlot) {
            ((IHasSlotType) targetSlot).setType(SlotType.RESULT);
        }
    }
}
