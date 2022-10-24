package ga.melara.stevesminipouch.util;

import net.minecraft.world.Container;
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
    OTHER,

    UNDEFINED;

    public static void setHiding(Slot target)
    {
        Container container = target.container;
        int page = ((IHasSlotPage)target).getPage();
        SlotType type = ((IHasSlotType)target).getType();
        int slot = target.getSlotIndex();

        if(type == SlotType.INVENTORY && page>0)
        {
            if(slot + 27*page < ((IStorageChangable)container).getInventorySize()
                    && ((IStorageChangable)container).isValidSlot(slot + 27*page + 5))
            {
                ((ISlotHidable)target).show();
            }
            else
            {
                ((ISlotHidable)target).hide();
            }
        }
        if(type == SlotType.HOTBAR)
        {
            if(((IStorageChangable)container).isValidSlot(slot)) {
                ((ISlotHidable)target).show();
            }
            else ((ISlotHidable)target).hide();
        }
        if(type == SlotType.ARMOR)
        {
            if(((IStorageChangable)container).isActiveArmor()) {
                ((ISlotHidable)target).show();
            }
            else ((ISlotHidable)target).hide();
        }
        if(type == SlotType.OFFHAND)
        {
            if(((IStorageChangable)container).isActiveOffhand()) {
                ((ISlotHidable)target).show();
            }
            else ((ISlotHidable)target).hide();
        }
        if(type == SlotType.CRAFT)
        {

        }
        if(type == SlotType.RESULT)
        {

        }
    }

    public static void setType(Slot targetSlot)
    {
        //System.out.println(targetSlot.container.toString());
        if(targetSlot.container instanceof Inventory)
        {
            if(targetSlot.getSlotIndex() >= 0 && targetSlot.getSlotIndex() < 9)
            {
                ((IHasSlotType)targetSlot).setType(SlotType.HOTBAR);
            }
            if(targetSlot.getSlotIndex() >= 9 && targetSlot.getSlotIndex() < 36)
            {
                ((IHasSlotType)targetSlot).setType(SlotType.INVENTORY);
                ((IHasSlotPage)targetSlot).setPage(0);
            }
            if(targetSlot.getSlotIndex() >= 36 && targetSlot.getSlotIndex() < 40)
            {
                ((IHasSlotType)targetSlot).setType(SlotType.ARMOR);
            }
            if(targetSlot.getSlotIndex() == 40)
            {
                ((IHasSlotType)targetSlot).setType(SlotType.OFFHAND);
            }
        }
        if(targetSlot.container instanceof CraftingContainer)
        {
            if(((CraftingContainer) targetSlot.container).menu instanceof InventoryMenu)
            {
                //2x2クラフティング系のスロットの場合
                ((IHasSlotType)targetSlot).setType(SlotType.CRAFT);
            }
        }
        if(targetSlot instanceof ResultSlot)
        {
            //クラフティングの完成品スロットの場合
            ((IHasSlotType)targetSlot).setType(SlotType.RESULT);
        }

    }
}
