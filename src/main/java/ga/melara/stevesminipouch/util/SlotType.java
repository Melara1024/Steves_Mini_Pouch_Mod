package ga.melara.stevesminipouch.util;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;


public enum SlotType {
    INVENTORY,
    HOTBAR,
    OFFHAND,
    ARMOR,
    CRAFT,
    RESULT,
    UNDEFINED;


    public static void setType(Slot targetSlot) {
        if(targetSlot.container instanceof PlayerInventory) {
            PlayerInventory inventory = (PlayerInventory) targetSlot.container;

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
        if(targetSlot.container instanceof CraftingInventory) {
            if(((CraftingInventory) targetSlot.container).menu instanceof Inventory) {
                ((IHasSlotType) targetSlot).setType(SlotType.CRAFT);
            }
        }
        if(targetSlot instanceof CraftingResultSlot) {
            ((IHasSlotType) targetSlot).setType(SlotType.RESULT);
        }
    }
}
