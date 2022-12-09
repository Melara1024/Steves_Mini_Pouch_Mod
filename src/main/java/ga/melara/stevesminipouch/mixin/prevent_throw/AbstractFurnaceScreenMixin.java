package ga.melara.stevesminipouch.mixin.prevent_throw;

import ga.melara.stevesminipouch.util.IHasPageButton;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceScreen.class)
public class AbstractFurnaceScreenMixin {

    @Inject(method = "hasClickedOutside(DDIII)Z", at = @At(value = "HEAD"), cancellable = true)
    protected void onClickedOutside(double mouseX, double mouseY, int leftPos, int rightPos, int ___, CallbackInfoReturnable<Boolean> cir) {
        ((IHasPageButton) this).buttonClicked(mouseX, mouseY, leftPos, rightPos, cir);
    }
}
