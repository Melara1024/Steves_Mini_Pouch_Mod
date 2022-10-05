package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.Config;
import ga.melara.stevesminipouch.util.IHasSlotType;
import ga.melara.stevesminipouch.util.SlotType;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerMenu.class)
public abstract class ContainerMenuMixin {

    @Shadow public abstract void slotsChanged(Container p_38868_);

    @Shadow protected abstract Slot addSlot(Slot p_38898_);

    @Shadow public abstract int incrementStateId();

    @Shadow NonNullList<Slot> slots;

    @Inject(method = "addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;", at = @At("HEAD"), cancellable = true)
    public void onAddSlot(Slot slot, CallbackInfoReturnable<Slot> cir)
    {
        //System.out.println("onAddSlot");
        //if(addAdditionalSlots(slot)) cir.setReturnValue(slot);
        slot.index = this.slots.size();
        this.slots.add(slot);
        setType(slot);
        cir.setReturnValue(slot);
    }

    //todo  very dirty. must be rewrite.

    public NonNullList<Slot> copyOfInventory = NonNullList.create();

    private boolean addAdditionalSlots(Slot slot)
    {
        if(slot.container instanceof Inventory)
        {
            if(slot.getSlotIndex() >= 9 && slot.getSlotIndex() < 36)
            {

                int maxpage = (int)(Math.floor((Config.MAX_SIZE.get()-9) / 27));
                maxpage = 1;
                System.out.println("maxpage is " + maxpage);

                for(int i=0; i<=maxpage; i++)
                {
                    Slot additionalSlot = copySlot(slot, i==0 ? 0 : Config.ADDITIONAL_INVENTORY_INDEX.get()+i);
                    additionalSlot.index = ((AbstractContainerMenu)(Object)this).slots.size() + (i==0 ? 0:41);
                    ((AbstractContainerMenu)(Object)this).slots.add(additionalSlot);
                    ((AbstractContainerMenu)(Object)this).lastSlots.add(new ItemStack(Items.DIAMOND_BLOCK, 1));
                    ((AbstractContainerMenu)(Object)this).remoteSlots.add(new ItemStack(Items.DIAMOND_BLOCK, 1));
                    additionalSlot.set(new ItemStack(Items.DIAMOND_BLOCK));
                    ((IHasSlotType)additionalSlot).setType(SlotType.INVENTORY);
                    ((IHasSlotType)additionalSlot).setPage(i);


                    System.out.println("========================================");
                    System.out.println("slot pos = " + additionalSlot.x + ", " + additionalSlot.y);
                    System.out.println("index = " + additionalSlot.getSlotIndex());
                    System.out.println("page = " + ((IHasSlotType)additionalSlot).getPage());

                }
                return true;
            }
        }
        return false;
    }

    private Slot copySlot(Slot slot, int slotOffset)
    {
        return new Slot(slot.container, slot.getSlotIndex() + slotOffset, slot.x, slot.y);
    }


    //スロットはサーバーの最大数分作っておく，アクセスの制限はクライアント側のスロットで行う
    private void setType(Slot targetSlot)
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
                ((IHasSlotType)targetSlot).setPage(0);
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
