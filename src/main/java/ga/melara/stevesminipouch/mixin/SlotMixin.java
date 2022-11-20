package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.util.*;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

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
    public Container container;


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
            if(this.slot + (27 * page) < ((IStorageChangable) container).getInventorySize()) {
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
        Slot target = (Slot)(Object)this;

        Container container = target.container;
        int page = ((IHasSlotPage) target).getPage();
        SlotType type = ((IHasSlotType) target).getType();
        int slot = target.getSlotIndex();

        if(container instanceof IStorageChangable inventory) {
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
        } else if(container instanceof ICraftingContainerChangable craftingContainer) {
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

    @Inject(method = "mayPlace(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    public void onCallMayPlace(CallbackInfoReturnable<Boolean> cir) {
        if(!this.isShowing()) cir.setReturnValue(false);
    }

    @Inject(method = "initialize(Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    public void onInitialize(ItemStack p_40240_, CallbackInfo ci) {
        if(this.type == SlotType.INVENTORY && page > 0) {
            if(this.slot + 27 * page < ((IStorageChangable) container).getInventorySize()) {
                this.show();
                this.container.setItem(this.slot + 27 * page + 5, p_40240_);
                this.setChanged();
                ci.cancel();
            } else {
                this.hide();
                ci.cancel();
            }
        }
        if(this.type == SlotType.HOTBAR) {
            if(((IStorageChangable) container).isValidSlot(this.slot)) {
                this.show();
            } else this.hide();
        }
        if(this.type == SlotType.ARMOR) {
            if(((IStorageChangable) container).isActiveArmor()) {
                this.show();
            } else this.hide();
        }
        if(this.type == SlotType.OFFHAND) {
            if(((IStorageChangable) container).isActiveOffhand()) {
                this.show();
            } else this.hide();
        }
    }

    @Inject(method = "set(Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    public void onSetItem(ItemStack p_40240_, CallbackInfo ci) {
        if(this.type == SlotType.INVENTORY && page > 0) {
            if(this.slot + 27 * page < ((IStorageChangable) container).getInventorySize()) {
                this.show();
                this.container.setItem(this.slot + 27 * page + 5, p_40240_);
                this.setChanged();
                ci.cancel();
            } else {
                this.hide();
                ci.cancel();
            }
        }
        if(this.type == SlotType.HOTBAR) {
            if(((IStorageChangable) container).isValidSlot(this.slot)) {
                this.show();
            } else this.hide();
        }
    }

    @Inject(method = "getItem()Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    public void onGetItem(CallbackInfoReturnable<ItemStack> cir) {
        if(this.type == SlotType.INVENTORY && page > 0) {
            if(this.slot + 27 * page < ((IStorageChangable) container).getInventorySize()) {
                this.show();
                cir.setReturnValue(this.container.getItem(this.slot + 27 * page + 5));
            } else {
                this.hide();
                cir.setReturnValue(ItemStack.EMPTY);
            }
        }
        if(this.type == SlotType.HOTBAR) {
            if(((IStorageChangable) container).isValidSlot(this.slot)) {
                this.show();
            } else this.hide();
        }
    }

    @Inject(method = "remove(I)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    public void onRemoveItem(int p_40227_, CallbackInfoReturnable<ItemStack> cir) {
        if(this.type == SlotType.INVENTORY && page > 0) {
            if(this.slot + 27 * page < ((IStorageChangable) container).getInventorySize()) {
                this.show();
                cir.setReturnValue(this.container.removeItem(this.slot + 27 * page + 5, p_40227_));
            } else {
                this.hide();
                cir.setReturnValue(ItemStack.EMPTY);
            }
        }
        if(this.type == SlotType.HOTBAR) {
            if(((IStorageChangable) container).isValidSlot(this.slot)) {
                this.show();
            } else this.hide();
        }
    }
}
