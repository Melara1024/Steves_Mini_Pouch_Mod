package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.stats.ClientInventoryData;
import ga.melara.stevesminipouch.util.IStorageChangable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
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

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    public void onSetScreen(Screen p_91153_, CallbackInfo ci)
    {
        if(p_91153_ instanceof InventoryScreen && !ClientInventoryData.isActiveInventory())ci.cancel();
    }
}
