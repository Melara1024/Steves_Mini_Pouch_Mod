package ga.melara.stevesminipouch.mixin.prevent_throw;

import ga.melara.stevesminipouch.util.IHasPageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceScreen.class)
public class AbstractFurnaceScreenMixin {

    @Inject(method = "hasClickedOutside(DDIII)Z", at = @At(value = "HEAD"), cancellable = true)
    protected void onClickedOutside(double mouseX, double mouseY, int leftPos, int RightPos, int p_97761_, CallbackInfoReturnable<Boolean> cir)
    {
        int x = ((IHasPageButton)(Object)this).getButtonX();
        int y = ((IHasPageButton)(Object)this).getButtonY();

        int w = ((IHasPageButton)(Object)this).getButtonWidth();
        int h = ((IHasPageButton)(Object)this).getButtonHeight();

        if(mouseX < x+w && mouseX > x && mouseY < y+h && mouseY > y)
        {
            cir.setReturnValue(false);
        }
    }
}
