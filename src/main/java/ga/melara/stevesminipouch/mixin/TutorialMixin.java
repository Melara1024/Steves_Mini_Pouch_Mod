package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.util.ICustomInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.tutorial.Tutorial;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Tutorial.class)
public class TutorialMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "onOpenInventory", at = @At("HEAD"), cancellable = true)
    public void onOpenInventory(CallbackInfo ci) {
        if(!((ICustomInventory) minecraft.player.getInventory()).isActiveInventory()) ci.cancel();
    }
}
