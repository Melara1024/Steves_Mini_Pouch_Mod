package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.util.*;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class SlotMixin implements IHasSlotType, IHasSlotPage, ISlotHidable {

    public SlotType type = SlotType.UNDEFINED;


    private int page = 0;

    @Final
    @Shadow
    @Mutable
    private int slot;

    @Final
    @Shadow
    @Mutable
    public IInventory container;


    @Shadow
    public void setChanged() {
    }

    private boolean isShowing = true;


    @Override
    public void setType(SlotType type) {
        this.type = type;
    }

    @Override
    public SlotType getType() {
        return this.type;
    }

    @Override
    public void setPage(int page) {
        this.page = page;
        if(this.type == SlotType.INVENTORY) {
            if(this.slot + (27 * page) < ((ICustomInventory) container).getInventorySize()) {
                show();
            } else {
                hide();
            }
        }
    }

    @Override
    public int getPage() {
        return this.page;
    }

    @Override
    public void hide() {
        isShowing = false;
    }

    @Override
    public void show() {
        isShowing = true;
    }

    @Override
    public boolean isShowing() {
        return this.isShowing;
    }

    @Override
    public void setHiding() {
        Slot target = (Slot) (Object) this;

        IInventory container = target.container;
        int page = ((IHasSlotPage) target).getPage();
        SlotType type = ((IHasSlotType) target).getType();
        int slot = target.getSlotIndex();

        if(container instanceof ICustomInventory) {
            ICustomInventory inventory = (ICustomInventory) container;
            if(type == SlotType.INVENTORY) {
                if(slot + 27 * page < inventory.getInventorySize()) {
                    ((ISlotHidable) target).show();
                } else {
                    ((ISlotHidable) target).hide();
                }
            }
            if(type == SlotType.HOTBAR) {
                if(slot < inventory.getInventorySize()) {
                    ((ISlotHidable) target).show();
                } else ((ISlotHidable) target).hide();
            }
            if(type == SlotType.ARMOR) {
                if(inventory.isActiveArmor()) {
                    ((ISlotHidable) target).show();
                } else ((ISlotHidable) target).hide();
            }
            if(type == SlotType.OFFHAND) {
                if(inventory.isActiveOffhand()) {
                    ((ISlotHidable) target).show();
                } else ((ISlotHidable) target).hide();
            }
        } else if(container instanceof ICraftingContainerChangable) {
            ICraftingContainerChangable craftingContainer = (ICraftingContainerChangable) container;
            if(type == SlotType.CRAFT) {
                if(craftingContainer.isActivateCraft()) {
                    ((ISlotHidable) target).show();
                } else ((ISlotHidable) target).hide();
            }
        }
    }

    @Inject(method = "isActive()Z", at = @At("HEAD"), cancellable = true)
    public void onCallIsActive(CallbackInfoReturnable<Boolean> cir) {
        if(!this.isShowing()) cir.setReturnValue(false);
    }

    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    public void onCallMayPlace(CallbackInfoReturnable<Boolean> cir) {
        if(!this.isShowing()) cir.setReturnValue(false);
    }

    @Inject(method = "set", at = @At("HEAD"), cancellable = true)
    public void onSetItem(ItemStack itemStack, CallbackInfo ci) {
        if(this.type == SlotType.INVENTORY && page > 0) {
            if(this.slot + 27 * page < ((ICustomInventory) container).getInventorySize()) {
                this.show();
                this.container.setItem(this.slot + 27 * page + 5, itemStack);
                this.setChanged();
                ci.cancel();
            } else {
                this.hide();
                ci.cancel();
            }
        }
        if(this.type == SlotType.HOTBAR) {
            if(((ICustomInventory) container).isValidSlot(this.slot)) {
                this.show();
            } else this.hide();
        }
    }

    @Inject(method = "getItem", at = @At("HEAD"), cancellable = true)
    public void onGetItem(CallbackInfoReturnable<ItemStack> cir) {
        if(this.type == SlotType.INVENTORY && page > 0) {
            if(this.slot + 27 * page < ((ICustomInventory) container).getInventorySize()) {
                this.show();
                cir.setReturnValue(this.container.getItem(this.slot + 27 * page + 5));
            } else {
                this.hide();
                cir.setReturnValue(ItemStack.EMPTY);
            }
        }
        if(this.type == SlotType.HOTBAR) {
            if(((ICustomInventory) container).isValidSlot(this.slot)) {
                this.show();
            } else this.hide();
        }
    }

    @Inject(method = "remove", at = @At("HEAD"), cancellable = true)
    public void onRemoveItem(int p_40227_, CallbackInfoReturnable<ItemStack> cir) {
        if(this.type == SlotType.INVENTORY && page > 0) {
            if(this.slot + 27 * page < ((ICustomInventory) container).getInventorySize()) {
                this.show();
                cir.setReturnValue(this.container.removeItem(this.slot + 27 * page + 5, p_40227_));
            } else {
                this.hide();
                cir.setReturnValue(ItemStack.EMPTY);
            }
        }
        if(this.type == SlotType.HOTBAR) {
            if(((ICustomInventory) container).isValidSlot(this.slot)) {
                this.show();
            } else this.hide();
        }
    }
}
