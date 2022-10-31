package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.data.ClientInventoryData;
import net.minecraft.client.tutorial.Tutorial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Tutorial.class)
public class TutorialMixin
{
    @Inject(method = "onOpenInventory", at= @At("HEAD"), cancellable = true)
    public void onOpenInventory(CallbackInfo ci)
    {
        if(!ClientInventoryData.isActiveInventory())ci.cancel();
    }
}
