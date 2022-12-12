package ga.melara.stevesminipouch.util;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public interface IHasPageButton {

    void buttonClicked(double mouseX, double mouseY, int leftPos, int RightPos, CallbackInfoReturnable<Boolean> cir);
}
