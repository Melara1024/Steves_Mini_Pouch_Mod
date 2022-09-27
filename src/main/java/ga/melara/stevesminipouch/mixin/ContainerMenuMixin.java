package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.IContainerAccessWidener;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerMenu.class)
public class ContainerMenuMixin implements IContainerAccessWidener {

    @Inject(method = "addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;", at = @At(value = "HEAD"),cancellable=true)
    public void disableVanillaSlot(Slot p_38898_, CallbackInfoReturnable<Slot> cir)
    {
        this.removeSlot();
        System.out.println("(from ContMenuMixin!)");
    }

    @Override
    public void removeSlot() {
        System.out.println("aaaaaaaaaaaaaaaaaa");
        //スロットのリセット用関数
    }
}
