package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.util.IStorageChangable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin
{

    @Shadow
    public LocalPlayer player;

    @Inject(method = "handleKeybinds()V", at = @At(value = "RETURN"), cancellable = true)
    public void onGetSuitableHotbarSlot(CallbackInfo ci)
    {
        if(this.player.getInventory().selected > ((IStorageChangable)this.player.getInventory()).getInventorySize()-1)
        this.player.getInventory().selected = ((IStorageChangable)this.player.getInventory()).getInventorySize()-1;
    }
}
