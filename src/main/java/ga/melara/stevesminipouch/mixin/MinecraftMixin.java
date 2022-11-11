package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.stats.ClientInventoryData;
import ga.melara.stevesminipouch.util.IStorageChangable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow
    public LocalPlayer player;

    @Inject(method = "handleKeybinds()V", at = @At(value = "RETURN"), cancellable = true)
    public void onGetSuitableHotbarSlot(CallbackInfo ci) {
        if(this.player.getInventory().selected > ((IStorageChangable) this.player.getInventory()).getInventorySize() - 1)
            this.player.getInventory().selected = ((IStorageChangable) this.player.getInventory()).getInventorySize() - 1;
    }

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    public void onSetScreen(Screen p_91153_, CallbackInfo ci) {
        if(p_91153_ instanceof InventoryScreen && !ClientInventoryData.isActiveInventory()) {

            ItemStack main = player.getInventory().items.get(0);
            ItemStack offhand = player.getInventory().offhand.get(0);
            String mainItem = main.getItem() == Items.AIR ? "nothing" : main.getItem().toString();
            String offhandItem = offhand.getItem() == Items.AIR ? "nothing" : offhand.getItem().toString();

            if(player.getLevel().isClientSide()) player.sendSystemMessage(Component.literal(String.format(
                    "You looked at your hand.\n %s in your main hand\n %s in your off hand.", mainItem, offhandItem)));
            ci.cancel();
        }
    }
}
